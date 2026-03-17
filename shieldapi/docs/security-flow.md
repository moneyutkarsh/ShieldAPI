# Security Processing Flow

This document details how ShieldAPI processes every incoming request to ensure maximum security and minimum latency.

## The Security Pipeline

ShieldAPI implements a "Fail Fast" security model. Requests are evaluated in stages; if any stage fails, the request is rejected immediately, saving downstream resources.

### Step 1: IP Reputation Check (`IpReputationFilter`)
- **Action**: Intercepts the request and checks the client's IP against the `blocked_ips` table.
- **Outcome**: If blocked, returns `403 Forbidden`. If clean, proceeds to the next layer.

### Step 2: Rate Limiting (`RateLimitFilter`)
- **Action**: Uses **Bucket4j** to track requests per IP address.
- **Outcome**: If the bucket is empty (limit exceeded), it records a `RATE_LIMIT_ABUSE` threat and returns `429 Too Many Requests`.

### Step 3: Authentication (`JwtAuthenticationFilter`)
- **Action**: Extracts the `Authorization` header and validates the JWT.
- **Outcome**:
    - **Valid**: Sets the `SecurityContext` with user details and roles.
    - **Missing/Invalid**: Access is denied (unless the endpoint is public, e.g., `/auth/**`).

### Step 4: Authorization
- **Action**: Spring Security checks the authenticated user's roles against the endpoint's requirements (e.g., `ROLE_ADMIN` for analytics).
- **Outcome**: Returns `403 Forbidden` if roles are insufficient.

### Step 5: Audit & Activity Tracking (`ApiAuditInterceptor`)
- **Action**: 
    - `preHandle`: Records the request start time.
    - `afterCompletion`: Captures URI, method, status code, response time, and user ID.
- **Outcome**: Persists an `ApiAuditLog` asynchronously to minimize latency.

## Threat Detection Logic

While the request flows through the filters, the **Threat Detection Engine** works in parallel:

1.  **Detection**: If a filter (or the Auth Failure Handler) detects a violation, it calls `ThreatIntelligenceService.recordThreat()`.
2.  **Scoring**: The `ThreatScoreService` updates the internal "Maliciousness Score" for that IP.
3.  **Blacklisting**: If the score reaches a critical threshold, the `IpReputationService` automatically blacklists the IP for 7 days.
4.  **Streaming**: A detailed `SecurityEvent` is published to the `shieldapi-security-events` Kafka topic.
