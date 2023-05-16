#!/bin/bash
set -e

# Note that this script is only run once by the docker image to prevent overwriting of data

# Create the systems and game databases
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE systems;
    CREATE USER systems WITH PASSWORD 'systems';
    GRANT ALL PRIVILEGES ON DATABASE systems TO systems;

    CREATE DATABASE games;
    CREATE USER games WITH PASSWORD 'games';
    GRANT ALL PRIVILEGES ON DATABASE games TO games;
EOSQL

# Connect to the systems database and grant privileges on the public schema
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "systems" <<-EOSQL
    GRANT ALL PRIVILEGES ON SCHEMA public TO systems;
EOSQL

# Connect to the systems database and grant privileges on the public schema
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "games" <<-EOSQL
    GRANT ALL PRIVILEGES ON SCHEMA public TO games;
EOSQL