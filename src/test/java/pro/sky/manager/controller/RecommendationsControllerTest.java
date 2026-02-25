package pro.sky.manager.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pro.sky.manager.model.rules.RecommendationDTO;
import pro.sky.manager.service.DynamicRuleService;
import pro.sky.manager.service.RecommendationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationsControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private DynamicRuleService dynamicRuleService;

    @InjectMocks
    private RecommendationsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Успешный сценарий
    @Test
    void getRecommendations_success() {
        UUID userId = UUID.randomUUID();

        RecommendationDTO dto1 = mock(RecommendationDTO.class);
        RecommendationDTO dto2 = mock(RecommendationDTO.class);

        when(recommendationService.getRecommendationsByUserId(userId))
                .thenReturn(List.of(dto1));

        when(dynamicRuleService.getRecommendationsFromDynamicRules(userId))
                .thenReturn(List.of(dto2));

        ResponseEntity<Map<String, Object>> response =
                controller.getRecommendations(userId.toString());

        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        assertEquals(userId.toString(), body.get("user_id"));
        assertEquals(2, body.get("count"));

        List<?> recommendations = (List<?>) body.get("recommendations");
        assertEquals(2, recommendations.size());

        verify(recommendationService).getRecommendationsByUserId(userId);
        verify(dynamicRuleService).getRecommendationsFromDynamicRules(userId);
    }

    // ❌ Неверный UUID
    @Test
    void getRecommendations_invalidUuid() {
        ResponseEntity<Map<String, Object>> response =
                controller.getRecommendations("invalid-uuid");

        assertEquals(400, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid UUID format", body.get("error"));

        verifyNoInteractions(recommendationService);
        verifyNoInteractions(dynamicRuleService);
    }

    // 💥 Исключение внутри сервиса
    @Test
    void getRecommendations_serviceThrowsException() {
        UUID userId = UUID.randomUUID();

        when(recommendationService.getRecommendationsByUserId(userId))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, Object>> response =
                controller.getRecommendations(userId.toString());

        assertEquals(500, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Error retrieving recommendations", body.get("error"));
        assertEquals("Database error", body.get("details"));
    }
}