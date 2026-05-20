# Streamcast UI

Angular 17 console for the Streamcast microservices platform.

## Stack
- Angular 17 (standalone components, signals)
- Angular Material + Tailwind CSS
- JWT bearer auth via HTTP interceptor
- Role-based route guards
- Lazy-loaded feature modules

## Prerequisites
- Node.js 18.19+ / 20+
- npm 10+
- All Streamcast backend services running

## First-time setup

```bash
cd "Final Microservice/streamcast-ui"
npm install
```

## Run

```bash
npm start
```
Opens http://localhost:4200. The app talks to the API Gateway on http://localhost:8082.

## Required backend state before login works

1. **MySQL running** (for the various service databases)
2. **Eureka** (Service-Registry) on :8761
3. **Streamcast-Backend** on :8081
4. **API-Gateway** on :8082 — **must be restarted after the latest CORS change**
5. Each feature page also needs its corresponding service running:
   - Titles/Assets/Metadata → Content-Catalog-Service (:1020)
   - Contracts → Contract-Service (:8088)
   - Clauses → Clause-Service (:8089)
   - Partners → Partner-Service (:8087)
   - Manifests → Distribution-Delivery (:8083)
   - Schedules/Conflicts → schedule-service (:1040)
   - Usage → usage-service (:1030)
   - Users/Roles → Streamcast-Backend (:8081)

## Pages

| Path | Roles | Backend |
|---|---|---|
| `/dashboard` | All authenticated | — |
| `/titles`, `/titles/:id` | ADMIN, CONTENT_OWNER, RIGHTS_MANAGER, SCHEDULER | catalog-service |
| `/contracts` | ADMIN, RIGHTS_MANAGER, SCHEDULER, COMPLIANCE_OFFICER | contract-service |
| `/clauses` | ADMIN, RIGHTS_MANAGER, SCHEDULER, COMPLIANCE_OFFICER | clause-service |
| `/partners` | ADMIN, DISTRIBUTION_OPERATOR, PARTNER_ADMIN, RIGHTS_MANAGER | partner-service |
| `/manifests` | ADMIN, DISTRIBUTION_OPERATOR, PARTNER_ADMIN | distribution-delivery |
| `/schedules` | ADMIN, SCHEDULER, RIGHTS_MANAGER, COMPLIANCE_OFFICER | schedule-service |
| `/conflicts` | ADMIN, SCHEDULER, COMPLIANCE_OFFICER | schedule-service |
| `/usage` | ADMIN, COMPLIANCE_OFFICER, SCHEDULER | usage-service |
| `/users` | ADMIN | streamcast-backend |
| `/roles` | ADMIN | streamcast-backend |

## Troubleshooting

**Login error: "Cannot reach the gateway"**
→ API-Gateway is not running or CORS is missing. Restart API-Gateway after pulling the latest `application.properties`.

**Login error: "Invalid email or password"**
→ User does not exist or password is wrong.

**Some pages 403**
→ Your role doesn't have access. Sign in as ADMIN to see everything.

**Some pages 500 / empty**
→ The downstream microservice is not running (see ports above) or its DB is unreachable.

## Build for production

```bash
npm run build
```
Outputs to `dist/streamcast-ui/`.
