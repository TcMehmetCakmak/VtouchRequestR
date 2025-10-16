-- Database initialization script for Template Java application
-- This script will be executed when the PostgreSQL container starts for the first time

-- Create the main database (already created by POSTGRES_DB environment variable)
-- CREATE DATABASE template_java_db;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS app_schema;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE template_java_db TO postgres;

-- Note: Demo data will be automatically inserted by the DataLoader component
-- when the Spring Boot application starts in development profiles (dev, local) 