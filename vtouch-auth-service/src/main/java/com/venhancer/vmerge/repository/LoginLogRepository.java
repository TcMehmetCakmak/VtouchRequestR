package com.venhancer.vmerge.repository;

import com.venhancer.vmerge.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    
    /**
     * Kullanıcıya göre login loglarını getirir
     */
    List<LoginLog> findByUsernameOrderByLoginTimeDesc(String username);
    
    /**
     * Başarılı login loglarını getirir
     */
    List<LoginLog> findBySuccessTrueOrderByLoginTimeDesc();
    
    /**
     * Başarısız login loglarını getirir
     */
    List<LoginLog> findBySuccessFalseOrderByLoginTimeDesc();
    
    /**
     * Belirli bir tarih aralığındaki login loglarını getirir
     */
    @Query("SELECT l FROM LoginLog l WHERE l.loginTime BETWEEN :startDate AND :endDate ORDER BY l.loginTime DESC")
    List<LoginLog> findByLoginTimeBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * IP adresine göre login loglarını getirir
     */
    List<LoginLog> findByIpAddressOrderByLoginTimeDesc(String ipAddress);
    
    /**
     * Kullanıcının son başarılı login'ini getirir
     */
    @Query("SELECT l FROM LoginLog l WHERE l.username = :username AND l.success = true ORDER BY l.loginTime DESC")
    List<LoginLog> findLastSuccessfulLoginByUsername(@Param("username") String username);
    
    /**
     * Belirli bir kullanıcının başarısız login sayısını getirir
     */
    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.username = :username AND l.success = false AND l.loginTime > :since")
    Long countFailedLoginsByUsernameSince(@Param("username") String username, @Param("since") LocalDateTime since);
}
