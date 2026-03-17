import React from 'react';
import { 
  Shield, LayoutDashboard, Database, Activity, 
  Settings, Bell, Search, LogOut, ShieldAlert
} from 'lucide-react';

const Sidebar = () => {
  const menuItems = [
    { icon: LayoutDashboard, label: 'Dashboard', active: true },
    { icon: Activity, label: 'Threat Center' },
    { icon: Database, label: 'Logs Archive' },
    { icon: ShieldAlert, label: 'Rules Engine' },
    { icon: Search, label: 'IP Intelligence' },
  ];

  return (
    <aside className="w-64 h-screen glass-card rounded-none border-y-0 border-l-0 border-r border-white/5 flex flex-col z-20 sticky top-0">
      <div className="p-6 flex items-center gap-3">
        <div className="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-900/40">
          <Shield className="text-white" size={24} />
        </div>
        <div>
          <h1 className="text-white font-bold tracking-tight">ShieldAPI</h1>
          <p className="text-[10px] text-blue-400 font-mono font-bold tracking-wider uppercase">SOC System</p>
        </div>
      </div>

      <nav className="flex-1 px-4 py-6 space-y-2">
        {menuItems.map((item) => (
          <button
            key={item.label}
            className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group ${
              item.active 
                ? 'bg-blue-600/10 text-blue-400 border border-blue-500/20 shadow-inner shadow-blue-500/5' 
                : 'text-white/40 hover:text-white/80 hover:bg-white/5'
            }`}
          >
            <item.icon size={18} className={item.active ? 'text-blue-400' : 'group-hover:text-white'} />
            <span className="text-sm font-medium">{item.label}</span>
            {item.active && (
              <div className="ml-auto w-1.5 h-1.5 rounded-full bg-blue-400 shadow-[0_0_8px_rgba(59,130,246,0.8)]" />
            )}
          </button>
        ))}
      </nav>

      <div className="p-4 border-t border-white/5 space-y-2">
        <button className="w-full flex items-center gap-3 px-4 py-3 text-white/40 hover:text-white/80 hover:bg-white/5 rounded-xl transition-all">
          <Settings size={18} />
          <span className="text-sm font-medium">Settings</span>
        </button>
        <button className="w-full flex items-center gap-3 px-4 py-3 text-red-400/60 hover:text-red-400 hover:bg-red-400/5 rounded-xl transition-all">
          <LogOut size={18} />
          <span className="text-sm font-medium">Sign Out</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
