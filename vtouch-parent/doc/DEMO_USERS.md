# Demo Users for Testing

This document contains the demo user credentials for testing the JWT authentication system.

## 🔑 Demo User Credentials

### Admin User
- **Username**: `admin`
- **Email**: `admin@example.com`
- **Password**: `admin123`
- **Role**: `ADMIN`
- **Status**: `ACTIVE`
- **Access**: Full administrative access to all endpoints

### Regular User
- **Username**: `user`
- **Email**: `user@example.com`
- **Password**: `user123`
- **Role**: `USER`
- **Status**: `ACTIVE`
- **Access**: Basic user access, can access own profile

### Moderator User
- **Username**: `moderator`
- **Email**: `moderator@example.com`
- **Password**: `mod123`
- **Role**: `MODERATOR`
- **Status**: `ACTIVE`
- **Access**: Moderate content and manage users

### Additional Test Users

#### John Doe
- **Username**: `john.doe`
- **Email**: `john.doe@example.com`
- **Password**: `user123`
- **Role**: `USER`
- **Status**: `ACTIVE`

#### Jane Smith
- **Username**: `jane.smith`
- **Email**: `jane.smith@example.com`
- **Password**: `mod123`
- **Role**: `MODERATOR`
- **Status**: `ACTIVE`

#### Inactive User (for testing)
- **Username**: `inactive`
- **Email**: `inactive@example.com`
- **Password**: `user123`
- **Role**: `USER`
- **Status**: `INACTIVE`
- **Note**: This user cannot log in due to inactive status

---

## 🚀 How to Test Login

### 1. Start the Application
```bash
# Start PostgreSQL
docker-compose up -d

# Start the application with local profile (loads demo data)
mvn spring-boot:run -Dspring.profiles.active=local
```

### 2. Access Swagger UI
Open your browser and go to: http://localhost:7070/api/swagger-ui.html

### 3. Test Authentication

#### Option A: Using Swagger UI
1. Go to the **Authentication** section
2. Click on **POST /auth/login**
3. Click "Try it out"
4. Use one of the demo credentials:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. Click "Execute"
6. Copy the `accessToken` from the response
7. Click the **🔒 Authorize** button at the top
8. Enter: `Bearer <your-access-token>`
9. Now you can test protected endpoints!

#### Option B: Using cURL
```bash
# Login as admin
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Use the returned token for protected endpoints
curl -X GET http://localhost:7070/api/auth/me \
  -H "Authorization: Bearer <your-access-token>"
```

#### Option C: Using Postman
1. Create a new POST request to `http://localhost:7070/api/auth/login`
2. Set Content-Type to `application/json`
3. Add request body with demo credentials
4. Send the request and copy the `accessToken`
5. For protected endpoints, add Authorization header: `Bearer <token>`

---

## 🔐 Role-Based Access Testing

### Admin Endpoints (requires ADMIN role)
- `GET /api/admin/**` - Admin-only endpoints
- `GET /api/users/statistics` - User statistics
- `PATCH /api/users/{id}/status` - Change user status

**Test with**: `admin` user

### Moderator Endpoints (requires MODERATOR or ADMIN role)
- `GET /api/users/**` - User management endpoints
- `GET /api/users/statistics` - User statistics

**Test with**: `moderator`, `jane.smith`, or `admin` users

### User Endpoints (requires authentication)
- `GET /api/auth/me` - Get current user info
- `GET /api/users/me` - Get own profile

**Test with**: Any active user (`admin`, `user`, `moderator`, etc.)

### Public Endpoints (no authentication required)
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register new user
- `GET /api/actuator/health` - Health check
- `GET /api/swagger-ui.html` - Swagger documentation

---

## 📋 Testing Scenarios

### 1. Successful Login
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```
**Expected**: 200 OK with JWT tokens

### 2. Invalid Credentials
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "wrong"}'
```
**Expected**: 401 Unauthorized

### 3. Inactive User Login
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "inactive", "password": "user123"}'
```
**Expected**: 401 Unauthorized (account disabled)

### 4. Access Protected Endpoint Without Token
```bash
curl -X GET http://localhost:7070/api/auth/me
```
**Expected**: 401 Unauthorized

### 5. Access Protected Endpoint With Token
```bash
curl -X GET http://localhost:7070/api/auth/me \
  -H "Authorization: Bearer <your-token>"
```
**Expected**: 200 OK with user information

### 6. Access Admin Endpoint as Regular User
```bash
# Login as regular user first, then:
curl -X GET http://localhost:7070/api/admin/users \
  -H "Authorization: Bearer <user-token>"
```
**Expected**: 403 Forbidden

### 7. Token Refresh
```bash
curl -X POST http://localhost:7070/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<your-refresh-token>"}'
```
**Expected**: 200 OK with new access token

---

## 🔄 Data Loading

### Automatic Loading
Demo users are automatically created when the application starts with `dev` or `local` profiles:
- Only runs if the database is empty
- Uses BCrypt password encoding
- Logs creation details to the console

### Manual Loading
If you need to recreate demo data:

1. **Clear existing data** (optional):
   ```sql
   -- Connect to PostgreSQL
   psql -h localhost -U postgres -d template_java_db
   
   -- Delete existing users
   DELETE FROM users;
   
   -- Reset sequence
   ALTER SEQUENCE users_id_seq RESTART WITH 1;
   ```

2. **Restart application** with dev/local profile:
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=local
   ```

### Using SQL Script
You can also use the provided `demo-data.sql` script:
```bash
# Execute the demo data script
psql -h localhost -U postgres -d template_java_db -f demo-data.sql
```

---

## 🐛 Troubleshooting

### Demo Data Not Loading
- Check that you're using `dev` or `local` profile
- Verify PostgreSQL is running and accessible
- Check application logs for DataLoader messages
- Ensure database is empty (DataLoader skips if users exist)

### Login Fails
- Verify user exists in database
- Check user status is ACTIVE
- Confirm password is correct
- Check application logs for authentication errors

### Token Issues
- Verify token format: `Bearer <token>`
- Check token expiration (1 hour for dev/local profiles)
- Ensure JWT secret is configured correctly

### Database Connection Issues
- Verify PostgreSQL is running: `docker-compose ps`
- Check database exists: `template_java_db`
- Verify connection settings in application.yml

---

## 📊 Demo Data Summary

| Username | Email | Password | Role | Status | Description |
|----------|-------|----------|------|--------|-------------|
| admin | admin@example.com | admin123 | ADMIN | ACTIVE | Full admin access |
| user | user@example.com | user123 | USER | ACTIVE | Basic user |
| moderator | moderator@example.com | mod123 | MODERATOR | ACTIVE | Content moderator |
| john.doe | john.doe@example.com | user123 | USER | ACTIVE | Additional test user |
| jane.smith | jane.smith@example.com | mod123 | MODERATOR | ACTIVE | Additional moderator |
| inactive | inactive@example.com | user123 | USER | INACTIVE | Cannot login |

**Total Users**: 6 (5 active, 1 inactive) 