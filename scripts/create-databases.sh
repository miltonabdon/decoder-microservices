#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE authuser_db;
    CREATE DATABASE course_db;
    CREATE DATABASE notification_db;
    GRANT ALL PRIVILEGES ON DATABASE authuser_db TO decoder;
    GRANT ALL PRIVILEGES ON DATABASE course_db TO decoder;
    GRANT ALL PRIVILEGES ON DATABASE notification_db TO decoder;
EOSQL
