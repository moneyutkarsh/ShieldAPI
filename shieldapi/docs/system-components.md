# System Components & Responsibilities

ShieldAPI is built on a modular architecture where each component has a clearly defined responsibility. This document provides a high-level overview of these components.

## 1. Security Filters

| Component | Responsibility |
| :--- | :--- |
| `IpReputationFilter` | Immediate rejection of requests from blacklisted IP addresses. |
| `RateLimitFilter` | Enforces client-specific request quotas using **Bucket4j**. |
| `JwtAuthenticationFilter` | Extracts and validates JWT tokens; manages the **SecurityContext**. |

## 2. Security Services

| Component | Responsibility |
| :--- | :--- |
| `ThreatIntelligenceService` | Orchestrates threat detection, recording, and scoring. |
| `ThreatScoreService` | Calculates IP-based threat scores using weighted risk categories. |
| `IpReputationService` | Logic for blacklisting and unblocking IP addresses. |
| `RateLimitService` | Manages and resolves request buckets for various client IPs. |

## 3. Monitoring & Analytics

| Component | Responsibility |
| :--- | :--- |
| `SecurityAnalyticsController` | Provides secure endpoints for admin monitoring. |
| `SecurityAnalyticsService` | Aggregates threat data and audit logs for the analytics API. |
| `ApiAuditInterceptor` | Intercepts requests/responses to log audit data synchronously and then persists it asynchronously. |
| `SecurityEventPublisher` | Publishes structured security events to **Apache Kafka**. |
| `SecurityMetricsService` | Tracks in-memory counters for real-time dashboarding. |

## 4. Repositories & Entities

- **`ThreatEvent`**: Entity for logging specific security violations.
- **`BlockedIpAddress`**: Entity for persistent blacklisting data.
- **`ApiAuditLog`**: Entity for general request/response auditing.
- **`User`**: Entity for application identity and role-based access control.

## 5. Interaction Examples

### Threat Mitigation Flow
1.  `CustomAuthenticationFailureHandler` detects a brute force attempt.
2.  It notifies `ThreatIntelligenceService`.
3.  `ThreatIntelligenceService` updates the `ThreatEventRepository` and calls `ThreatScoreService`.
4.  If the score is too high, `IpReputationService` blacklists the IP.
5.  `SecurityEventPublisher` streams the event to Kafka for external monitoring.
