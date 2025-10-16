package com.venhancer.vmerge.repository;

import com.venhancer.vmerge.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * @param username Username to search for
     * @return Optional user
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     * @param email Email to search for
     * @return Optional user
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username or email
     * @param username Username to search for
     * @param email Email to search for
     * @return Optional user
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return true if exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if username exists excluding given user ID
     * @param username Username to check
     * @param userId User ID to exclude
     * @return true if exists
     */
    boolean existsByUsernameAndIdNot(String username, Long userId);
    
    /**
     * Check if email exists excluding given user ID
     * @param email Email to check
     * @param userId User ID to exclude
     * @return true if exists
     */
    boolean existsByEmailAndIdNot(String email, Long userId);
    
    /**
     * Find users by status
     * @param status User status
     * @param pageable Pagination info
     * @return Page of users
     */
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    
    /**
     * Find users by role
     * @param role User role
     * @param pageable Pagination info
     * @return Page of users
     */
    Page<User> findByRole(User.UserRole role, Pageable pageable);
    
    /**
     * Find users by status and role
     * @param status User status
     * @param role User role
     * @param pageable Pagination info
     * @return Page of users
     */
    Page<User> findByStatusAndRole(User.UserStatus status, User.UserRole role, Pageable pageable);
    
    /**
     * Find users created after given date
     * @param createdAfter Date to search after
     * @param pageable Pagination info
     * @return Page of users
     */
    Page<User> findByCreatedAtAfter(LocalDateTime createdAfter, Pageable pageable);
    
    /**
     * Find users created between dates
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination info
     * @return Page of users
     */
    Page<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Search users by name (first name or last name) containing keyword
     * @param keyword Keyword to search
     * @param pageable Pagination info
     * @return Page of users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Search users by multiple criteria
     * @param keyword Keyword to search in name, username, or email
     * @param status User status (optional)
     * @param role User role (optional)
     * @param pageable Pagination info
     * @return Page of users
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:role IS NULL OR u.role = :role)")
    Page<User> searchUsers(@Param("keyword") String keyword,
                          @Param("status") User.UserStatus status,
                          @Param("role") User.UserRole role,
                          Pageable pageable);
    
    /**
     * Get user count by status
     * @param status User status
     * @return User count
     */
    long countByStatus(User.UserStatus status);
    
    /**
     * Get user count by role
     * @param role User role
     * @return User count
     */
    long countByRole(User.UserRole role);
    
    /**
     * Get active users count
     * @return Active users count
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();
    
    /**
     * Get recently created users (last 30 days)
     * @param pageable Pagination info
     * @return Page of users
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :thirtyDaysAgo ORDER BY u.createdAt DESC")
    Page<User> findRecentlyCreatedUsers(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo, Pageable pageable);
    
    /**
     * Bulk update user status
     * @param userIds List of user IDs
     * @param status New status
     * @return Number of updated records
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")
    int updateUserStatus(@Param("userIds") List<Long> userIds, @Param("status") User.UserStatus status);
    
    /**
     * Soft delete users by updating status to DELETED
     * @param userIds List of user IDs to delete
     * @return Number of deleted records
     */
    @Modifying
    @Query("UPDATE User u SET u.status = 'DELETED' WHERE u.id IN :userIds")
    int softDeleteUsers(@Param("userIds") List<Long> userIds);
    
    /**
     * Find users with pagination and sorting options
     * @param pageable Pagination and sorting info
     * @return Page of users
     */
    @Query("SELECT u FROM User u WHERE u.status != 'DELETED'")
    Page<User> findAllActiveUsers(Pageable pageable);
} 