# ticketly
Event Ticketing Platform

## 🚀 Quick Start: Running the Stack

Ensure you have Docker installed and running.

### **1. Launch Everything**

Run this single command from the root directory of the `ticketly` repository:

```bash
docker compose up --build
```

Note: To run in the background (detached mode), add the -d flag:
```bash
docker compose up --build -d
```

The stack starts in dependency order: `db` and `redis` first, then `eureka`
once healthy, then `gateway`, `ticketing-service`, and both `payment-service`
replicas once Eureka is accepting registrations. Give it a minute or two on
first run - it's building four Spring Boot modules from source.

### **2. Entry Point**

**All application traffic goes through the gateway at `:8080`.** The gateway
resolves `ticketing-service` and `payment-service` via Eureka service
discovery and load-balances across replicas - you should not need to call
the individual services directly for normal use.

| Gateway route                           | Forwarded to                                                                |
|:----------------------------------------|:----------------------------------------------------------------------------|
| `http://localhost:8080/events/**`       | `ticketing-service` → `/api/events/**`                                      |
| `http://localhost:8080/reservations/**` | `ticketing-service` → `/api/reservations/**`                                |
| `http://localhost:8080/payments/**`     | `payment-service` → `/api/payments/**` (round-robined across both replicas) |

### **3. Port Mappings & Services**

Once the health checks pass and all containers are healthy, the following
are accessible from your host machine. Most of these are for direct
inspection/debugging - normal traffic should go through the gateway above.

| Service                 | Internal Container Port | Host Port | Endpoint / Access URL                                |
|:------------------------|:------------------------|:----------|:-----------------------------------------------------|
| **`gateway`**           | `8080`                  | `8080`    | `http://localhost:8080` (main entry point)           |
| **`eureka`**            | `8761`                  | `8761`    | `http://localhost:8761` (service registry dashboard) |
| **`ticketing-service`** | `8081`                  | `8081`    | `http://localhost:8081/api/events`                   |
| **`payment-service-1`** | `8082`                  | `8082`    | `http://localhost:8082/api/payments`                 |
| **`payment-service-2`** | `8083`                  | `8083`    | `http://localhost:8083/api/payments`                 |
| **`PostgreSQL`**        | `5432`                  | `5432`    | `jdbc:postgresql://localhost:5432/ticketly`          |
| **`Redis`**             | `6379`                  | `6379`    | `redis://localhost:6379`                             |

Check `http://localhost:8761` to confirm all five application services
(`GATEWAY`, `TICKETING-SERVICE`, and two `PAYMENT-SERVICE` instances) show
as `UP` before relying on the gateway routes - traffic sent before Eureka
has synced can occasionally 503 until registration propagates.

---

### **4. Stop & Reset State**

* **Stop services (keep database data):**
  ```bash
  docker compose down
  ```

* **Stop services and wipe the database volume:**
  ```bash
  docker compose down -v
  ```

* **Rebuild from scratch** (useful after Dockerfile or `pom.xml` changes):
  ```bash
  docker compose build --no-cache
  docker compose up
  ```