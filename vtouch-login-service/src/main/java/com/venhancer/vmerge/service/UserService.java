package com.venhancer.vmerge.service;

import com.venhancer.vmerge.dto.UserDTO;
import com.venhancer.vmerge.dto.UserStatistics;
import com.venhancer.vmerge.dto.request.CreateUserRequest;
import com.venhancer.vmerge.dto.request.RegisterRequest;
import com.venhancer.vmerge.dto.request.UpdateUserRequest;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.PagedResponse;
import com.venhancer.vmerge.entity.User;
import com.venhancer.vmerge.exception.BusinessException;
import com.venhancer.vmerge.exception.InvalidCredentialsException;
import com.venhancer.vmerge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Create a new user
     * @param request Create user request
     * @return API response with created user
     */
    public ApiResponse<UserDTO> createUser(CreateUserRequest request) {
        logger.info("Creating new user with username: {}", request.username());
        
        // Validate unique constraints
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException.DuplicateResourceException("User", "username", request.username());
        }
        
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException.DuplicateResourceException("User", "email", request.email());
        }
        
        try {
            User user = request.toEntity();
            // Encode password before saving
            user.setPassword(passwordEncoder.encode(request.password()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = UserDTO.fromEntity(savedUser);
            
            String message = messageService.getMessageWithDefault("user.created", "User created successfully");
            logger.info("User created successfully with ID: {}", savedUser.getId());
            
            return ApiResponse.success(message, userDTO);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new BusinessException("USER_CREATION_FAILED", "Failed to create user: " + e.getMessage());
        }
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return API response with user data
     */
    @Transactional(readOnly = true)
    public ApiResponse<UserDTO> getUserById(Long userId) {
        logger.debug("Getting user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException.ResourceNotFoundException("User", String.valueOf(userId)));
        
        UserDTO userDTO = UserDTO.fromEntity(user);
        return ApiResponse.success(userDTO);
    }
    
    /**
     * Get user by username
     * @param username Username
     * @return API response with user data
     */
    @Transactional(readOnly = true)
    public ApiResponse<UserDTO> getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException.ResourceNotFoundException("User", username));
        
        UserDTO userDTO = UserDTO.fromEntity(user);
        return ApiResponse.success(userDTO);
    }
    
    /**
     * Update user
     * @param userId User ID to update
     * @param request Update user request
     * @return API response with updated user
     */
    public ApiResponse<UserDTO> updateUser(Long userId, UpdateUserRequest request) {
        logger.info("Updating user with ID: {}", userId);
        
        if (!request.hasUpdates()) {
            throw new BusinessException.ValidationException("request", "No updates provided");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException.ResourceNotFoundException("User", String.valueOf(userId)));
        
        // Validate unique constraints for updated fields
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(request.username(), userId)) {
                throw new BusinessException.DuplicateResourceException("User", "username", request.username());
            }
        }
        
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.email(), userId)) {
                throw new BusinessException.DuplicateResourceException("User", "email", request.email());
            }
        }
        
        try {
            User updatedUser = request.applyTo(user);
            User savedUser = userRepository.save(updatedUser);
            UserDTO userDTO = UserDTO.fromEntity(savedUser);
            
            String message = messageService.getMessageWithDefault("user.updated", "User updated successfully");
            logger.info("User updated successfully with ID: {}", savedUser.getId());
            
            return ApiResponse.success(message, userDTO);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            throw new BusinessException("USER_UPDATE_FAILED", "Failed to update user: " + e.getMessage());
        }
    }
    
    /**
     * Delete user (soft delete)
     * @param userId User ID to delete
     * @return API response
     */
    public ApiResponse<Void> deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException.ResourceNotFoundException("User", String.valueOf(userId)));
        
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new BusinessException.InvalidOperationException("delete user", "User is already deleted");
        }
        
        try {
            user.setStatus(User.UserStatus.DELETED);
            userRepository.save(user);
            
            String message = messageService.getMessageWithDefault("user.deleted", "User deleted successfully");
            logger.info("User deleted successfully with ID: {}", userId);
            
            return ApiResponse.success(message);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            throw new BusinessException("USER_DELETE_FAILED", "Failed to delete user: " + e.getMessage());
        }
    }
    
    /**
     * Get all users with pagination
     * @param page Page number (0-based)
     * @param size Page size
     * @param sort Sort field
     * @param direction Sort direction
     * @return API response with paged users
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<UserDTO>> getAllUsers(int page, int size, String sort, String direction) {
        logger.debug("Getting all users - page: {}, size: {}, sort: {}, direction: {}", page, size, sort, direction);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<User> userPage = userRepository.findAllActiveUsers(pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(UserDTO::fromEntity)
                .toList();
        
        PagedResponse<UserDTO> pagedResponse = new PagedResponse<>(
            userDTOs,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages()
        );
        
        return ApiResponse.success(pagedResponse);
    }
    
    /**
     * Search users
     * @param keyword Search keyword
     * @param status User status filter
     * @param role User role filter
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @param direction Sort direction
     * @return API response with search results
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<UserDTO>> searchUsers(String keyword, User.UserStatus status, User.UserRole role,
                                                          int page, int size, String sort, String direction) {
        logger.debug("Searching users - keyword: {}, status: {}, role: {}", keyword, status, role);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<User> userPage = userRepository.searchUsers(keyword, status, role, pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(UserDTO::fromEntity)
                .toList();
        
        PagedResponse<UserDTO> pagedResponse = new PagedResponse<>(
            userDTOs,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages()
        );
        
        return ApiResponse.success(pagedResponse);
    }
    
    /**
     * Get user statistics
     * @return API response with user statistics
     */
    @Transactional(readOnly = true)
    public ApiResponse<UserStatistics> getUserStatistics() {
        logger.debug("Getting user statistics");
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(User.UserStatus.ACTIVE);
        long inactiveUsers = userRepository.countByStatus(User.UserStatus.INACTIVE);
        long suspendedUsers = userRepository.countByStatus(User.UserStatus.SUSPENDED);
        long deletedUsers = userRepository.countByStatus(User.UserStatus.DELETED);
        
        long adminUsers = userRepository.countByRole(User.UserRole.ADMIN);
        long moderatorUsers = userRepository.countByRole(User.UserRole.MODERATOR);
        long regularUsers = userRepository.countByRole(User.UserRole.USER);
        
        UserStatistics statistics = new UserStatistics(
            totalUsers, activeUsers, inactiveUsers, suspendedUsers, deletedUsers,
            adminUsers, moderatorUsers, regularUsers
        );
        
        return ApiResponse.success(statistics);
    }
    
    /**
     * Get recently created users
     * @param page Page number
     * @param size Page size
     * @return API response with recently created users
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<UserDTO>> getRecentlyCreatedUsers(int page, int size) {
        logger.debug("Getting recently created users - page: {}, size: {}", page, size);
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<User> userPage = userRepository.findRecentlyCreatedUsers(thirtyDaysAgo, pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(UserDTO::fromEntity)
                .toList();
        
        PagedResponse<UserDTO> pagedResponse = new PagedResponse<>(
            userDTOs,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages()
        );
        
        return ApiResponse.success(pagedResponse);
    }
    
    /**
     * Change user status
     * @param userId User ID
     * @param newStatus New status
     * @return API response
     */
    public ApiResponse<UserDTO> changeUserStatus(Long userId, User.UserStatus newStatus) {
        logger.info("Changing user status - userId: {}, newStatus: {}", userId, newStatus);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException.ResourceNotFoundException("User", String.valueOf(userId)));
        
        if (user.getStatus() == newStatus) {
            throw new BusinessException.InvalidOperationException("change user status", 
                "User is already in " + newStatus + " status");
        }
        
        try {
            user.setStatus(newStatus);
            User savedUser = userRepository.save(user);
            UserDTO userDTO = UserDTO.fromEntity(savedUser);
            
            String message = messageService.getMessageWithDefault("user.status.changed", "User status changed successfully");
            logger.info("User status changed successfully - userId: {}, newStatus: {}", userId, newStatus);
            
            return ApiResponse.success(message, userDTO);
        } catch (Exception e) {
            logger.error("Error changing user status: {}", e.getMessage(), e);
            throw new BusinessException("USER_STATUS_CHANGE_FAILED", "Failed to change user status: " + e.getMessage());
        }
    }
    
    /**
     * Change user role
     * @param userId User ID
     * @param newRole New role
     * @return API response with updated user
     */
    public ApiResponse<UserDTO> changeUserRole(Long userId, User.UserRole newRole) {//
        logger.info("Changing user role - userId: {}, newRole: {}", userId, newRole);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException.ResourceNotFoundException("User", String.valueOf(userId)));
        
        if (user.getRole() == newRole) {
            throw new BusinessException.InvalidOperationException("change user role", 
                "User already has " + newRole + " role");
        }
        
        try {
            user.setRole(newRole);
            User savedUser = userRepository.save(user);
            UserDTO userDTO = UserDTO.fromEntity(savedUser);
            
            String message = messageService.getMessageWithDefault("user.role.changed", "User role changed successfully");
            logger.info("User role changed successfully - userId: {}, newRole: {}", userId, newRole);
            
            return ApiResponse.success(message, userDTO);
        } catch (Exception e) {
            logger.error("Error changing user role: {}", e.getMessage(), e);
            throw new BusinessException("USER_ROLE_CHANGE_FAILED", "Failed to change user role: " + e.getMessage());
        }
    }
    
    // Authentication methods
    
    /**
     * Load user by username for Spring Security
     * @param username Username or email
     * @return UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active: " + username);
        }
        
        return user;
    }
    
    /**
     * Register new user with encoded password
     * @param request Register request
     * @return API response with created user
     */
    public ApiResponse<UserDTO> registerUser(RegisterRequest request) {
        logger.info("Registering new user with username: {}", request.username());
        
        // Validate unique constraints
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException.DuplicateResourceException("User", "username", request.username());
        }
        
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException.DuplicateResourceException("User", "email", request.email());
        }
        
        try {
            User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName()
            );
            
            if (request.phoneNumber() != null) {
                user.setPhoneNumber(request.phoneNumber());
            }
            
            User savedUser = userRepository.save(user);
            UserDTO userDTO = UserDTO.fromEntity(savedUser);
            
            String message = messageService.getMessageWithDefault("user.registered", "User registered successfully");
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            
            return ApiResponse.success(message, userDTO);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            throw new BusinessException("USER_REGISTRATION_FAILED", "Failed to register user: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user credentials
     * @param username Username or email
     * @param password Plain text password
     * @return User if credentials are valid
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public User authenticateUser(String username, String password) {
        logger.debug("Authenticating user: {}", username);
        
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new InvalidCredentialsException());
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("User account is not active");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        logger.info("User authenticated successfully: {}", username);
        return user;
    }
    
    /**
     * Find user by username or email
     * @param identifier Username or email
     * @return User if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier);
    }
    
} 