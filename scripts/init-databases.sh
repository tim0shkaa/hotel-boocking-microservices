#!/bin/bash
set -e

create_database() {
    local database=$1
    echo "Creating database: $database"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -c "CREATE DATABASE $database;"
}

create_database booking_db
create_database auth_db
create_database notification_db
create_database payment_db
create_database review_db