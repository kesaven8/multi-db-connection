# multi-db-connection

Spring Boot sample project that demonstrates how to connect one application to **two PostgreSQL databases**.

The application is currently centered around an `Invoice` feature that stores and reads invoice data from the **second datasource** (`db2`), while still keeping the default Spring datasource configuration in place for the **primary datasource** (`db1`).

## What this project does

This project shows how to:

- use the default `spring.datasource.*` properties for the primary database
- define a separate custom datasource for a second database
- configure a separate JPA `EntityManagerFactory` and transaction manager for the second datasource
- expose REST endpoints for creating and retrieving invoices from the second database
- use pagination with Spring Data JPA
- start PostgreSQL with Docker and initialize two databases automatically
- seed `db2` with **1,000,000 invoice rows** using PostgreSQL `generate_series`

## Tech stack

- Java 21
- Spring Boot 4.0.2
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Lombok
- Maven

## High-level architecture

### Datasources

The application defines two PostgreSQL connections:

1. **Primary datasource**
   - configured with `spring.datasource.*`
   - points to `db1`
   - file: `src/main/resources/application.yml`

2. **Secondary datasource**
   - configured with `app.datasource.db2.*`
   - points to `db2`
   - wired manually in `src/main/java/com/example/multi_db_connection/config/Db2Config.java`

`Invoice` is stored in **`db2`**, not in the default datasource.

## Configuration


### Secondary datasource JPA config

`Db2Config` does the following:

- binds `app.datasource.db2.*` into `DataSourceProperties`
- creates a dedicated `db2DataSource`
- creates a dedicated `db2EntityManagerFactory`
- scans `com.example.multi_db_connection.db2` for JPA entities and repositories
- creates a dedicated `db2TransactionManager`

It also sets:

- `hibernate.hbm2ddl.auto=update`
- PostgreSQL dialect

## Docker setup

The root `docker-compose.yml` starts PostgreSQL with:

- image: `postgres:17.5-alpine3.20`
- container name: `multi-db-postgres`
- host port: `5433`
- username: `admin`
- password: `admin`

### Docker init flow

On the first startup of the PostgreSQL container, the scripts under `docker/postgres/init` run automatically.

#### `01-create-databases.sh`
Creates:

- `db1`
- `db2`

#### `02-seed-db2-invoice.sql`
In `db2`, this script:

- enables `pgcrypto`
- creates the `invoice` table if it does not exist
- inserts **1,000,000 rows** using `generate_series`
- runs `ANALYZE invoice`

Example generated names:

- `invoice-1`
- `invoice-2`
- `...`
- `invoice-1000000`

### Important Docker behavior

The init scripts run **only when the Postgres data volume is empty**.

If the container has already started once, Docker will reuse the existing volume and the init scripts will not run again automatically.

To force a clean re-initialization:

```powershell
docker compose down -v
docker compose up -d
```

## Running the project

### 1. Start PostgreSQL with Docker

```powershell
docker compose up -d
```

If you want the databases and seed data recreated from scratch:

```powershell
docker compose down -v
docker compose up -d
```

The entity uses:

- `@GeneratedValue(strategy = GenerationType.UUID)`

When data is inserted through Docker seeding, PostgreSQL generates UUIDs directly using `gen_random_uuid()`.

```

## Tests

The project currently includes focused tests for the invoice pagination flow:

- `src/test/java/com/example/multi_db_connection/controller/InvoiceControllerTest.java`
- `src/test/java/com/example/multi_db_connection/service/InvoiceServiceTest.java`

Run tests with:

```powershell
mvn test
```

## Current limitations and notes

- The primary datasource (`db1`) is configured, but there is currently no active entity/repository flow using it.
- The invoice feature uses only the secondary datasource (`db2`).
- Request validation is minimal: `CreateInvoiceRequest` does not currently enforce bean validation annotations such as `@NotBlank`.
- `Db2Config` currently uses `hibernate.hbm2ddl.auto=update`, even though Docker init also creates the `invoice` table.
- The Docker seed loads **1,000,000 rows**, so the first container start can take noticeably longer than a normal Postgres startup.
- Docker seeding happens only on a fresh volume.
