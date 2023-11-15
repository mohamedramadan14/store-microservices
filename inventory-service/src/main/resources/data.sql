SELECT 'CREATE DATABASE inventory_service'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inventory_service')\gexec