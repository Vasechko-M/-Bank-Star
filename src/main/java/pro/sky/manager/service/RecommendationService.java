package pro.sky.manager.service;

//import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import pro.sky.manager.model.rules.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;
    private final CacheManager cacheManager;




    public RecommendationService(List<RecommendationRuleSet> ruleSets, CacheManager cacheManager) {
        this.ruleSets = ruleSets;
        this.cacheManager = cacheManager;
    }


    /**
     * Получает рекомендации для пользователя с кешированием
     */
    @Cacheable(value = "userRecommendations", key = "#userId")
    public List<RecommendationDTO> getRecommendationsByUserId(UUID userId) {
        return ruleSets.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    /**
     * Проверяет, есть ли рекомендации для пользователя (без кеширования)
     */
    public boolean checkConditionsForSetRules(UUID userId) {
        return ruleSets.stream()
                .anyMatch(rule -> rule.check(userId).isPresent());
    }
    /**
     * Метод для сброса кеша
     */
    public void clearUserRecommendationsCache() {
        Cache cache = cacheManager.getCache("userRecommendations");
        if (cache != null) {
            cache.clear();
        }
    }


}