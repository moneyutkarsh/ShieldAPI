import React from 'react';
import { Bell, Search, User, RefreshCw, Command } from 'lucide-react';

const Navbar = ({ onRefresh, loading, isLive }) => {
  return (
    <header className="h-20 px-8 flex items-center justify-between border-b border-white/5 bg-transparent backdrop-blur-md sticky top-0 z-10">
      <div className="flex items-center gap-6">
        <div className="relative group">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-white/20 group-focus-within:text-blue-400 transition-colors" size={16} />
          <input 
            type="text" 
            placeholder="Search threats, IPs, or logs..." 
            className="w-80 h-10 bg-white/5 border border-white/10 rounded-xl pl-10 pr-4 text-xs text-white focus:outline-none focus:border-blue-500/50 focus:ring-4 focus:ring-blue-500/5 transition-all"
          />
          <div className="absolute right-3 top-1/2 -translate-y-1/2 px-1.5 py-0.5 rounded border border-white/10 bg-white/5 text-[10px] text-white/30 font-mono pointer-events-none">
            ⌘ K
          </div>
        </div>
      </div>

      <div className="flex items-center gap-4">
        {/* Backend Status Indicator */}
        <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-white/5 border border-white/5 mr-2">
          <div className={`w-2 h-2 rounded-full animate-pulse transition-colors ${
            isLive ? 'bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]' : 'bg-amber-500 shadow-[0_0_8px_rgba(245,158,11,0.5)]'
          }`} />
          <span className="text-[10px] font-mono font-bold uppercase tracking-wider text-white/40">
            {isLive ? 'Backend Live' : 'Mock Mode'}
          </span>
        </div>

        <button 
          onClick={onRefresh}
          className={`w-10 h-10 flex items-center justify-center rounded-xl bg-white/5 border border-white/10 text-white/60 hover:text-white hover:bg-white/10 transition-all ${loading ? 'animate-spin' : ''}`}
        >
          <RefreshCw size={18} />
        </button>
        
        <div className="h-6 w-px bg-white/10" />

        <button className="relative w-10 h-10 flex items-center justify-center rounded-xl bg-white/5 border border-white/10 text-white/60 hover:text-white hover:bg-white/10 transition-all">
          <Bell size={18} />
          <div className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-[#060c1a]" />
        </button>

        <div className="flex items-center gap-3 pl-2">
          <div className="text-right">
            <p className="text-xs font-semibold text-white">Admin Unit 01</p>
            <p className="text-[10px] text-emerald-400 font-mono uppercase tracking-widest font-bold">Authenticated</p>
          </div>
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 border border-white/10 flex items-center justify-center text-white font-bold text-sm shadow-lg shadow-blue-500/20">
            AU
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
