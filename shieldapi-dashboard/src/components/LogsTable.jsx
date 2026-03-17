import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ExternalLink, ShieldAlert, ShieldCheck, ShieldX } from 'lucide-react';

const StatusBadge = ({ status }) => {
  const styles = {
    Blocked: 'bg-red-500/10 text-red-400 border-red-500/20',
    Flagged: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
    Safe: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  };

  const icons = {
    Blocked: ShieldX,
    Flagged: ShieldAlert,
    Safe: ShieldCheck,
  };

  const Icon = icons[status] || ShieldCheck;

  return (
    <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full border text-[10px] font-bold uppercase tracking-tight ${styles[status]}`}>
      <Icon size={12} />
      {status}
    </div>
  );
};

const LogsTable = ({ logs }) => {
  return (
    <div className="glass-card overflow-hidden">
      <div className="px-6 py-4 border-b border-white/5 flex items-center justify-between">
        <div>
          <h3 className="section-title">Security Events Log</h3>
          <p className="label-sm">Real-time API activity monitor</p>
        </div>
        <button className="text-xs text-blue-400 hover:text-blue-300 transition-colors flex items-center gap-1.5 font-medium">
          View All Logs <ExternalLink size={14} />
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left">
          <thead>
            <tr className="bg-white/[0.02]">
              <th className="px-6 py-4 label-sm">Timestamp</th>
              <th className="px-6 py-4 label-sm">Origin (IP)</th>
              <th className="px-6 py-4 label-sm">Endpoint</th>
              <th className="px-6 py-4 label-sm">Status</th>
              <th className="px-6 py-4 label-sm">Threat Signature</th>
              <th className="px-6 py-4 label-sm text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            <AnimatePresence>
              {logs.map((log, index) => (
                <motion.tr 
                  key={log.id}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="group hover:bg-white/[0.02] transition-colors cursor-default"
                >
                  <td className="px-6 py-4 text-xs mono text-white/40">{new Date(log.timestamp).toLocaleTimeString()}</td>
                  <td className="px-6 py-4 text-xs font-bold text-white/80">{log.ip}</td>
                  <td className="px-6 py-4 text-xs mono text-blue-400/80">{log.endpoint}</td>
                  <td className="px-6 py-4">
                    <StatusBadge status={log.status} />
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-[11px] font-medium ${log.threatType !== 'None' ? 'text-red-400' : 'text-white/30'}`}>
                      {log.threatType}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <button className="text-white/20 hover:text-blue-400 transition-colors">
                      <ExternalLink size={16} />
                    </button>
                  </td>
                </motion.tr>
              ))}
            </AnimatePresence>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default LogsTable;
