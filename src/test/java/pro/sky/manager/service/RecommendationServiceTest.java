package pro.sky.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    RecommendationRuleSet ruleSet1;

    @Mock
    RecommendationRuleSet ruleSet2;

    RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(List.of(ruleSet1, ruleSet2));
    }

    @Test
    void shouldReturnRecommendationsFromAllRuleSets() {
        UUID userId = UUID.randomUUID();
        RecommendationDTO rec1 = new RecommendationDTO(UUID.randomUUID(), "Rec 1", "Text 1");
        RecommendationDTO rec2 = new RecommendationDTO(UUID.randomUUID(), "Rec 2", "Text 2");

        when(ruleSet1.check(userId)).thenReturn(Optional.of(rec1));
        when(ruleSet2.check(userId)).thenReturn(Optional.of(rec2));

        List<RecommendationDTO> result = recommendationService.getRecommendationsByUserId(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(rec1));
        assertTrue(result.contains(rec2));
    }

    @Test
    void shouldReturnEmptyList_whenNoRuleSetsReturnRecommendation() {
        UUID userId = UUID.randomUUID();

        when(ruleSet1.check(userId)).thenReturn(Optional.empty());
        when(ruleSet2.check(userId)).thenReturn(Optional.empty());

        List<RecommendationDTO> result = recommendationService.getRecommendationsByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTrue_whenAtLeastOneRuleSetReturnsRecommendation() {
        UUID userId = UUID.randomUUID();
        RecommendationDTO rec1 = new RecommendationDTO(UUID.randomUUID(), "Rec 1", "Text 1");

        when(ruleSet1.check(userId)).thenReturn(Optional.of(rec1));

        
        boolean result = recommendationService.checkConditionsForSetRules(userId);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenNoRuleSetReturnsRecommendation() {
        UUID userId = UUID.randomUUID();

        when(ruleSet1.check(userId)).thenReturn(Optional.empty());
        when(ruleSet2.check(userId)).thenReturn(Optional.empty());

        boolean result = recommendationService.checkConditionsForSetRules(userId);

        assertFalse(result);
    }
}
