# ShieldAPI Threat Model

ShieldAPI is designed to mitigate common web-based attack vectors through intelligent detection and automated response.

## Mitigated Threats

### 1. Brute Force Attacks
- **Description**: Repeated attempts to guess credentials on authentication endpoints.
- **Detection**: `CustomAuthenticationFailureHandler` triggers after every failed login attempt.
- **Mitigation**: 
    - Each failure increases the IP's threat score.
    - High-frequency failures trigger an automatic IP block.

### 2. Excessive Request Flooding (DoS)
- **Description**: Flooding the API with requests to exhaust resources and cause denial of service.
- **Detection**: `RateLimitFilter` monitors the request rate per IP.
- **Mitigation**: 
    - Immediate `429 Too Many Requests` response.
    - Repeated violations lead to longer-term blacklisting.

### 3. Suspicious Endpoint Scanning
- **Description**: Probing for sensitive endpoints or known vulnerabilities (e.g., trying to access admin paths without authority).
- **Detection**: Security filters and controllers monitor for repeated `401` or `403` responses.
- **Mitigation**: Automatically records a `THREAT_DETECTED` event with the targeted endpoint.

### 4. Credential Stuffing
- **Description**: Using lists of leaked credentials to gain unauthorized access.
- **Detection**: Monitored via the `AUTH_FAILURE` event stream in Kafka.
- **Mitigation**: Real-time alerts to security teams via the event stream for coordinated response.

## Defensive Strategies

| Defense Layer | Threat Category | Action Taken |
| :--- | :--- | :--- |
| **IP Reputation** | Known Malicious Sources | Direct Block (Hard Drop) |
| **Rate Limiter** | Bot Activity / Scraping | Request Throttling |
| **JWT Validator** | Unauthorized Access | Identity Verification |
| **Threat Scorer** | Coordinated Attacks | Behavioral Analysis & Dynamic Blocking |

## Event Severity Levels
- **LOW**: Minor rate limit hits or single malformed requests.
- **MEDIUM**: Multiple auth failures or repeated scanning.
- **HIGH**: Threshold reached – IP blacklisted.
- **CRITICAL**: Immediate system-wide alerts (via Kafka).
