package pro.sky.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private RecommendationRuleSet ruleSet;
    private CacheManager cacheManager;
    private Cache cache;
    private RecommendationService service;

    @BeforeEach
    void setUp() {
        ruleSet = mock(RecommendationRuleSet.class);
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);

        when(cacheManager.getCache("userRecommendations")).thenReturn(cache);

        service = new RecommendationService(List.of(ruleSet), cacheManager);
    }

    @Test
    void getRecommendationsByUserId_returnsRecommendations() {
        UUID userId = UUID.randomUUID();
        RecommendationDTO recommendation = new RecommendationDTO(UUID.randomUUID(), "Product", "Text");

        when(ruleSet.check(userId)).thenReturn(Optional.of(recommendation));

        List<RecommendationDTO> result = service.getRecommendationsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(recommendation, result.get(0));
        verify(ruleSet).check(userId);
    }

    @Test
    void checkConditionsForSetRules_returnsTrueIfAnyMatches() {
        UUID userId = UUID.randomUUID();
        RecommendationDTO recommendation = new RecommendationDTO(UUID.randomUUID(), "Product", "Text");

        when(ruleSet.check(userId)).thenReturn(Optional.of(recommendation));

        boolean result = service.checkConditionsForSetRules(userId);

        assertTrue(result);
    }

    @Test
    void checkConditionsForSetRules_returnsFalseIfNoneMatch() {
        UUID userId = UUID.randomUUID();

        when(ruleSet.check(userId)).thenReturn(Optional.empty());

        boolean result = service.checkConditionsForSetRules(userId);

        assertFalse(result);
    }

    @Test
    void clearUserRecommendationsCache_invokesCacheClear() {
        service.clearUserRecommendationsCache();

        verify(cache).clear();
    }

    @Test
    void clearUserRecommendationsCache_cacheIsNull_doesNotThrow() {
        when(cacheManager.getCache("userRecommendations")).thenReturn(null);

        assertDoesNotThrow(() -> service.clearUserRecommendationsCache());
    }
}