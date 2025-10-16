package com.venhancer.vmerge.controller;

import com.venhancer.vmerge.service.LocationCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Location controller for Redis caching operations
 * Activated only when Redis is enabled
 */
@RestController
@RequestMapping("/locations")
@Tag(name = "Lokasyon", description = "Lokasyon önbellekleme işlemleri")
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = false)
public class LocationController {

    private final LocationCacheService locationCacheService;

    @Autowired
    public LocationController(LocationCacheService locationCacheService) {
        this.locationCacheService = locationCacheService;
    }

    /**
     * Cache user location
     */
    @PostMapping("/user/{userId}/location")
    @Operation(summary = "Kullanıcı konumunu önbelleğe al", description = "Kullanıcının mevcut konumunu Redis'e kaydeder")
    public ResponseEntity<Map<String, Object>> cacheUserLocation(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId,
            @Parameter(description = "Enlem") @RequestParam double latitude,
            @Parameter(description = "Boylam") @RequestParam double longitude,
            @Parameter(description = "Dakika cinsinden TTL") @RequestParam(defaultValue = "60") long ttlMinutes) {
        
        locationCacheService.cacheUserLocation(userId, latitude, longitude, ttlMinutes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Kullanıcı konumu başarıyla önbelleğe alındı");
        response.put("userId", userId);
        response.put("latitude", latitude);
        response.put("longitude", longitude);
        response.put("ttlMinutes", ttlMinutes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user location from cache
     */
    @GetMapping("/user/{userId}/location")
    @Operation(summary = "Kullanıcı konumunu getir", description = "Önbelleğe alınmış kullanıcı konumunu getirir")
    public ResponseEntity<Map<String, Object>> getUserLocation(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId) {
        
        Map<Object, Object> location = locationCacheService.getUserLocation(userId);
        
        Map<String, Object> response = new HashMap<>();
        if (location.isEmpty()) {
            response.put("message", "Kullanıcı konumu bulunamadı");
            response.put("userId", userId);
            return ResponseEntity.notFound().build();
        }
        
        response.put("message", "Kullanıcı konumu başarıyla getirildi");
        response.put("userId", userId);
        response.put("location", location);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Add location to user favorites
     */
    @PostMapping("/user/{userId}/favorites/{locationId}")
    @Operation(summary = "Favori lokasyon ekle", description = "Kullanıcının favori lokasyonlarına yeni lokasyon ekler")
    public ResponseEntity<Map<String, Object>> addToFavorites(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId,
            @Parameter(description = "Lokasyon ID") @PathVariable String locationId) {
        
        locationCacheService.addToFavorites(userId, locationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lokasyon favorilere eklendi");
        response.put("userId", userId);
        response.put("locationId", locationId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Remove location from user favorites
     */
    @DeleteMapping("/user/{userId}/favorites/{locationId}")
    @Operation(summary = "Favori lokasyon çıkar", description = "Kullanıcının favori lokasyonlarından lokasyon çıkarır")
    public ResponseEntity<Map<String, Object>> removeFromFavorites(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId,
            @Parameter(description = "Lokasyon ID") @PathVariable String locationId) {
        
        locationCacheService.removeFromFavorites(userId, locationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lokasyon favorilerden çıkarıldı");
        response.put("userId", userId);
        response.put("locationId", locationId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user favorites
     */
    @GetMapping("/user/{userId}/favorites")
    @Operation(summary = "Favori lokasyonları getir", description = "Kullanıcının tüm favori lokasyonlarını getirir")
    public ResponseEntity<Map<String, Object>> getUserFavorites(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId) {
        
        Set<Object> favorites = locationCacheService.getUserFavorites(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Favori lokasyonlar başarıyla getirildi");
        response.put("userId", userId);
        response.put("favorites", favorites);
        response.put("count", favorites.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if location is favorite
     */
    @GetMapping("/user/{userId}/favorites/{locationId}/check")
    @Operation(summary = "Favori kontrolü", description = "Lokasyonun kullanıcının favorilerinde olup olmadığını kontrol eder")
    public ResponseEntity<Map<String, Object>> isLocationFavorite(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId,
            @Parameter(description = "Lokasyon ID") @PathVariable String locationId) {
        
        boolean isFavorite = locationCacheService.isLocationFavorite(userId, locationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", isFavorite ? "Lokasyon favorilerde" : "Lokasyon favorilerde değil");
        response.put("userId", userId);
        response.put("locationId", locationId);
        response.put("isFavorite", isFavorite);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Increment location visits
     */
    @PostMapping("/{locationId}/visit")
    @Operation(summary = "Lokasyon ziyaret sayısını artır", description = "Lokasyonun ziyaret sayısını bir artırır")
    public ResponseEntity<Map<String, Object>> incrementLocationVisits(
            @Parameter(description = "Lokasyon ID") @PathVariable String locationId) {
        
        Long visits = locationCacheService.incrementLocationVisits(locationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lokasyon ziyaret sayısı güncellendi");
        response.put("locationId", locationId);
        response.put("visits", visits);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get location statistics
     */
    @GetMapping("/{locationId}/stats")
    @Operation(summary = "Lokasyon istatistikleri", description = "Lokasyon istatistiklerini getirir")
    public ResponseEntity<Map<String, Object>> getLocationStats(
            @Parameter(description = "Lokasyon ID") @PathVariable String locationId) {
        
        Map<Object, Object> stats = locationCacheService.getLocationStats(locationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lokasyon istatistikleri getirildi");
        response.put("locationId", locationId);
        response.put("stats", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear user location cache
     */
    @DeleteMapping("/user/{userId}/cache")
    @Operation(summary = "Kullanıcı önbelleğini temizle", description = "Kullanıcının lokasyon önbelleğini temizler")
    public ResponseEntity<Map<String, Object>> clearUserLocationCache(
            @Parameter(description = "Kullanıcı ID") @PathVariable String userId) {
        
        locationCacheService.clearUserLocationCache(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Kullanıcı lokasyon önbelleği temizlendi");
        response.put("userId", userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check Redis connection status
     */
    @GetMapping("/redis/status")
    @Operation(summary = "Redis bağlantı durumu", description = "Redis bağlantısının durumunu kontrol eder")
    public ResponseEntity<Map<String, Object>> getRedisStatus() {
        
        boolean isConnected = locationCacheService.isRedisConnected();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", isConnected ? "Redis bağlantısı aktif" : "Redis bağlantısı yok");
        response.put("isConnected", isConnected);
        
        return ResponseEntity.ok(response);
    }
} 