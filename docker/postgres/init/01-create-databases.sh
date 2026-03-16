#!/usr/bin/env bash
set -euo pipefail

# Runs on first container startup only (when the data directory is empty).
# Uses the superuser defined by POSTGRES_USER/POSTGRES_PASSWORD.

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE DATABASE db1;
  CREATE DATABASE db2;
EOSQL

