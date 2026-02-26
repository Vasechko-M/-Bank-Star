package pro.sky.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.manager.cache.UserActivityCache;
import pro.sky.manager.service.CacheService;
import pro.sky.manager.service.RecommendationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
@Tag(name = "Управление сервисом", description = "Эндпоинты для управления и мониторинга сервиса")
public class ManagementController {

    private final CacheManager cacheManager;
    private final UserActivityCache userActivityCache;
    private final RecommendationService recommendationService;
    private final CacheService cacheService;
    private final BuildProperties buildProperties;

    @PostMapping("/clear-caches")
    @Operation(summary = "Очистить все кэши")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {

        long startTime = System.currentTimeMillis();

        // 1. Очищаем стандартные Spring-кэши
        cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(cache -> cache.clear());

        // 2. Очищаем UserActivityCache
        userActivityCache.invalidateAll();

        // 3. Очищаем кэш рекомендаций
        recommendationService.clearUserRecommendationsCache();

        // 4. Очищаем все Caffeine-кэши
        cacheService.clearAllCaffeineCaches();

        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Все кэши успешно очищены");
        response.put("clearedCaches", cacheManager.getCacheNames());
        response.put("durationMs", duration);
        response.put("timestamp", java.time.Instant.now().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "Информация о сервисе")
    public ResponseEntity<Map<String, String>> getServiceInfo() {

        Map<String, String> info = new HashMap<>();
        info.put("name", buildProperties.getArtifact());
        info.put("version", buildProperties.getVersion());
        info.put("group", buildProperties.getGroup());
        info.put("timestamp", buildProperties.getTime().toString());

        return ResponseEntity.ok(info);
    }

    @GetMapping("/cache-stats")
    @Operation(summary = "Статистика по кэшам", description = "Для отладки и мониторинга")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        return ResponseEntity.ok(cacheService.getCacheStats());
    }
}