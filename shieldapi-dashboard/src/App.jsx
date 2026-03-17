import React, { useState, useEffect, useMemo, useCallback } from 'react';
import {
  Shield, Activity, Zap, Lock, AlertTriangle,
  Globe, Terminal, Server, RefreshCcw, Filter, Eye,
  WifiOff, CheckCircle2, Timer, Target
} from 'lucide-react';
import {
  ResponsiveContainer, AreaChart, Area,
  XAxis, YAxis, CartesianGrid, Tooltip
} from 'recharts';
import { motion, AnimatePresence } from 'framer-motion';
import { useSecurityEvents } from './hooks/useSecurityEvents';
import {
  getDashboardMetrics,
  getRecentAttacks,
  getTopOffenders,
  getTrafficData,
  getToken,
  logout
} from './services/api';
import Login from './components/Login';

// ─────────────────────────────────────────────────
//  STATIC POOLS (simulation / fallback)
// ─────────────────────────────────────────────────
const SIM_TYPES = [
  'SQL_INJECTION','XSS_PAYLOAD','AUTH_BRUTE_FORCE','RECON_SCAN',
  'DDOS_FLOOD','CSRF_ATTACK','TOR_NODE','HONEYPOT_HIT','JWT_TAMPER','RATE_LIMIT',
];
const SIM_SEVS  = ['critical','critical','high','high','high','medium','medium','low'];
const SIM_POOL  = [
  { ip:'185.220.101.47', cc:'DE', asn:'TOR Exit Node',      lat:51.2,  lng:6.8   },
  { ip:'193.32.162.157', cc:'RU', asn:'Selectel LLC',       lat:59.9,  lng:30.3  },
  { ip:'91.108.4.67',    cc:'PL', asn:'DigitalOcean',       lat:52.2,  lng:21.0  },
  { ip:'45.130.228.91',  cc:'NL', asn:'Frantech Solutions', lat:52.4,  lng:4.9   },
  { ip:'103.148.64.11',  cc:'CN', asn:'Alibaba Cloud',      lat:39.9,  lng:116.4 },
  { ip:'5.188.87.56',    cc:'RU', asn:'PKNET LLC',          lat:55.7,  lng:37.6  },
];
const SIM_EPS   = [
  '/api/v1/auth/login','/api/v1/admin/exec','/api/v1/users',
  '/api/v1/payments','/api/v1/search','/api/v1/token/refresh',
];
const GEO_NODES = [
  { cc:'CN', lat:39.9,  lng:116.4 }, { cc:'RU', lat:55.7,  lng:37.6  },
  { cc:'DE', lat:51.2,  lng:10.5  }, { cc:'NL', lat:52.4,  lng:4.9   },
  { cc:'BR', lat:-15.8, lng:-47.9 }, { cc:'US', lat:37.8,  lng:-96.1 },
];
const SERVER_POS = { lat: 12.97, lng: 77.59 };
const SEV_STYLE = {
  critical:    { dot:'dot-c', text:'text-red-400',    rowBg:'bg-red-500/[0.04]',   border:'border-red-500/10'   },
  CRITICAL:    { dot:'dot-c', text:'text-red-400',    rowBg:'bg-red-500/[0.04]',   border:'border-red-500/10'   },
  high:        { dot:'dot-h', text:'text-orange-400', rowBg:'bg-orange-500/[0.04]',border:'border-orange-500/10' },
  HIGH:        { dot:'dot-h', text:'text-orange-400', rowBg:'bg-orange-500/[0.04]',border:'border-orange-500/10' },
  medium:      { dot:'dot-m', text:'text-amber-400',  rowBg:'bg-amber-500/[0.04]', border:'border-amber-500/10'  },
  MEDIUM:      { dot:'dot-m', text:'text-amber-400',  rowBg:'bg-amber-500/[0.04]', border:'border-amber-500/10'  },
  low:         { dot:'dot-l', text:'text-green-400',  rowBg:'bg-green-500/[0.04]', border:'border-green-500/10'  },
  LOW:         { dot:'dot-l', text:'text-green-400',  rowBg:'bg-green-500/[0.04]', border:'border-green-500/10'  },
};

const BASELINE = [32,20,12,8,6,5,11,25,50,70,85,80,76,82,92,88,74,83,91,86,68,55,46,38];
const mkFallbackTimeline = () => BASELINE.map((atk, i) => ({
  h:   `${String(i).padStart(2,'0')}:00`,
  atk,
  blk: Math.round(atk * 0.82),
  trf: atk * 15 + 90,
}));

const pick = (a) => a[Math.floor(Math.random() * a.length)];
const mkSim = () => ({
  id:       `sim-${Date.now()}-${Math.random().toString(36).slice(2,6)}`,
  type:     pick(['SQL_INJECTION', 'XSS_PAYLOAD', 'BRUTE_FORCE', 'JWT_TAMPER', 'RATE_LIMIT', 'RECON_SCAN', 'TOR_NODE', 'DDOS_FLOOD']),
  severity: pick(['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']),
  ip:       pick(SIM_POOL).ip,
  attacker: pick(SIM_POOL),
  endpoint: pick(SIM_EPS),
  timestamp: new Date().toISOString(),
  ts:       new Date().toLocaleTimeString('en-GB'),
});

// Normalise API attack → internal alert shape
const normaliseAttack = (a) => ({
  id:       `api-${a.ip}-${a.timestamp ?? Date.now()}`,
  type:     a.type,
  severity: a.severity || 'MEDIUM',
  ip:       a.ip,
  attacker: {
    ip: a.ip,
    cc: a.cc || (SIM_POOL.find(p => p.ip === a.ip) || {}).cc || '--',
    asn: a.asn || (SIM_POOL.find(p => p.ip === a.ip) || {}).asn || '—'
  },
  endpoint: a.endpoint,
  timestamp: a.timestamp,
  ts:       a.timestamp ? new Date(a.timestamp).toLocaleTimeString('en-GB') : '--:--:--',
});

// Normalise offender → internal shape
const normaliseOffender = (o) => ({
  ip:      o.ip,
  cc:      o.cc || (SIM_POOL.find(p=>p.ip===o.ip)||{}).cc || '--',
  asn:     o.asn || (SIM_POOL.find(p=>p.ip===o.ip)||{}).asn || '—',
  count:   o.requests ?? o.attackCount ?? 0,
  status:  o.status ?? 'ALLOW',
});

// Normalise traffic point → chart shape
const normaliseTraffic = (t) => ({
  h:   t.h ?? t.time ?? '00:00',
  trf: t.trf ?? t.traffic ?? 0,
  atk: t.atk ?? t.attacks ?? 0,
  blk: t.blk ?? t.blocked  ?? 0,
});

// Mercator projection
const merc = (lat, lng, W=540, H=175) => {
  const x = ((lng+180)/360)*W;
  const latR = (lat*Math.PI)/180;
  const y = H/2 - (W * Math.log(Math.tan(Math.PI/4+latR/2))) / (2*Math.PI);
  return { x, y };
};

// ─────────────────────────────────────────────────
//  CHART TOOLTIP
// ─────────────────────────────────────────────────
function ChartTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="card px-3 py-2.5 text-[10px] mono min-w-[130px]">
      <p className="label mb-2">{label}</p>
      {payload.map(p => (
        <div key={p.name} className="flex justify-between gap-4 mb-0.5">
          <span style={{ color:p.color }}>{p.name}</span>
          <span className="text-white font-semibold">{p.value}</span>
        </div>
      ))}
    </div>
  );
}

// ─────────────────────────────────────────────────
//  APP
// ─────────────────────────────────────────────────
export default function App() {
  const ws = useSecurityEvents() || [];

  // ── State ──────────────────────────────────────
  const [isAuthenticated, setIsAuthenticated] = useState(!!getToken());
  const [backendOnline, setBackendOnline]   = useState(null);   // null = unknown
  const [metrics,       setMetrics]         = useState({ activeThreats:0, requestsPerMin:0, blockedToday:0, riskScore:0, avgLatencyMs:0, topTargetEndpoint:'' });
  const [alerts,        setAlerts]          = useState([]);
  const [incidents,     setIncidents]       = useState([]); // For real-time toasts
  const [topIPs,        setTopIPs]          = useState([]);
  const [timeline,      setTimeline]        = useState([]);
  const [blocked,       setBlocked]         = useState(0);     // maintained locally for real-time increment
  const [now,           setNow]             = useState(new Date());
  const [range,         setRange]           = useState('24H');
  const [lastFetch,     setLastFetch]       = useState(null);
  const [liveStart,     setLiveStart]       = useState(null);
  const [failCount,     setFailCount]       = useState(0);     // To prevent flickering

  // ── Track Live Session ────────────────────────
  useEffect(() => {
    if (backendOnline && !liveStart) {
      setLiveStart(new Date());
    } else if (backendOnline === false && liveStart) {
      setLiveStart(null);
    }
  }, [backendOnline, liveStart]);

  // ── Clock ─────────────────────────────────────
  useEffect(() => {
    const id = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(id);
  }, []);

  // ── Poll backend every 15s ────────────────────
  const fetchAll = useCallback(async () => {
    try {
      const [m, attacks, offenders, traffic] = await Promise.all([
        getDashboardMetrics(),
        getRecentAttacks(),
        getTopOffenders(),
        getTrafficData(),
      ]);

      setBackendOnline(true);
      setFailCount(0);
      setLastFetch(new Date());

      // Metrics
      setMetrics(m);
      setBlocked(m.blockedToday);

      // Attacks → normalised alerts
      const norm = attacks.map(normaliseAttack);
      setAlerts(prev => {
        const ids = new Set(prev.map(a=>a.id));
        const fresh = norm.filter(a=>!ids.has(a.id));
        
        // Critical alert toasts
        const criticals = fresh.filter(a => a.severity === 'CRITICAL' || a.severity === 'HIGH');
        if (criticals.length) {
          setIncidents(p => [...criticals.map(c => ({...c, tid:Math.random()})), ...p].slice(0, 3));
        }

        return [...fresh, ...prev].slice(0, 60);
      });

      // Top offenders
      setTopIPs(offenders.map(normaliseOffender));

      // Traffic chart
      if (traffic?.length) setTimeline(traffic.map(normaliseTraffic));

    } catch {
      // Only set offline after 2 consecutive failures to prevent "irregular" flickering
      setFailCount(prev => {
        const newCount = prev + 1;
        if (newCount >= 2) setBackendOnline(false);
        return newCount;
      });
    }
  }, []);

  useEffect(() => {
    fetchAll();
    const id = setInterval(fetchAll, 15_000);
    return () => clearInterval(id);
  }, [fetchAll]);

  // ── WebSocket live events ─────────────────────
  useEffect(() => {
    if (!ws.length) return;
    const mapped = ws.map(e => ({
      id:       `ws-${Date.now()}-${Math.random()}`,
      type:     e.eventType || e.type || 'WS_EVENT',
      severity: (e.severity || 'MEDIUM').toUpperCase(),
      ip:       e.sourceIp || e.ip || 'unknown',
      attacker: { ip:e.sourceIp||e.ip||'unknown', cc:'--', asn:'WebSocket' },
      endpoint: e.endpoint || '/ws',
      timestamp: new Date().toISOString(),
      ts:       new Date().toLocaleTimeString('en-GB'),
    }));
    setAlerts(p => [...mapped, ...p].slice(0, 60));
    setBlocked(p => p + mapped.length);
  }, [ws]);

  // ── No Simulation layer (Simulation disabled by user request) ─────────

  // ── Derived ───────────────────────────────────
  const riskLabel = metrics.riskScore > 80 ? 'CRITICAL'
    : metrics.riskScore > 60 ? 'HIGH'
    : metrics.riskScore > 40 ? 'MEDIUM' : 'LOW';
  const riskColor = metrics.riskScore > 80 ? 'text-red-400'
    : metrics.riskScore > 60 ? 'text-orange-400'
    : metrics.riskScore > 40 ? 'text-amber-400' : 'text-green-400';

  const chartData = useMemo(() => {
    const hr = now.getHours();
    if (range === '1H') return timeline.slice(Math.max(0, hr));
    if (range === '6H') return timeline.slice(Math.max(0, hr - 5));
    return timeline;
  }, [timeline, range, now]);

  // Derive topIPs from alerts when backend is offline
  const resolvedTopIPs = useMemo(() => {
    if (topIPs.length) return topIPs;
    const map = {};
    alerts.forEach(a => {
      if (!map[a.ip]) map[a.ip] = { ip:a.ip, cc:a.attacker?.cc||'--', asn:a.attacker?.asn||'—', count:0, status:'ALLOW' };
      map[a.ip].count++;
    });
    return Object.values(map)
      .sort((a,b) => b.count - a.count)
      .slice(0, 5)
      .map(ip => ({ ...ip, status: ip.count>5?'BLOCKED':ip.count>2?'MONITOR':'ALLOW' }));
  }, [topIPs, alerts]);

  return (
    <>
      <div className="bg-img" />
      <div className="bg-overlay" />
      <div className="bg-grid" />

      {!isAuthenticated ? (
        <Login onLoginSuccess={() => setIsAuthenticated(true)} />
      ) : (
        <div className="relative flex h-screen overflow-hidden" style={{ zIndex:3 }}>
          <Sidebar onLogout={logout} />

        <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative">
          <Header
            now={now}
            backendOnline={backendOnline}
            lastFetch={lastFetch}
            liveStart={liveStart}
            onRefresh={fetchAll}
          />

          {/* Real-time Incident Toasts */}
          <div className="absolute top-14 right-5 z-50 pointer-events-none space-y-3 w-80">
            <AnimatePresence>
              {incidents.map((inc) => (
                <motion.div key={inc.tid}
                  initial={{ opacity:0, x:50, scale:0.9 }} animate={{ opacity:1, x:0, scale:1 }}
                  exit={{ opacity:0, x:20, scale:0.95 }}
                  onAnimationComplete={() => setTimeout(() => setIncidents(p => p.filter(x => x.tid !== inc.tid)), 5000)}
                  className="bg-red-500/15 border border-red-500/30 backdrop-blur-xl p-4 rounded-2xl shadow-2xl shadow-red-950/40 pointer-events-auto">
                  <div className="flex items-center gap-3 mb-2">
                    <div className="w-8 h-8 rounded-full bg-red-500/20 flex items-center justify-center border border-red-500/40 shadow-[0_0_15px_rgba(239,68,68,0.3)]">
                      <Zap size={14} className="text-red-400" fill="currentColor"/>
                    </div>
                    <div>
                      <p className="mono text-[10px] font-bold text-red-100 uppercase tracking-widest leading-tight">{inc.type}</p>
                      <p className="mono text-[8px] text-red-400 font-bold uppercase mt-0.5 tracking-tighter">Critical Threat Detected</p>
                    </div>
                  </div>
                  <div className="flex justify-between items-end">
                    <div>
                      <p className="mono text-[9px] text-white/50">{inc.ip}</p>
                      <p className="mono text-[8px] text-white/30 truncate max-w-[150px]">{inc.endpoint}</p>
                    </div>
                    <span className="mono text-[8.5px] text-red-300 font-bold bg-red-500/10 px-1.5 py-0.5 rounded border border-red-500/20">{inc.attacker?.cc}</span>
                  </div>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>

          {/* ── KPI ROW ── */}
          <div className="flex-shrink-0 px-5 pt-4 pb-0 grid grid-cols-6 gap-4">
            <Kpi icon={<Zap size={15}/>}
                 label="Active Threats"
                 value={metrics.activeThreats}
                 sub={`Risk level: ${riskLabel}`}
                 color="red" pulse={metrics.activeThreats > 0}/>
            <Kpi icon={<Activity size={15}/>}
                 label="Requests / Min"
                 value={metrics.requestsPerMin.toLocaleString()}
                 sub="60-second window"
                 color="blue"/>
            <Kpi icon={<Timer size={15}/>}
                 label="Avg Latency"
                 value={`${metrics.avgLatencyMs}ms`}
                 sub="System Response"
                 color="blue"/>
            <Kpi icon={<Target size={15}/>}
                 label="Top Target"
                 value={metrics.topTargetEndpoint.split('/').pop() || 'Auth'}
                 sub={metrics.topTargetEndpoint}
                 color="amber"/>
            <Kpi icon={<Lock size={15}/>}
                 label="Blocked Today"
                 value={(metrics.blockedToday || blocked).toLocaleString()}
                 sub="Auto-firewall enforcement"
                 color="green"/>
            <Kpi icon={<AlertTriangle size={15}/>}
                 label="System Risk"
                 value={`${metrics.riskScore} / 100`}
                 sub={riskLabel}
                 color="amber"
                 pulse={metrics.riskScore > 75}
                 rColor={riskColor}/>
          </div>

          {/* ── MAIN ROW ── */}
          <div className="flex-1 min-h-0 px-5 pt-4 grid grid-cols-12 gap-4 pb-0">
            {/* Chart */}
            <div className="col-span-8 card p-5 flex flex-col relative overflow-hidden">
              <div className="scan-wrap"><div className="scan-bar"/></div>

              <div className="flex items-start justify-between mb-4 flex-shrink-0">
                <div>
                  <p className="label mb-0.5">Network Telemetry · {backendOnline === null ? 'Initializing telemetry…' : backendOnline ? 'Backend Connected' : 'System Offline'}</p>
                  <h2 className="section-title">Traffic · Attacks · Blocked</h2>
                </div>
                <div className="flex items-center gap-1.5">
                  {['1H','6H','24H'].map(r => (
                    <button key={r} onClick={() => setRange(r)}
                      className={`mono text-[9px] px-2.5 py-1 rounded-lg transition-all ${
                        range===r ? 'bg-blue-500/15 border border-blue-500/30 text-blue-300'
                                  : 'bg-white/[0.03] border border-white/[0.06] text-white/30 hover:text-white/60'}`}>
                      {r}
                    </button>
                  ))}
                </div>
              </div>

              <div className="flex-1 min-h-0">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={chartData} margin={{ left:-14, right:8, top:4, bottom:0 }}>
                    <defs>
                      {[['trf','#3b82f6',0.07],['blk','#8b5cf6',0.18],['atk','#ef4444',0.22]].map(([k,c,op]) => (
                        <linearGradient key={k} id={`g_${k}`} x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%"   stopColor={c} stopOpacity={op}/>
                          <stop offset="100%" stopColor={c} stopOpacity={0}/>
                        </linearGradient>
                      ))}
                    </defs>
                    <CartesianGrid strokeDasharray="1 8" stroke="rgba(255,255,255,.03)" vertical={false}/>
                    <XAxis dataKey="h" tick={{ fill:'rgba(200,211,234,.2)', fontSize:8.5, fontFamily:'JetBrains Mono' }} tickLine={false} axisLine={false} interval={range==='24H'?3:0}/>
                    <YAxis tick={{ fill:'rgba(200,211,234,.2)', fontSize:8.5, fontFamily:'JetBrains Mono' }} tickLine={false} axisLine={false}/>
                    <Tooltip content={<ChartTooltip/>} cursor={{ stroke:'rgba(255,255,255,0.05)', strokeWidth:1 }}/>
                    <Area type="monotone" dataKey="trf" name="Traffic" stroke="#3b82f6" strokeWidth={1.5} fill="url(#g_trf)"/>
                    <Area type="monotone" dataKey="blk" name="Blocked" stroke="#8b5cf6" strokeWidth={2}   fill="url(#g_blk)"/>
                    <Area type="monotone" dataKey="atk" name="Attacks" stroke="#ef4444" strokeWidth={2.5} fill="url(#g_atk)"/>
                  </AreaChart>
                </ResponsiveContainer>
              </div>

              <div className="flex gap-5 mt-2.5 flex-shrink-0">
                {[['#ef4444','Attacks'],['#8b5cf6','Blocked'],['#3b82f6','Traffic']].map(([c,l]) => (
                  <div key={l} className="flex items-center gap-1.5">
                    <div className="w-4 h-[2px] rounded-full" style={{ background:c }}/>
                    <span className="label" style={{ letterSpacing:'0.15em' }}>{l}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Attack Activity Feed */}
            <div className="col-span-4 card flex flex-col overflow-hidden">
              <div className="px-4 pt-4 pb-2.5 border-b border-white/[0.05] flex-shrink-0">
                <p className="label mb-0.5">Real-Time</p>
                <div className="flex items-center justify-between mt-0.5">
                  <h2 className="section-title flex items-center gap-1.5">
                    <Terminal size={12} className="text-red-400"/>
                    Recent Attack Activity
                  </h2>
                  <div className="flex items-center gap-1.5">
                    <div className="live-dot flex-shrink-0"/>
                    <span className="mono text-[8px] text-emerald-400 uppercase tracking-widest">
                      Live · {alerts.length}
                    </span>
                  </div>
                </div>
              </div>
              <div className="flex-1 overflow-y-auto scroll p-2.5 space-y-1 min-h-0">
                <AnimatePresence initial={false} mode="popLayout">
                  {alerts.slice(0, 22).map(a => <FeedRow key={a.id} a={a}/>)}
                </AnimatePresence>
              </div>
            </div>
          </div>

          {/* ── BOTTOM ROW ── */}
          <div className="flex-shrink-0 px-5 pt-4 pb-5 grid grid-cols-12 gap-4">
            {/* World Map */}
            <div className="col-span-8 card p-4 relative overflow-hidden">
              <p className="label mb-0.5">GeoIP Intelligence</p>
              <h3 className="section-title mb-3">Global Attack Origins</h3>
              <WorldMap alerts={alerts}/>
            </div>

            {/* Top Offending IPs */}
            <div className="col-span-4 card p-4 flex flex-col">
              <p className="label mb-0.5">Attacker Intelligence</p>
              <h3 className="section-title mb-4">Top Offending IPs</h3>
              <div className="space-y-2 flex-1">
                <AnimatePresence mode="popLayout">
                  {resolvedTopIPs.map((ip, i) => {
                    const bc = ip.status==='BLOCKED'?'b-r':ip.status==='MONITOR'?'b-a':'b-g';
                    return (
                      <motion.div key={ip.ip} layout
                        initial={{ opacity:0, x:16 }} animate={{ opacity:1, x:0 }} transition={{ delay:i*0.04 }}
                        className="flex items-center gap-2.5 px-3 py-2.5 rounded-xl bg-white/[0.02] border border-white/[0.05] hover:bg-white/[0.045] transition-all cursor-default">
                        <span className="mono text-[8.5px] text-white/20 w-3">{i+1}</span>
                        <div className="flex-1 min-w-0">
                          <div className="flex justify-between items-center gap-1">
                            <span className="mono text-[10px] text-white/80 truncate">{ip.ip}</span>
                            <span className={`badge ${bc} flex-shrink-0`}>{ip.status}</span>
                          </div>
                          <div className="flex justify-between mono text-[8px] text-white/25 mt-0.5">
                            <span>{ip.cc} · {ip.asn}</span>
                            <span className="text-white/35">{ip.count ?? ip.requests} req</span>
                          </div>
                        </div>
                      </motion.div>
                    );
                  })}
                  {resolvedTopIPs.length === 0 &&
                    <p className="mono text-[9px] text-white/20 text-center py-4">Collecting data…</p>}
                </AnimatePresence>
              </div>
            </div>
          </div>
        </div>
        </div>
      )}
    </>
  );
}

// ─────────────────────────────────────────────────
//  SIDEBAR
// ─────────────────────────────────────────────────
function Sidebar({ onLogout }) {
  return (
    <aside className="w-[58px] flex flex-col items-center py-4 gap-1 border-r border-white/[0.05] flex-shrink-0"
      style={{ background:'rgba(3,6,16,.9)', backdropFilter:'blur(28px)' }}>
      <div className="w-9 h-9 bg-gradient-to-br from-red-700 to-red-950 rounded-xl flex items-center justify-center mb-5 shadow-lg shadow-red-950/60">
        <Shield size={16} className="text-white"/>
      </div>
      {[
        { icon:<Globe size={15}/>,   label:'Overview', active:true },
        { icon:<Activity size={15}/>,label:'Telemetry' },
        { icon:<Server size={15}/>,  label:'Servers'   },
        { icon:<Eye size={15}/>,     label:'Monitor'   },
        { icon:<Filter size={15}/>,  label:'Rules'     },
        { icon:<Lock size={15}/>,    label:'Logout', action: onLogout },
      ].map(({ icon, label, active, action }) => (
        <button key={label} title={label} onClick={action}
          className={`w-9 h-9 flex items-center justify-center rounded-xl transition-all ${
            active ? 'bg-white/[0.08] text-white border border-white/[0.08]'
                   : 'text-white/22 hover:text-white/55 hover:bg-white/[0.04]'}`}>
          {icon}
        </button>
      ))}
      <div className="mt-auto">
        <div className="w-7 h-7 rounded-full bg-gradient-to-br from-indigo-600 to-violet-700 ring-2 ring-white/[0.07] cursor-pointer"/>
      </div>
    </aside>
  );
}

// ─────────────────────────────────────────────────
//  HEADER
// ─────────────────────────────────────────────────
function Header({ now, backendOnline, lastFetch, liveStart, onRefresh }) {
  const elapsed = useMemo(() => {
    if (!liveStart) return '00:00:00';
    const diff = Math.floor((now - liveStart) / 1000);
    const h = Math.floor(diff / 3600);
    const m = Math.floor((diff % 3600) / 60);
    const s = diff % 60;
    return [h, m, s].map(v => String(v).padStart(2, '0')).join(':');
  }, [liveStart, now]);

  return (
    <header className="flex-shrink-0 h-12 flex items-center justify-between px-5 border-b border-white/[0.05]"
      style={{ background:'rgba(3,6,16,.8)', backdropFilter:'blur(28px)' }}>
      <div>
        <p className="label">ShieldAPI Security Platform · v3.2</p>
        <h1 className="text-[13px] font-bold text-white flex items-center gap-2 leading-tight mt-0.5">
          SOC Command Center
          <span className="mono text-[7.5px] px-1.5 py-0.5 rounded bg-red-500/10 border border-red-500/20 text-red-400 uppercase tracking-widest">
            THREAT ACTIVE
          </span>
        </h1>
      </div>

      <div className="flex items-center gap-3">
        {/* Backend connectivity pill */}
        <div className={`flex items-center gap-1.5 px-2.5 py-1 rounded-full border mono text-[8px] uppercase tracking-widest ${
          backendOnline === null  ? 'bg-blue-500/10 border-blue-500/20 text-blue-400'
          : backendOnline         ? 'bg-emerald-500/8 border-emerald-500/15 text-emerald-400'
                                  : 'bg-red-500/8 border-red-500/15 text-red-400'}`}>
          {backendOnline === null ? <Activity size={9} className="animate-pulse"/> : backendOnline
            ? <CheckCircle2 size={9}/>
            : <WifiOff size={9}/>}
          {backendOnline === null ? 'Establishing Connection…'
           : backendOnline ? `Backend Live · ${elapsed}`
           : 'Backend Offline'}
        </div>

        <div className="h-7 w-px bg-white/[0.06]"/>

        <div className="text-right">
          <p className="label">UTC +5:30</p>
          <p className="mono text-[11px] text-white/60 mt-0.5" style={{ fontVariantNumeric:'tabular-nums' }}>
            {now.toLocaleTimeString('en-GB')} — {now.toLocaleDateString('en-GB')}
          </p>
        </div>
        <div className="h-7 w-px bg-white/[0.06]"/>
        <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-500/8 border border-emerald-500/15">
          <motion.span className="w-1.5 h-1.5 rounded-full bg-emerald-400"
            animate={{ opacity:[1,.2,1] }} transition={{ repeat:Infinity, duration:2 }}/>
          <span className="mono text-[8px] text-emerald-400 uppercase tracking-widest">Stream Live</span>
        </div>
        <button className="flex items-center gap-1.5 bg-red-700 hover:bg-red-800 text-white px-3.5 py-1.5 rounded-lg mono text-[9.5px] font-bold uppercase tracking-wider transition-all">
          <Zap size={11} fill="currentColor"/> Lockdown
        </button>
        <button onClick={onRefresh} title="Refresh from backend"
          className="w-7 h-7 flex items-center justify-center bg-white/[0.04] hover:bg-white/[0.08] border border-white/[0.06] text-white/35 hover:text-white rounded-lg transition-all">
          <RefreshCcw size={11}/>
        </button>
      </div>
    </header>
  );
}

// ─────────────────────────────────────────────────
//  KPI CARD
// ─────────────────────────────────────────────────
const KPI_P = {
  red:   { ib:'bg-red-500/10 text-red-400 border-red-500/15',       glow:'rgba(239,68,68,0.08)'   },
  blue:  { ib:'bg-blue-500/10 text-blue-400 border-blue-500/15',     glow:'rgba(59,130,246,0.07)'  },
  green: { ib:'bg-emerald-500/10 text-emerald-400 border-emerald-500/15', glow:'rgba(16,185,129,0.06)' },
  amber: { ib:'bg-amber-500/10 text-amber-400 border-amber-500/15',  glow:'rgba(245,158,11,0.07)' },
};

function Kpi({ icon, label, value, sub, color, pulse, rColor }) {
  const p = KPI_P[color]||KPI_P.blue;
  return (
    <motion.div
      animate={pulse ? { boxShadow:['0 0 0 0 rgba(239,68,68,0)','0 0 0 8px rgba(239,68,68,0.1)','0 0 0 0 rgba(239,68,68,0)'] }:{}}
      transition={{ duration:1.4, repeat:pulse?Infinity:0 }}
      className="card p-5 relative overflow-hidden group cursor-default hover:scale-[1.015]">
      <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none"
        style={{ background:`radial-gradient(ellipse at 75% 25%, ${p.glow} 0%, transparent 70%)` }}/>
      <div className={`w-7 h-7 rounded-lg flex items-center justify-center border mb-4 ${p.ib}`}>{icon}</div>
      <div className={`text-[24px] font-bold leading-none mb-1.5 ${rColor||'text-white'}`}
        style={{ fontVariantNumeric:'tabular-nums' }}>{value}</div>
      <p className="label mb-1">{label}</p>
      <p className="mono text-[8px] text-white/18">{sub}</p>
    </motion.div>
  );
}

// ─────────────────────────────────────────────────
//  FEED ROW
// ─────────────────────────────────────────────────
function FeedRow({ a }) {
  const s = SEV_STYLE[a.severity] || SEV_STYLE.medium;
  return (
    <motion.div layout
      initial={{ opacity:0, y:-6 }} animate={{ opacity:1, y:0 }}
      exit={{ opacity:0, height:0, marginBottom:0 }} transition={{ duration:0.2 }}
      className={`px-2.5 py-2 rounded-xl border cursor-default hover:brightness-115 transition-all ${s.rowBg} ${s.border}`}>
      <div className="flex items-start gap-2">
        <motion.div animate={{ opacity:[1,0.3,1] }} transition={{ repeat:Infinity, duration:2.5, delay:Math.random()*2 }}
          className={`dot ${s.dot} mt-1.5`}/>
        <div className="flex-1 min-w-0">
          <div className="flex justify-between items-center gap-2">
            <span className={`mono text-[9.5px] font-semibold truncate ${s.text}`}>{a.type}</span>
            <span className="mono text-[7.5px] text-white/20 flex-shrink-0 tabular-nums">{a.ts}</span>
          </div>
          <div className="flex justify-between mono text-[8px] text-white/28 mt-0.5">
            <span>{a.attacker?.cc || '--'} · {a.ip}</span>
            <span className="text-white/18 truncate max-w-[75px]">{a.endpoint}</span>
          </div>
        </div>
      </div>
    </motion.div>
  );
}

// ─────────────────────────────────────────────────
//  WORLD MAP
// ─────────────────────────────────────────────────
function WorldMap({ alerts }) {
  const W = 540, H = 175;
  const srv = merc(SERVER_POS.lat, SERVER_POS.lng, W, H);

  const activeSrc = useMemo(() => {
    const seen = new Set();
    return alerts.slice(0, 10).filter(a => {
      const att = a.attacker;
      if (!att?.lat || seen.has(a.ip)) return false;
      seen.add(a.ip); return true;
    }).map(a => ({ ...merc(a.attacker.lat, a.attacker.lng, W, H), ip:a.ip }));
  }, [alerts]);

  return (
    <div className="relative w-full" style={{ height:'155px' }}>
      <svg width="100%" height="100%" viewBox={`0 0 ${W} ${H}`} preserveAspectRatio="xMidYMid meet"
        style={{ position:'absolute', inset:0 }}>
        {[-60,-30,0,30,60].map(lat => { const {y}=merc(lat,0,W,H); return <line key={`lat${lat}`} x1={0} y1={y} x2={W} y2={y} stroke="rgba(59,130,246,0.07)" strokeWidth="0.5"/>; })}
        {[-120,-60,0,60,120].map(lng => { const {x}=merc(0,lng,W,H); return <line key={`lng${lng}`} x1={x} y1={0} x2={x} y2={H} stroke="rgba(59,130,246,0.07)" strokeWidth="0.5"/>; })}

        {GEO_NODES.map(g => {
          const pt = merc(g.lat,g.lng,W,H);
          return (
            <g key={g.cc}>
              <motion.circle cx={pt.x} cy={pt.y} r={12} fill="rgba(239,68,68,0.07)"
                animate={{ r:[12,18,12], opacity:[0.6,0.1,0.6] }}
                transition={{ duration:3+Math.random()*2, repeat:Infinity, delay:Math.random()*2 }}/>
              <circle cx={pt.x} cy={pt.y} r={3.5} fill="rgba(239,68,68,0.4)" stroke="#ef4444" strokeWidth="0.8"/>
              <text x={pt.x} y={pt.y+12} textAnchor="middle" fontSize="6.5" fontFamily="JetBrains Mono" fill="rgba(239,68,68,0.6)">{g.cc}</text>
            </g>
          );
        })}

        {activeSrc.map(s => (
          <motion.line key={s.ip} x1={s.x} y1={s.y} x2={srv.x} y2={srv.y}
            stroke="rgba(239,68,68,0.35)" strokeWidth="0.8" strokeDasharray="4 4"
            initial={{ opacity:0 }} animate={{ opacity:[0,.55,0] }}
            transition={{ duration:2.5, repeat:Infinity, delay:Math.random()*1.5 }}/>
        ))}

        <motion.circle cx={srv.x} cy={srv.y} r={12} fill="rgba(59,130,246,0.08)"
          animate={{ r:[12,20,12], opacity:[0.9,0.15,0.9] }} transition={{ duration:2.5, repeat:Infinity }}/>
        <circle cx={srv.x} cy={srv.y} r={4} fill="#3b82f6" stroke="rgba(59,130,246,0.5)" strokeWidth="2"
          style={{ filter:'drop-shadow(0 0 5px #3b82f6)' }}/>
        <text x={srv.x} y={srv.y+13} textAnchor="middle" fontSize="6.5" fontFamily="JetBrains Mono" fill="rgba(59,130,246,0.65)">SHIELD</text>
      </svg>
      <div className="absolute bottom-0 left-0 flex gap-4">
        {[['#ef4444','Attack source'],['#3b82f6','Protected server']].map(([c,l]) => (
          <div key={l} className="flex items-center gap-1.5">
            <div className="w-2 h-2 rounded-full" style={{ background:c }}/>
            <span className="mono text-[8px] text-white/25">{l}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
