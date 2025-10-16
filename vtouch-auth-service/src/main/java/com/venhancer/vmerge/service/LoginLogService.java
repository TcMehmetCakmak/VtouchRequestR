package com.venhancer.vmerge.service;

import com.venhancer.vmerge.entity.LoginLog;
import com.venhancer.vmerge.repository.LoginLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginLogService.class);
    
    private final LoginLogRepository loginLogRepository;
    
    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }
    
    /**
     * Başarılı login'i loglar
     */
    public void logSuccessfulLogin(String username, String ipAddress, String userAgent) {
        try {
            LoginLog loginLog = new LoginLog(username, ipAddress, userAgent, true);
            loginLogRepository.save(loginLog);
            logger.info("Successful login logged for user: {}", username);
        } catch (Exception e) {
            logger.error("Error logging successful login for user: {}", username, e);
        }
    }
    
    /**
     * Başarısız login'i loglar
     */
    public void logFailedLogin(String username, String ipAddress, String userAgent, String failureReason) {
        try {
            LoginLog loginLog = new LoginLog(username, ipAddress, userAgent, false);
            loginLog.setFailureReason(failureReason);
            loginLogRepository.save(loginLog);
            logger.warn("Failed login logged for user: {} - Reason: {}", username, failureReason);
        } catch (Exception e) {
            logger.error("Error logging failed login for user: {}", username, e);
        }
    }
    
    /**
     * Kullanıcının login geçmişini getirir
     */
    public List<LoginLog> getUserLoginHistory(String username) {
        return loginLogRepository.findByUsernameOrderByLoginTimeDesc(username);
    }
    
    /**
     * Son 24 saatteki başarısız login sayısını getirir
     */
    public Long getFailedLoginCountLast24Hours(String username) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return loginLogRepository.countFailedLoginsByUsernameSince(username, since);
    }
    
    /**
     * Tüm login loglarını getirir
     */
    public List<LoginLog> getAllLoginLogs() {
        return loginLogRepository.findAll();
    }
    
    /**
     * Başarılı login loglarını getirir
     */
    public List<LoginLog> getSuccessfulLoginLogs() {
        return loginLogRepository.findBySuccessTrueOrderByLoginTimeDesc();
    }
    
    /**
     * Başarısız login loglarını getirir
     */
    public List<LoginLog> getFailedLoginLogs() {
        return loginLogRepository.findBySuccessFalseOrderByLoginTimeDesc();
    }
}
