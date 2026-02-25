package pro.sky.manager.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import pro.sky.manager.model.rules.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class RecommendationServiceImplRuleSetTest {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private Cache cache;
    private RecommendationServiceImplRuleSet service;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        cache = mock(Cache.class);

        service = new RecommendationServiceImplRuleSet(
                mock(javax.sql.DataSource.class),
                mock(org.springframework.cache.CacheManager.class, invocation -> cache)
        );
    }

    @Test
    void check_returnsCachedResult() {
        UUID userId = UUID.randomUUID();
        Optional<RecommendationDTO> cached = Optional.of(new RecommendationDTO(userId, "P", "T"));

        when(cache.get("recommendation_" + userId, Optional.class)).thenReturn(cached);

        Optional<RecommendationDTO> result = service.check(userId);

        assertTrue(result.isPresent());
        assertEquals(cached.get(), result.get());
        verify(cache).get("recommendation_" + userId, Optional.class);
    }

    @Test
    void check_returnsFromDatabaseWhenNotCached() {
        UUID userId = UUID.randomUUID();

        when(cache.get("recommendation_" + userId, Optional.class)).thenReturn(null);
        RecommendationDTO recommendation = new RecommendationDTO(UUID.randomUUID(), "Invest", "Text");

        RecommendationServiceImplRuleSet spyService = spy(service);
        doReturn(Optional.of(recommendation)).when(spyService).checkRecommendations(userId);

        Optional<RecommendationDTO> result = spyService.check(userId);

        assertTrue(result.isPresent());
        assertEquals(recommendation, result.get());
        verify(cache).put("recommendation_" + userId, Optional.of(recommendation));
    }
}