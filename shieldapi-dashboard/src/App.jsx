import React, { useState, useEffect } from 'react';
import { 
  Zap, ShieldAlert, ShieldCheck, Users, 
  Activity, BarChart3, AlertTriangle 
} from 'lucide-react';
import Sidebar from './layout/Sidebar';
import Navbar from './layout/Navbar';
import StatsCard from './components/StatsCard';
import { RequestsChart, ThreatDistribution } from './components/SecurityCharts';
import LogsTable from './components/LogsTable';
import { securityService } from './services/api';

function App() {
  const [loading, setLoading] = useState(true);
  const [isLive, setIsLive] = useState(false);
  const [stats, setStats] = useState(securityService.getMockStats());
  const [logs, setLogs] = useState(securityService.getMockLogs());
  const [threats, setThreats] = useState(securityService.getMockThreatData());
  const [chartData, setChartData] = useState(securityService.getMockChartData());

  const fetchData = async () => {
    setLoading(true);
    try {
      const [s, l, t] = await Promise.all([
        securityService.getStats(),
        securityService.getLogs(),
        securityService.getThreats()
      ]);
      setStats(s); 
      setLogs(l); 
      setThreats(t);
      setChartData(securityService.getMockChartData());
      setIsLive(true);
    } catch (error) {
      console.warn("Backend unreachable, falling back to mock data");
      setIsLive(false);
      setStats(securityService.getMockStats());
      setLogs(securityService.getMockLogs());
      setThreats(securityService.getMockThreatData());
      setChartData(securityService.getMockChartData());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="flex bg-bg-dark text-white min-h-screen">
      <div className="fixed inset-0 bg-grid opacity-10 pointer-events-none" />
      
      <Sidebar />

      <main className="flex-1 flex flex-col relative z-10 w-full overflow-x-hidden">
        <Navbar onRefresh={fetchData} loading={loading} isLive={isLive} />

        <div className="p-8 space-y-8 max-w-[1600px] mx-auto w-full">
          {/* Header Section */}
          <div className="flex items-end justify-between">
            <div>
              <h2 className="text-2xl font-bold tracking-tight text-white mb-1">Command Center</h2>
              <p className="text-white/40 text-sm">System operational · Tracking <span className="text-blue-400 font-bold uppercase tracking-widest text-[10px]">Active Node Cluster 04</span></p>
            </div>
            
            <div className="flex items-center gap-3">
              <div className="px-3 py-1.5 rounded-lg bg-emerald-500/10 border border-emerald-500/20 flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse shadow-[0_0_8px_rgba(16,185,129,0.8)]" />
                <span className="text-[10px] mono font-bold text-emerald-400 uppercase tracking-widest">Global Watcher Online</span>
              </div>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatsCard 
              icon={Activity} 
              title="Total API Requests" 
              value={stats.totalRequests.toLocaleString()} 
              trend="up" 
              trendValue={stats.requestTrend}
              color="blue"
            />
            <StatsCard 
              icon={ShieldAlert} 
              title="Threats Detected" 
              value={stats.threatsDetected} 
              trend="down" 
              trendValue={stats.threatTrend}
              color="red"
            />
            <StatsCard 
              icon={Zap} 
              title="Blocked Requests" 
              value={stats.blockedRequests} 
              color="amber"
            />
            <StatsCard 
              icon={Users} 
              title="Unique Attack IPs" 
              value={stats.activeIps} 
              color="blue"
            />
          </div>

          {/* Charts Section */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className="lg:col-span-8">
              <RequestsChart data={chartData} />
            </div>
            <div className="lg:col-span-4">
              <ThreatDistribution data={threats} />
            </div>
          </div>

          {/* Logs Section */}
          <div className="pb-8">
            <LogsTable logs={logs} />
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
