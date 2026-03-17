import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
});

export const securityService = {
  getStats: async () => {
    try {
      const response = await api.get('/api/analytics/stats');
      const data = response.data;
      // Map backend MetricsDTO to frontend expected stats
      return {
        totalRequests: (data.requestsPerMin || 0) * 1440, // Projection for 24h
        threatsDetected: data.activeThreats || 0,
        blockedRequests: data.blockedToday || 0,
        activeIps: Math.ceil((data.activeThreats || 0) / 3) + 5, // Estimated
        requestTrend: +12.5,
        threatTrend: -2.3,
        riskScore: data.riskScore || 0,
        avgLatencyMs: data.avgLatencyMs || 0,
        topTargetEndpoint: data.topTargetEndpoint || 'unknown'
      };
    } catch (error) {
      console.error('Error fetching stats:', error);
      throw error;
    }
  },

  getLogs: async (params = {}) => {
    try {
      const response = await api.get('/api/analytics/logs', { params });
      return response.data.map((log, idx) => ({
        id: idx,
        timestamp: log.timestamp,
        ip: log.ip,
        endpoint: log.endpoint,
        status: log.severity === 'CRITICAL' ? 'Blocked' : log.severity === 'MEDIUM' ? 'Flagged' : 'Safe',
        threatType: log.type,
        severity: log.severity
      }));
    } catch (error) {
      console.error('Error fetching logs:', error);
      throw error;
    }
  },

  getThreats: async () => {
    try {
      const response = await api.get('/api/analytics/threats');
      return response.data; // Already returns {name, count} list
    } catch (error) {
      console.error('Error fetching threats:', error);
      throw error;
    }
  },

  // Mock data for development
  getMockStats: () => ({
    totalRequests: 125430,
    threatsDetected: 124,
    blockedRequests: 89,
    activeIps: 45,
    requestTrend: +12.5,
    threatTrend: -2.3,
  }),

  getMockLogs: () => [
    { id: 1, timestamp: new Date().toISOString(), ip: '192.168.1.105', endpoint: '/auth/login', status: 'Blocked', threatType: 'SQL Injection', severity: 'High' },
    { id: 2, timestamp: new Date().toISOString(), ip: '45.76.12.34', endpoint: '/api/users', status: 'Flagged', threatType: 'XSS Attempt', severity: 'Medium' },
    { id: 3, timestamp: new Date().toISOString(), ip: '102.34.11.9', endpoint: '/api/data', status: 'Safe', threatType: 'None', severity: 'Low' },
    { id: 4, timestamp: new Date().toISOString(), ip: '89.123.44.2', endpoint: '/admin/config', status: 'Blocked', threatType: 'Brute Force', severity: 'Critical' },
    { id: 5, timestamp: new Date().toISOString(), ip: '192.168.1.105', endpoint: '/auth/register', status: 'Safe', threatType: 'None', severity: 'Low' },
  ],

  getMockThreatData: () => [
    { name: 'SQLi', count: 45 },
    { name: 'XSS', count: 32 },
    { name: 'Brute Force', count: 28 },
    { name: 'Bot traffic', count: 15 },
    { name: 'DDoS', count: 4 },
  ],

  getMockChartData: () => {
    const data = [];
    for (let i = 24; i >= 0; i--) {
      data.push({
        time: `${i}h ago`,
        requests: Math.floor(Math.random() * 5000) + 1000,
        threats: Math.floor(Math.random() * 50),
      });
    }
    return data;
  }
};

export default api;
