# 💳 Payment & Invoice Service

A minimal **invoice and payment service** built with **Java, Spring Boot, PostgreSQL, and Liquibase**.
Designed to demonstrate real-world payment flows including idempotency, webhooks, and invoice lifecycle management.

---

## 🚀 Features

* 🏢 Multi-tenant business management
* 🔑 API Key authentication
* 👥 Customer management
* 📄 Invoice creation with line items
* 💳 Payment processing with Mock PSP
* 🔄 Idempotent payments
* 📊 Invoice state machine (`DRAFT → OPEN → PAID`)
* 🔔 Webhook notifications with retry logic
* 📝 Webhook delivery history tracking

---

## 🧰 Tech Stack

* **Java 17**
* **Spring Boot 3.5.14**
* **PostgreSQL 13.23**
* **Liquibase** (DB migrations)
* **Docker**

---

## 📦 Prerequisites

Make sure you have installed:

* Java 17+
* Maven
* Docker & Docker Compose

---

## ⚡ Quick Start

### 1. Start PostgreSQL

```bash
docker run -d --name postgres \
  -e POSTGRES_DB=payment_and_invoice_service \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:13.23
```

### 2. Run the Application

```bash
mvn clean compile
mvn spring-boot:run
```

App will start at: `http://localhost:8080`

---

## 🔐 Authentication

All endpoints require an API key via header:

```http
X-API-Key: billing_...
```

---

## 📡 API Endpoints

### 🏢 Businesses

| Method | Endpoint             | Description       |
| ------ | -------------------- | ----------------- |
| POST   | `/api/v1/businesses` | Create a business |

---

### 🔑 API Keys

| Method | Endpoint                | Description      |
| ------ | ----------------------- | ---------------- |
| POST   | `/api/v1/keys/generate` | Generate API key |

---

### 👥 Customers

| Method | Endpoint                 | Description     |
| ------ | ------------------------ | --------------- |
| POST   | `/api/v1/customers`      | Create customer |
| GET    | `/api/v1/customers`      | List customers  |
| GET    | `/api/v1/customers/{id}` | Get customer    |

---

### 📄 Invoices

| Method | Endpoint                    | Description    |
| ------ | --------------------------- | -------------- |
| POST   | `/api/v1/invoices`          | Create invoice |
| GET    | `/api/v1/invoices`          | List invoices  |
| GET    | `/api/v1/invoices/{id}`     | Get invoice    |
| POST   | `/api/v1/invoices/{id}/pay` | Pay invoice    |

---

### 🔔 Webhooks

| Method | Endpoint                          | Description      |
| ------ | --------------------------------- | ---------------- |
| POST   | `/api/v1/webhooks/endpoints`      | Register webhook |
| GET    | `/api/v1/webhooks/endpoints`      | List endpoints   |
| DELETE | `/api/v1/webhooks/endpoints/{id}` | Delete endpoint  |
| GET    | `/api/v1/webhooks/deliveries`     | Delivery history |

---

## 🧪 cURL Examples

### 1. Create a Business

```bash
curl -X POST http://localhost:8080/api/v1/businesses \
  -H "Content-Type: application/json" \
  -d '{"name": "My Company"}'
```

---

### 2. Generate API Key

```bash
curl -X POST http://localhost:8080/api/v1/keys/generate \
  -H "Content-Type: application/json" \
  -d '{"businessId": 1, "keyName": "Production"}'
```

⚠️ **Save the API key — it will not be shown again!**

---

### 3. Create Customer

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

---

### 4. Create Invoice

```bash
curl -X POST http://localhost:8080/api/v1/invoices \
  -H "Content-Type: application/json" \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -d '{
    "customerId": 1,
    "dueDate": "2024-12-31T23:59:59",
    "itemRequests": [
      {
        "description": "Professional Services",
        "quantity": 10,
        "unitAmountCents": 5000
      },
      {
        "description": "Software License",
        "quantity": 1,
        "unitAmountCents": 10000
      }
    ]
  }'
```

---

### 5. Pay Invoice (Success)

```bash
curl -X POST http://localhost:8080/api/v1/invoices/1/pay \
  -H "Content-Type: application/json" \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -H "Idempotency-Key: payment-001" \
  -d '{"cardToken": "tok_success"}'
```

---

### 6. Pay Invoice (Failure)

```bash
curl -X POST http://localhost:8080/api/v1/invoices/2/pay \
  -H "Content-Type: application/json" \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -H "Idempotency-Key: payment-002" \
  -d '{"cardToken": "tok_insufficient_funds"}'
```

---

### 7. Idempotency Test

```bash
# First request
curl -X POST http://localhost:8080/api/v1/invoices/1/pay \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -H "Idempotency-Key: payment-001"

# Second request (same key → no double charge)
curl -X POST http://localhost:8080/api/v1/invoices/1/pay \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -H "Idempotency-Key: payment-001"
```

---

### 8. Register Webhook

```bash
curl -X POST http://localhost:8080/api/v1/webhooks/endpoints \
  -H "Content-Type: application/json" \
  -H "X-API-Key: billing_YOUR_API_KEY" \
  -d '{
    "url": "https://webhook.site/your-id",
    "description": "My webhook"
  }'
```

---

### 9. View Webhook Deliveries

```bash
curl -X GET http://localhost:8080/api/v1/webhooks/deliveries \
  -H "X-API-Key: billing_YOUR_API_KEY"
```

---

## 💳 Mock PSP Tokens

| Token                    | Behavior         |
| ------------------------ | ---------------- |
| `tok_success`            | Payment succeeds |
| `tok_insufficient_funds` | Payment fails    |
| `tok_card_declined`      | Card declined    |
| `tok_timeout`            | Delayed success  |
| `tok_network_error`      | Network failure  |

---

## 🔄 Invoice State Machine

```
DRAFT → OPEN → PAID
  ↓       ↓
 VOID   VOID
```

| State         | Description         |
| ------------- | ------------------- |
| DRAFT         | Not payable         |
| OPEN          | Ready for payment   |
| PAID          | Paid successfully   |
| VOID          | Cancelled           |
| UNCOLLECTIBLE | Cannot be collected |

---

## 🔔 Webhook Events

| Event                    | Trigger         |
| ------------------------ | --------------- |
| `invoice.created`        | Invoice created |
| `invoice.paid`           | Payment success |
| `invoice.payment_failed` | Payment failed  |
| `invoice.voided`         | Invoice voided  |

---

## 🔁 Webhook Retry Policy

| Attempt | Delay         |
| ------- | ------------- |
| 1       | Immediate     |
| 2       | 1 minute      |
| 3       | 5 minutes     |
| 4       | 15 minutes    |
| 5       | 30 minutes    |
| Final   | Stop retrying |

---

## 🗄️ Database Schema

Managed via Liquibase.

Tables:

* `businesses` – Business accounts
* `api_keys` – API authentication
* `customers` – Customers per business
* `invoices` – Invoice records
* `invoice_items` – Line items
* `payment_attempts` – Payment history
* `webhook_endpoints` – Registered URLs
* `webhook_deliveries` – Delivery logs

---

## 📌 Notes

* Payments are **idempotent** using `Idempotency-Key`
* API keys are **write-once visible**
* Webhooks include **retry + delivery tracking**

---

<!--
## 🧑‍💻 License

MIT License (or your preferred license)

---
--!>
