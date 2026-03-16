# Docker PostgreSQL setup

This project starts PostgreSQL with Docker and initializes two databases:

- `db1`
- `db2`

On the first startup of the `postgres` container, the init scripts under `docker/postgres/init` run in order:

1. `01-create-databases.sh` creates `db1` and `db2`
2. `02-seed-db2-invoice.sql` creates the `invoice` table in `db2` and inserts `1,000,000` rows with `generate_series`

## Important behavior

The seed runs only when the Docker volume is empty. If the container has already been started once, Docker will reuse the existing `postgres-data` volume and the init scripts will not run again.

To force a full re-init and re-seed:

```powershell
docker compose down -v
docker compose up -d
```

## Seeded table

The seeded table matches the JPA entity shape used by the second datasource:

- table: `invoice`
- columns: `id UUID`, `name VARCHAR(255)`, `description VARCHAR(255)`

The inserted rows look like:

- `invoice-1`
- `invoice-2`
- ...
- `invoice-1000000`

