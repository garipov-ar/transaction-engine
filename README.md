# Transaction Processing Engine

> A production-grade, idempotent transaction processing system built with **Spring Boot 3**, **RabbitMQ**, and **PostgreSQL**. Designed for high-load environments with **dead-letter queue (DLQ)**, **async processing**, and **Docker-first deployment**.

&#x20;   &#x20;

## ✨ Features

- **Idempotent processing** – duplicate transactions (same `externalId`) are safely ignored
- **Dead Letter Queue (DLQ)** – failed messages are automatically routed for inspection
- **Async message-driven architecture** via RabbitMQ
- **One-command local setup** with Docker Compose
- **Production-ready security** (configurable via Spring Security)
- **Observability-ready** – Micrometer metrics (Prometheus-compatible)

## 🚀 Quick Start

### 1. Start infrastructure

```bash
docker-compose up -d
```

> Starts PostgreSQL, RabbitMQ (with management UI), and Redis.

### 2. Run the service

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```

> Uses `application-docker.properties` for DB and RabbitMQ config.

### 3. Submit a transaction

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"externalId":"tx-001","amount":100,"currency":"USD"}'
```

✅ The transaction is:

- Published to RabbitMQ
- Processed asynchronously
- Saved to PostgreSQL with status `COMPLETED`

### 4. Inspect failed messages

Open RabbitMQ Management UI:

👉 [http://localhost:15672](http://localhost:15672)

Credentials: `appuser` / `secretpass`

Queue: `transaction.process.dlq`

## 📦 Tech Stack

| Layer          | Technology                                  |
| -------------- | ------------------------------------------- |
| **Language**   | Java 17                                     |
| **Framework**  | Spring Boot 3, Spring AMQP, Spring Data JPA |
| **Messaging**  | RabbitMQ (with DLX/DLQ, TTL)                |
| **Database**   | PostgreSQL                                  |
| **Infra**      | Docker Compose                              |
| **Monitoring** | Micrometer + Prometheus (pre-configured)    |
| **Security**   | Spring Security (customizable)              |

## 📂 Project Structure

```
transaction-engine/
├── docker-compose.yml
├── transaction-service/
│   └── src/main/java/com/yourname/transactionservice/
│       ├── config/
│       ├── controller/
│       ├── entity/
│       ├── repository/
│       ├── service/
│       └── messaging/
└── README.md
```

## 🔒 Idempotency & Reliability

- Each transaction requires a unique `externalId` (provided by client)
- On receipt, the system checks for existing `externalId` in DB
- If found → skips processing (prevents duplicates)
- If processing fails → message requeued → after TTL → sent to DLQ

This ensures **exactly-once semantic processing** in failure scenarios.

## 📊 Observability

The service exposes:

- `/actuator/health` – liveness & readiness
- `/actuator/prometheus` – metrics (processed count, duration, etc.)

Integrate with Prometheus + Grafana for full monitoring.

## 🧪 Testing DLQ (Optional)

To simulate a failure:

1. Temporarily add:

```java
throw new RuntimeException("test");
```

in `TransactionService.processTransactionMessage()` 2. Send a transaction 3. Wait 5 seconds → message appears in `transaction.process.dlq`

## 📌 Why This Project?

This repository demonstrates:

- Deep understanding of **distributed systems reliability**
- Proficiency in **Spring Boot, RabbitMQ, and PostgreSQL**
- Ability to build **observable, maintainable, cloud-native services**
- Experience with **infrastructure-as-code (Docker Compose)**

Perfect for backend/SRE roles requiring **high-load, fault-tolerant systems**.

## 📄 License

MIT License – see [LICENSE](LICENSE) for details.

---

### ✅ Before Pushing

1. Replace `your-username` in the badge URL with your GitHub username:

```md
![License](https://img.shields.io/github/license/aidar/transaction-engine)
```

2. Save the file as `transaction-engine/README.md`
3. Run:

```bash
git add README.md
git commit -m "docs: add professional English README"
git push origin main
```

💡 *Your repository will now look like a polished, production-ready open-source project. Great work, Айдар!*

