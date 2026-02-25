package pro.sky.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RecommendationRulesManagementServiceTest {

    private RecommendationsRepository ruleRepository;
    private RecommendationService recommendationService;
    private RecommendationRulesManagementService service;

    @BeforeEach
    void setUp() {
        ruleRepository = mock(RecommendationsRepository.class);
        recommendationService = mock(RecommendationService.class);
        service = new RecommendationRulesManagementService(ruleRepository, recommendationService);
    }

    @Test
    void addRule_success() {
        RecommendationDTO rule = new RecommendationDTO(UUID.randomUUID(), "Product", "Text");

        service.addRule(rule);

        // проверяем, что сохранение прошло
        verify(ruleRepository, times(1)).save(rule);
        // проверяем, что кеш очистился
        verify(recommendationService, times(1)).clearUserRecommendationsCache();
    }

    @Test
    void removeRule_success() {
        UUID ruleId = UUID.randomUUID();

        service.removeRule(ruleId);

        // проверяем, что удаление прошло
        verify(ruleRepository, times(1)).deleteById(ruleId);
        // проверяем, что кеш очистился
        verify(recommendationService, times(1)).clearUserRecommendationsCache();
    }
}