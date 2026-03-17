/**
 * src/services/api.js
 *
 * Centralized fetch-based API service for the ShieldAPI SOC Dashboard.
 * Uses native browser fetch — no extra dependencies required.
 *
 * All four endpoints connect to DashboardController on localhost:8080.
 */

const BASE_URL = 'http://localhost:8080/api';
const AUTH_URL = 'http://localhost:8080/auth';
const TIMEOUT_MS = 12_000;

let authToken = localStorage.getItem('shield_token') || null;

export const setToken = (token) => {
  authToken = token;
  if (token) localStorage.setItem('shield_token', token);
  else localStorage.removeItem('shield_token');
};

export const getToken = () => authToken;

export const logout = () => {
  setToken(null);
  window.location.reload();
};

// ─── Timeout-aware fetch wrapper ─────────────────────────────────────────────
async function apiFetch(path, options = {}) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), TIMEOUT_MS);
  
  const isAuthPath = path.startsWith('/login') || path.startsWith('/register');
  const url = isAuthPath ? `${AUTH_URL}${path}` : `${BASE_URL}${path}`;

  const headers = { 
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    ...options.headers 
  };
  
  if (authToken && !isAuthPath) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }

  try {
    const res = await fetch(url, {
      ...options,
      headers,
      signal: controller.signal,
    });
    
    if (res.status === 401 && !isAuthPath) {
      logout();
      throw new Error('Unauthorized');
    }

    if (!res.ok) throw new Error(`HTTP ${res.status} on ${path}`);
    return await res.json();
  } catch (err) {
    const msg = err.name === 'AbortError' ? `Timeout on ${path}` : err.message;
    console.warn(`[ShieldAPI API] ${msg}`);
    throw err;
  } finally {
    clearTimeout(timer);
  }
}

// ─── API Functions ────────────────────────────────────────────────────────────

/**
 * GET /api/dashboard/metrics
 * @returns {{ activeThreats, requestsPerMin, blockedToday, riskScore }}
 */
export async function getDashboardMetrics() {
  return apiFetch('/dashboard/metrics');
}

/**
 * GET /api/dashboard/attacks
 * @returns {Array<{ type, ip, endpoint, severity, timestamp }>}
 */
export async function getRecentAttacks() {
  return apiFetch('/dashboard/attacks');
}

/**
 * GET /api/dashboard/top-offenders
 * @returns {Array<{ ip, requests, status }>}
 */
export async function getTopOffenders() {
  return apiFetch('/dashboard/top-offenders');
}

/**
 * GET /api/dashboard/traffic
 * @returns {Array<{ h, trf, atk, blk }>}
 */
export async function getTrafficData() {
  return apiFetch('/dashboard/traffic');
}

/**
 * POST /auth/login
 */
export async function login(username, password) {
  const data = await apiFetch('/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  });
  if (data.token) setToken(data.token);
  return data;
}

/**
 * POST /auth/register
 */
export async function register(username, password, email, roles = ['VIEWER']) {
  return apiFetch('/register', {
    method: 'POST',
    body: JSON.stringify({ username, password, email, roles })
  });
}

/**
 * GET /api/simulate/{type}
 */
export async function simulateAttack(type) {
  return apiFetch(`/simulate/${type}`);
}
