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

### **2. Port Mappings & Services**

Once the health checks pass and all containers are healthy, the following services will be accessible from your host machine:

| Service | Internal Container Port | Host Port | Endpoint / Access URL |
| :--- | :--- | :--- | :--- |
| **`ticketing-service`** | `8080` (or `8081`) | `8081` | `http://localhost:8081/api/events` |
| **`PostgreSQL`** | `5432` | `5432` | `jdbc:postgresql://localhost:5432/ticketly` |
| **`Redis`** | `6379` | `6379` | `redis://localhost:6379` |

---

### **3. Stop & Reset State**

* **Stop services (keep database data):**
  ```bash
  docker compose down