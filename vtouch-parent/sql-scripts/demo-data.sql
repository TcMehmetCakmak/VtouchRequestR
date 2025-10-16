-- Demo data for Template Java Application
-- This script creates demo users for testing login functionality
-- Passwords are BCrypt encoded with strength 12

-- Note: These passwords are BCrypt encoded versions of simple passwords
-- admin123 -> $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeO7fuL9sGX6N6q7e
-- user123  -> $2a$12$wjjOKNqQN2ZXCqOlPPvEKOqNZmqvxFhGXEPZCVOCYL7WjdJLhVKKq
-- mod123   -> $2a$12$8SbwEqOJhNQfKEcNxTtMJOQXD6zQgKRbQjjNnKrN9OZfKnSgVLXMu

-- Insert demo users
INSERT INTO users (id, username, email, first_name, last_name, password, role, active, created_at, updated_at) 
VALUES 
  -- Admin user
  (1, 'admin', 'admin@example.com', 'Admin', 'User', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeO7fuL9sGX6N6q7e', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  
  -- Regular user
  (2, 'user', 'user@example.com', 'Regular', 'User', '$2a$12$wjjOKNqQN2ZXCqOlPPvEKOqNZmqvxFhGXEPZCVOCYL7WjdJLhVKKq', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  
  -- Moderator user
  (3, 'moderator', 'moderator@example.com', 'Moderator', 'User', '$2a$12$8SbwEqOJhNQfKEcNxTtMJOQXD6zQgKRbQjjNnKrN9OZfKnSgVLXMu', 'MODERATOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  
  -- Additional test users
  (4, 'john.doe', 'john.doe@example.com', 'John', 'Doe', '$2a$12$wjjOKNqQN2ZXCqOlPPvEKOqNZmqvxFhGXEPZCVOCYL7WjdJLhVKKq', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  
  (5, 'jane.smith', 'jane.smith@example.com', 'Jane', 'Smith', '$2a$12$8SbwEqOJhNQfKEcNxTtMJOQXD6zQgKRbQjjNnKrN9OZfKnSgVLXMu', 'MODERATOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  
  -- Inactive user for testing
  (6, 'inactive', 'inactive@example.com', 'Inactive', 'User', '$2a$12$wjjOKNqQN2ZXCqOlPPvEKOqNZmqvxFhGXEPZCVOCYL7WjdJLhVKKq', 'USER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Reset sequence to continue from the highest ID
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Verify data insertion
SELECT 
  id, username, email, first_name, last_name, role, active, created_at 
FROM users 
ORDER BY id; 