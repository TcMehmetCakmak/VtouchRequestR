package com.venhancer.vmerge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service for location-related caching operations using Redis
 * Activated only when Redis is enabled
 */
@Service
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = false)
public class LocationCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Cache keys constants
    private static final String LOCATION_CACHE_KEY = "location:";
    private static final String USER_LOCATION_KEY = "user:location:";
    private static final String NEARBY_LOCATIONS_KEY = "nearby:";
    private static final String LOCATION_STATS_KEY = "location:stats:";

    @Autowired
    public LocationCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Cache location data with TTL
     * @param locationId Location identifier
     * @param locationData Location data to cache
     * @param ttlMinutes Time to live in minutes
     */
    public void cacheLocation(String locationId, Object locationData, long ttlMinutes) {
        String key = LOCATION_CACHE_KEY + locationId;
        redisTemplate.opsForValue().set(key, locationData, ttlMinutes, TimeUnit.MINUTES);
    }

    /**
     * Get cached location data
     * @param locationId Location identifier
     * @return Cached location data or null if not found
     */
    @Cacheable(value = "locations", key = "#locationId")
    public Object getCachedLocation(String locationId) {
        String key = LOCATION_CACHE_KEY + locationId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Cache user's current location
     * @param userId User identifier
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param ttlMinutes Time to live in minutes
     */
    public void cacheUserLocation(String userId, double latitude, double longitude, long ttlMinutes) {
        String key = USER_LOCATION_KEY + userId;
        Map<String, Double> location = Map.of(
            "latitude", latitude,
            "longitude", longitude,
            "timestamp", (double) System.currentTimeMillis()
        );
        redisTemplate.opsForHash().putAll(key, location);
        redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
    }

    /**
     * Get user's cached location
     * @param userId User identifier
     * @return User's location data or null if not found
     */
    @Cacheable(value = "userLocations", key = "#userId")
    public Map<Object, Object> getUserLocation(String userId) {
        String key = USER_LOCATION_KEY + userId;
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Cache nearby locations for a specific area
     * @param areaKey Area identifier (e.g., "lat_lng_radius")
     * @param nearbyLocations List of nearby locations
     * @param ttlMinutes Time to live in minutes
     */
    public void cacheNearbyLocations(String areaKey, List<Object> nearbyLocations, long ttlMinutes) {
        String key = NEARBY_LOCATIONS_KEY + areaKey;
        redisTemplate.opsForList().rightPushAll(key, nearbyLocations.toArray());
        redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
    }

    /**
     * Get cached nearby locations
     * @param areaKey Area identifier
     * @return List of nearby locations or empty list if not found
     */
    @Cacheable(value = "nearbyLocations", key = "#areaKey")
    public List<Object> getNearbyLocations(String areaKey) {
        String key = NEARBY_LOCATIONS_KEY + areaKey;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * Cache location statistics
     * @param locationId Location identifier
     * @param stats Statistics data
     * @param ttlMinutes Time to live in minutes
     */
    public void cacheLocationStats(String locationId, Map<String, Object> stats, long ttlMinutes) {
        String key = LOCATION_STATS_KEY + locationId;
        redisTemplate.opsForHash().putAll(key, stats);
        redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
    }

    /**
     * Get cached location statistics
     * @param locationId Location identifier
     * @return Location statistics or empty map if not found
     */
    @Cacheable(value = "locationStats", key = "#locationId")
    public Map<Object, Object> getLocationStats(String locationId) {
        String key = LOCATION_STATS_KEY + locationId;
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Increment location visit counter
     * @param locationId Location identifier
     * @return New visit count
     */
    public Long incrementLocationVisits(String locationId) {
        String key = LOCATION_STATS_KEY + locationId;
        return redisTemplate.opsForHash().increment(key, "visits", 1);
    }

    /**
     * Add location to user's favorites using Redis Set
     * @param userId User identifier
     * @param locationId Location identifier
     */
    public void addToFavorites(String userId, String locationId) {
        String key = "user:favorites:" + userId;
        redisTemplate.opsForSet().add(key, locationId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS); // 30 days TTL
    }

    /**
     * Remove location from user's favorites
     * @param userId User identifier
     * @param locationId Location identifier
     */
    public void removeFromFavorites(String userId, String locationId) {
        String key = "user:favorites:" + userId;
        redisTemplate.opsForSet().remove(key, locationId);
    }

    /**
     * Get user's favorite locations
     * @param userId User identifier
     * @return Set of favorite location IDs
     */
    @Cacheable(value = "userFavorites", key = "#userId")
    public Set<Object> getUserFavorites(String userId) {
        String key = "user:favorites:" + userId;
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * Check if location is in user's favorites
     * @param userId User identifier
     * @param locationId Location identifier
     * @return true if location is favorite, false otherwise
     */
    public boolean isLocationFavorite(String userId, String locationId) {
        String key = "user:favorites:" + userId;
        return redisTemplate.opsForSet().isMember(key, locationId);
    }

    /**
     * Clear all location caches
     */
    @CacheEvict(value = {"locations", "userLocations", "nearbyLocations", "locationStats", "userFavorites"}, allEntries = true)
    public void clearAllLocationCaches() {
        // This method will clear all location-related caches
    }

    /**
     * Clear specific location cache
     * @param locationId Location identifier
     */
    @CacheEvict(value = "locations", key = "#locationId")
    public void clearLocationCache(String locationId) {
        String key = LOCATION_CACHE_KEY + locationId;
        redisTemplate.delete(key);
    }

    /**
     * Clear user's location cache
     * @param userId User identifier
     */
    @CacheEvict(value = {"userLocations", "userFavorites"}, key = "#userId")
    public void clearUserLocationCache(String userId) {
        String userLocationKey = USER_LOCATION_KEY + userId;
        String userFavoritesKey = "user:favorites:" + userId;
        redisTemplate.delete(userLocationKey);
        redisTemplate.delete(userFavoritesKey);
    }

    /**
     * Get Redis connection status
     * @return true if Redis is connected, false otherwise
     */
    public boolean isRedisConnected() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 