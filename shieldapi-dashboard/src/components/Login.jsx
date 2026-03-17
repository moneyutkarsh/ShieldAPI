import React, { useState } from 'react';
import { Shield, Lock, User, ArrowRight, Loader2 } from 'lucide-react';
import { login } from '../services/api';
import { motion } from 'framer-motion';

export default function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(username, password);
      onLoginSuccess();
    } catch (err) {
      setError('Invalid credentials or system offline');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden bg-[#030610]">
      <div className="bg-img" />
      <div className="bg-overlay" />
      <div className="bg-grid" />

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="relative z-10 w-full max-w-md p-8 card bg-white/[0.02] border-white/[0.05] backdrop-blur-3xl shadow-2xl"
      >
        <div className="flex flex-col items-center mb-8">
          <div className="w-16 h-16 bg-gradient-to-br from-red-700 to-red-950 rounded-2xl flex items-center justify-center mb-4 shadow-xl shadow-red-950/40">
            <Shield size={32} className="text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">ShieldAPI Access</h1>
          <p className="label mt-2">Security Operations Center Login</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-1.5">
            <label className="label ml-1">Identity Identifier</label>
            <div className="relative group">
              <User className="absolute left-3.5 top-1/2 -translate-y-1/2 text-white/20 group-focus-within:text-blue-400 transition-colors" size={18} />
              <input 
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Username"
                className="w-full bg-white/[0.03] border border-white/[0.08] rounded-xl py-3.5 pl-11 pr-4 text-white mono text-sm focus:outline-none focus:border-blue-500/50 focus:bg-blue-500/[0.02] transition-all"
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="label ml-1">Access Key</label>
            <div className="relative group">
              <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-white/20 group-focus-within:text-blue-400 transition-colors" size={18} />
              <input 
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-white/[0.03] border border-white/[0.08] rounded-xl py-3.5 pl-11 pr-4 text-white mono text-sm focus:outline-none focus:border-blue-500/50 focus:bg-blue-500/[0.02] transition-all"
              />
            </div>
          </div>

          {error && (
            <motion.p 
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              className="mono text-[11px] text-red-400 bg-red-400/10 border border-red-400/20 p-3 rounded-lg text-center"
            >
              {error}
            </motion.p>
          )}

          <button 
            type="submit"
            disabled={loading}
            className="w-full bg-red-700 hover:bg-red-800 disabled:opacity-50 text-white py-4 rounded-xl mono text-sm font-bold uppercase tracking-widest transition-all flex items-center justify-center gap-2 group"
          >
            {loading ? (
              <Loader2 className="animate-spin" size={20} />
            ) : (
              <>
                Initiate Access <ArrowRight className="group-hover:translate-x-1 transition-transform" size={18} />
              </>
            )}
          </button>
        </form>

        <div className="mt-8 pt-6 border-t border-white/[0.05] text-center">
          <p className="mono text-[10px] text-white/20 uppercase tracking-[0.2em]">Authorized Personnel Only</p>
        </div>
      </motion.div>
    </div>
  );
}
