# PostgreSQL Setup Guide

This guide will help you set up PostgreSQL for the Template Java application.

## Quick Start with Docker (Recommended)

### 1. Start PostgreSQL with Docker Compose

```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Check if containers are running
docker-compose ps
```

This will start:
- **PostgreSQL** on port `5432`
- **pgAdmin** on port `8080` (web interface)

### 2. Access pgAdmin (Optional)

- URL: http://localhost:8080
- Email: `admin@example.com`
- Password: `admin`

To connect to PostgreSQL in pgAdmin:
- Host: `postgres` (container name)
- Port: `5432`
- Database: `template_java`
- Username: `postgres`
- Password: `password`

### 3. Run the Application

```bash
# With default profile (uses PostgreSQL configuration)
mvn spring-boot:run

# With development profile (recommended for development)
mvn spring-boot:run -Dspring.profiles.active=dev

# With production profile
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Manual PostgreSQL Installation

### Option 1: Install PostgreSQL locally

#### On macOS:
```bash
# Install PostgreSQL
brew install postgresql

# Start PostgreSQL service
brew services start postgresql

# Create database
createdb template_java
```

#### On Ubuntu/Debian:
```bash
# Install PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database
sudo -u postgres createdb template_java
```

#### On Windows:
1. Download PostgreSQL installer from https://www.postgresql.org/download/windows/
2. Run the installer and follow the setup wizard
3. Create a database named `template_java`

### Option 2: Use PostgreSQL cloud service

You can use cloud PostgreSQL services like:
- **AWS RDS**
- **Google Cloud SQL**
- **Azure Database for PostgreSQL**
- **Heroku Postgres**
- **ElephantSQL**

## Configuration

### Environment Variables

You can override database configuration using environment variables:

```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export DATABASE_URL=jdbc:postgresql://your-host:5432/your_database
export JWT_SECRET=your-secret-key
```

### Application Profiles

The application supports different profiles:

#### Development Profile (`dev`)
- Uses `create-drop` DDL strategy (recreates schema on restart)
- Enables SQL logging and debug information
- Shorter JWT token expiration for testing

```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

#### Production Profile (`prod`)
- Uses `validate` DDL strategy (doesn't modify schema)
- Disables SQL logging for performance
- Longer JWT token expiration
- Connection pooling optimized for production

```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Database Schema

The application uses Hibernate to automatically create/update the database schema based on your JPA entities.

### DDL Strategies:

- **create-drop**: Drops and recreates schema on each restart (dev profile)
- **update**: Updates schema when entities change (default profile)
- **validate**: Only validates schema, doesn't modify it (prod profile)

## Connection Settings

### Default Configuration:
- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `template_java`
- **Username**: `postgres`
- **Password**: `password`

### Connection Pool Settings (Production):
- **Maximum Pool Size**: 20
- **Minimum Idle**: 5
- **Idle Timeout**: 5 minutes
- **Connection Timeout**: 20 seconds
- **Max Lifetime**: 20 minutes

## Troubleshooting

### Common Issues:

1. **Connection refused**
   - Ensure PostgreSQL is running
   - Check if port 5432 is available
   - Verify connection credentials

2. **Database doesn't exist**
   ```bash
   # Create database manually
   createdb template_java
   ```

3. **Permission denied**
   - Ensure the user has proper permissions
   - Grant necessary privileges:
   ```sql
   GRANT ALL PRIVILEGES ON DATABASE template_java TO postgres;
   ```

4. **Docker issues**
   ```bash
   # Stop and remove containers
   docker-compose down
   
   # Remove volumes (WARNING: This will delete all data)
   docker-compose down -v
   
   # Restart
   docker-compose up -d
   ```

### Useful Commands:

```bash
# Connect to PostgreSQL via command line
psql -h localhost -p 5432 -U postgres -d template_java

# Check database connection
docker-compose exec postgres psql -U postgres -d template_java -c "SELECT version();"

# View logs
docker-compose logs postgres
docker-compose logs -f postgres  # Follow logs

# Stop services
docker-compose stop

# Remove everything (including data)
docker-compose down -v
```

## Security Notes

### For Production:

1. **Change default passwords**
2. **Use environment variables for sensitive data**
3. **Enable SSL/TLS connections**
4. **Restrict database access by IP**
5. **Use a dedicated database user with limited privileges**
6. **Regular backups**

### SSL Configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/template_java?ssl=true&sslmode=require
```

## Backup and Restore

### Backup:
```bash
# Create backup
pg_dump -h localhost -U postgres template_java > backup.sql

# With Docker
docker-compose exec postgres pg_dump -U postgres template_java > backup.sql
```

### Restore:
```bash
# Restore from backup
psql -h localhost -U postgres template_java < backup.sql

# With Docker
docker-compose exec -T postgres psql -U postgres template_java < backup.sql
``` 