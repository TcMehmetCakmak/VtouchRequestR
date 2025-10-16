package com.venhancer.vmerge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {
    
    @Autowired
    private MessageSource messageSource;
    
    /**
     * Get localized message for the given key
     * @param key Message key
     * @return Localized message
     */
    public String getMessage(String key) {
        return getMessageWithDefault(key, null);
    }
    
    /**
     * Get localized message for the given key with default value
     * @param key Message key
     * @param defaultMessage Default message if key not found
     * @return Localized message or default message
     */
    public String getMessageWithDefault(String key, String defaultMessage) {
        return getMessage(key, null, defaultMessage);
    }
    
    /**
     * Get localized message for the given key with parameters
     * @param key Message key
     * @param args Message parameters
     * @return Localized message
     */
    public String getMessage(String key, Object[] args) {
        return getMessage(key, args, null);
    }
    
    /**
     * Get localized message for the given key with parameters and default value
     * @param key Message key
     * @param args Message parameters
     * @param defaultMessage Default message if key not found
     * @return Localized message or default message
     */
    public String getMessage(String key, Object[] args, String defaultMessage) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return defaultMessage != null ? defaultMessage : key;
        }
    }
    
    /**
     * Get localized message for the given key with specific locale
     * @param key Message key
     * @param locale Specific locale
     * @return Localized message
     */
    public String getMessage(String key, Locale locale) {
        return getMessage(key, null, null, locale);
    }
    
    /**
     * Get localized message for the given key with parameters and specific locale
     * @param key Message key
     * @param args Message parameters
     * @param defaultMessage Default message if key not found
     * @param locale Specific locale
     * @return Localized message or default message
     */
    public String getMessage(String key, Object[] args, String defaultMessage, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return defaultMessage != null ? defaultMessage : key;
        }
    }
    
    /**
     * Check if message key exists
     * @param key Message key
     * @return true if key exists, false otherwise
     */
    public boolean hasMessage(String key) {
        return hasMessage(key, LocaleContextHolder.getLocale());
    }
    
    /**
     * Check if message key exists for specific locale
     * @param key Message key
     * @param locale Specific locale
     * @return true if key exists, false otherwise
     */
    public boolean hasMessage(String key, Locale locale) {
        try {
            messageSource.getMessage(key, null, locale);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 