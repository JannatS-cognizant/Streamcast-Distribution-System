# StreamCast - Integrated Microservices

## Service Map

| Service | Eureka Name | Port | Database | Owner |
|---|---|---|---|---|
| Service-Registry | - | 8761 | - | Team |
| API-Gateway | API-Gateway | 8082 | - | Team |
| **Identity-Auth-Service** | Identity-Auth-Service | 8081 | streamcast_database | **Swastika** |
| **Compliance-Service** | Compliance-Service | 8090 | streamcast_database | **Swastika** |
| Content-Catalog-Service | catalog-service | 1020 | content_catalog_database | Arunachalam |
| schedule-service | schedule-service | 1040 | schedule_database | Nithin |
| usage-service | usage-service | 1030 | usage_database | Nithin |
| Distribution-Delivery | Distribution-Delivery | 8083 | distribution_delivery_database | Jannat |
| Contract-Service | Contract-Service | 8088 | contract_database | Afrin |
| Clause-Service | Clause-Service | 8089 | clause_database | Afrin |
| Partner-Service | Partner-Service | 8087 | partner_database | Afrin |

> Note: Streamcast-Backend is kept in this zip for reference only.
> Identity-Auth-Service replaces it completely with a cleaner architecture.

---

## MySQL Setup

Run this once in MySQL Workbench before starting any service:

```sql
CREATE DATABASE IF NOT EXISTS streamcast_database;
CREATE DATABASE IF NOT EXISTS content_catalog_database;
CREATE DATABASE IF NOT EXISTS schedule_database;
CREATE DATABASE IF NOT EXISTS usage_database;
CREATE DATABASE IF NOT EXISTS distribution_delivery_database;
CREATE DATABASE IF NOT EXISTS contract_database;
CREATE DATABASE IF NOT EXISTS clause_database;
CREATE DATABASE IF NOT EXISTS partner_database;
```

Then seed the roles (needed before first registration):
```sql
USE streamcast_database;
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('CONTENT_OWNER');
INSERT INTO roles (name) VALUES ('RIGHTS_MANAGER');
INSERT INTO roles (name) VALUES ('SCHEDULER');
INSERT INTO roles (name) VALUES ('DISTRIBUTION_OPERATOR');
INSERT INTO roles (name) VALUES ('LEGAL_OFFICER');
```

---

## Startup Order

Start services in this exact sequence. Wait for each to show "Tomcat started" before the next.

```
1. Service-Registry      (port 8761)  — wait 15 seconds
2. Identity-Auth-Service (port 8081)
3. Content-Catalog-Service (port 1020)
4. Compliance-Service    (port 8090)
5. schedule-service      (port 1040)
6. usage-service         (port 1030)
7. Distribution-Delivery (port 8083)
8. Contract-Service      (port 8088)
9. Clause-Service        (port 8089)
10. Partner-Service      (port 8087)
11. API-Gateway          (port 8082)  — ALWAYS LAST
```

Verify Eureka: http://localhost:8761 — all services should appear.

---

## Email Configuration

Before running Identity-Auth-Service, update its application.properties:

```properties
spring.mail.username=your_real_gmail@gmail.com
spring.mail.password=your_16_char_app_password
```

To get app password: Gmail > Google Account > Security > 2-Step Verification > App passwords.

If email is not configured, verification tokens print to the Eclipse console — the service still works.

---

## API Gateway Routes

All requests go through port 8082.

| Path Pattern | Routes To |
|---|---|
| /auth/** | Identity-Auth-Service |
| /users/** | Identity-Auth-Service |
| /roles/** | Identity-Auth-Service |
| /manifests/** | Distribution-Delivery |
| /api/usage/** | usage-service |
| /api/schedules/**, /api/conflicts/** | schedule-service |
| /clauses/** | Clause-Service |
| /partners/**, /accessLogs/** | Partner-Service |
| /contracts/** | Contract-Service |
| /api/titles/**, /api/assets/**, /api/metadata/** | Content-Catalog-Service |
| /compliance/** | Compliance-Service |
| /api/notifications/**, /api/alerts-rules/** | Notification-Service (pending) |

---

## Changes Made During Integration

1. **Identity-Auth-Service** — DB updated to `streamcast_database`, password `ctssql2026`, Eureka enabled
2. **Compliance-Service** — DB updated to `streamcast_database`, password `ctssql2026`
3. **API-Gateway routes** — Routes 0-2 now point to `Identity-Auth-Service` instead of `Streamcast-Backend`
4. **API-Gateway routes** — Route 5 now covers `/api/conflicts/**` in addition to `/api/schedules/**`
5. **API-Gateway routes** — Route 9 fixed to use specific paths instead of `/api/**` (which was too broad)
6. **API-Gateway routes** — Route 10 added for `Compliance-Service`
7. **API-Gateway routes** — Route 11 added for `Notification-Service` (ready for Arunachalam)
8. **JwtAuthFilter** — Added `/auth/verify`, `/auth/forgot-password`, `/auth/reset-password`, `/auth/forgot-username` to PUBLIC list

---

## Pending (Arunachalam)

Notification-Service is not yet in this package.
Once added, it should register with Eureka as `Notification-Service` on port `8086`.
Gateway Route 11 is already configured for it.
