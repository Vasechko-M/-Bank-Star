package pro.sky.manager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.manager.model.rules.RecommendationDTO;
import pro.sky.manager.service.RecommendationService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationsController.class)
public class RecommendationsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RecommendationService recommendationService;

    @Test
    void shouldReturnBadRequest_whenUserIdIsEmpty() throws Exception {
        mockMvc.perform(get("/recommendation/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User ID cannot be empty"));
    }

    @Test
    void shouldReturnBadRequest_whenUserIdIsInvalidFormat() throws Exception {
        mockMvc.perform(get("/recommendation/invalid-uuid-string"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid UUID format"))
                .andExpect(jsonPath("$.received").value("invalid-uuid-string"));
    }

    @Test
    void shouldReturnRecommendations_whenUserIdIsValid() throws Exception {
        UUID userId = UUID.randomUUID();

        when(recommendationService.checkConditionsForSetRules(userId)).thenReturn(true);
        when(recommendationService.getRecommendationsByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recommendation/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    void shouldReturnRecommendations_whenServiceReturnsData() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationDTO recommendation1 = new RecommendationDTO(UUID.randomUUID(), "Rec 1", "Text 1");
        RecommendationDTO recommendation2 = new RecommendationDTO(UUID.randomUUID(), "Rec 2", "Text 2");
        List<RecommendationDTO> recommendations = List.of(recommendation1, recommendation2);

        when(recommendationService.checkConditionsForSetRules(userId)).thenReturn(true);
        when(recommendationService.getRecommendationsByUserId(userId)).thenReturn(recommendations);

        mockMvc.perform(get("/recommendation/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations[0].name").value("Rec 1"))
                .andExpect(jsonPath("$.recommendations[1].name").value("Rec 2"));
    }

    @Test
    void shouldReturnNoRecommendations_whenRulesNotApplicable() throws Exception {
        UUID userId = UUID.randomUUID();
        when(recommendationService.checkConditionsForSetRules(userId)).thenReturn(false);

        mockMvc.perform(get("/recommendation/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations").value("No"))
                .andExpect(jsonPath("$.message").value("Условия не выполнены"));
    }

    @Test
    void shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        UUID userId = UUID.randomUUID();
        when(recommendationService.checkConditionsForSetRules(any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/recommendation/" + userId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error checking conditions"))
                .andExpect(jsonPath("$.details").value("Database error"));
    }

    @Test
    void shouldReturnInternalServerError_whenGetRecommendationsThrowsException() throws Exception {
        UUID userId = UUID.randomUUID();
        when(recommendationService.checkConditionsForSetRules(userId)).thenReturn(true);
        when(recommendationService.getRecommendationsByUserId(userId)).thenThrow(new RuntimeException("Database error on get"));

        mockMvc.perform(get("/recommendation/" + userId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error retrieving recommendations"))
                .andExpect(jsonPath("$.details").value("Database error on get"));
    }

}
