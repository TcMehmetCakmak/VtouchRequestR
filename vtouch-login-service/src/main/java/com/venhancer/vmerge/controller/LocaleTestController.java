package com.venhancer.vmerge.controller;

import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for locale and message testing
 * Only available in development profiles
 */
@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test endpoint'leri (sadece geliştirme ortamında)")
public class LocaleTestController {

    @Autowired
    private MessageService messageService;

    /**
     * Test Turkish messages
     * @return ApiResponse with Turkish messages
     */
    @GetMapping("/messages")
    @Operation(summary = "Test Turkish messages", description = "Türkçe mesajları test eder")
    public ResponseEntity<ApiResponse<Map<String, String>>> testMessages() {
        Map<String, String> messages = new HashMap<>();
        
        // Test common messages
        messages.put("operation.success", messageService.getMessage("operation.success"));
        messages.put("operation.created", messageService.getMessage("operation.created"));
        messages.put("operation.updated", messageService.getMessage("operation.updated"));
        messages.put("operation.deleted", messageService.getMessage("operation.deleted"));
        
        // Test error messages
        messages.put("RESOURCE_NOT_FOUND", messageService.getMessage("RESOURCE_NOT_FOUND"));
        messages.put("VALIDATION_ERROR", messageService.getMessage("VALIDATION_ERROR"));
        messages.put("INVALID_OPERATION", messageService.getMessage("INVALID_OPERATION"));
        
        // Test user messages
        messages.put("user.created", messageService.getMessage("user.created"));
        messages.put("user.updated", messageService.getMessage("user.updated"));
        messages.put("user.not.found", messageService.getMessage("user.not.found"));
        
        // Test authentication messages
        messages.put("auth.login.success", messageService.getMessage("auth.login.success"));
        messages.put("auth.invalid.credentials", messageService.getMessage("auth.invalid.credentials"));
        messages.put("auth.access.denied", messageService.getMessage("auth.access.denied"));
        
        // Test validation messages
        messages.put("NotNull", messageService.getMessage("NotNull"));
        messages.put("NotEmpty", messageService.getMessage("NotEmpty"));
        messages.put("Email", messageService.getMessage("Email"));
        
        return ResponseEntity.ok(
            ApiResponse.success("Türkçe mesajlar başarıyla yüklendi", messages)
        );
    }

    /**
     * Test message with parameters
     * @param name Name parameter for message
     * @return ApiResponse with parameterized message
     */
    @GetMapping("/message-with-params")
    @Operation(summary = "Test parameterized message", description = "Parametreli mesajı test eder")
    public ResponseEntity<ApiResponse<String>> testMessageWithParams(
            @Parameter(description = "İsim parametresi", example = "Ahmet")
            @RequestParam(defaultValue = "Kullanıcı") String name) {
        
        String message = messageService.getMessageWithDefault(
            "welcome.message", 
            "Hoş geldiniz, " + name + "!"
        );
        
        return ResponseEntity.ok(
            ApiResponse.success("Parametreli mesaj test edildi", message)
        );
    }

    /**
     * Test current locale information
     * @return ApiResponse with locale information
     */
    @GetMapping("/locale-info")
    @Operation(summary = "Get locale information", description = "Mevcut dil ayarı bilgilerini getirir")
    public ResponseEntity<ApiResponse<Map<String, String>>> getLocaleInfo() {
        Map<String, String> localeInfo = new HashMap<>();
        
        localeInfo.put("default_locale", "tr_TR");
        localeInfo.put("supported_locales", "tr_TR, en_US, es_ES");
        localeInfo.put("change_locale_param", "lang");
        localeInfo.put("example_url", "/api/test/messages?lang=en");
        localeInfo.put("turkish_test", messageService.getMessage("operation.success"));
        
        return ResponseEntity.ok(
            ApiResponse.success("Dil ayarı bilgileri", localeInfo)
        );
    }
} 