#!/bin/bash
set -e

# Create the systems database
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE systems;
    CREATE USER systems WITH PASSWORD 'systems';
    GRANT ALL PRIVILEGES ON DATABASE systems TO systems;
EOSQL

# Connect to the systems database and grant privileges on the public schema
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "systems" <<-EOSQL
    GRANT ALL PRIVILEGES ON SCHEMA public TO systems;
EOSQL