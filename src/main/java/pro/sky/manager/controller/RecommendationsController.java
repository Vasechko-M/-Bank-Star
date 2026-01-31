package pro.sky.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.servise.RecommendationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class RecommendationsController {

    private final RecommendationService recommendationService;

    public RecommendationsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendation/{user_id}")
    public ResponseEntity<Map<String, Object>> getRecommendations(@PathVariable("user_id") String userIdStr) {
        UUID userId;
        // Обработка UUID
        try {
            String cleanedUserId = userIdStr.trim().replaceAll("\\s", "");
            if (cleanedUserId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User ID cannot be empty"));
            }
            userId = UUID.fromString(cleanedUserId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Invalid UUID format",
                            "received", userIdStr.trim(),
                            "expected", "Valid UUID (e.g., 123e4567-e89b-12d3-a456-426614174000)"
                    ));
        }

        boolean rulesApplicable;
        try {
            rulesApplicable = recommendationService.checkConditionsForSetRules(userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error checking conditions",
                    "details", e.getMessage()
            ));
        }

        if (!rulesApplicable) {
            return ResponseEntity.ok(Map.of(
                    "user_id", userId.toString(),
                    "recommendations", null,
                    "message", "Условия не выполнены"
            ));
        }

        List<RecommendationDTO> recommendations;
        try {
            recommendations = recommendationService.getRecommendationsByUserId(userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error retrieving recommendations",
                    "details", e.getMessage()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "user_id", userId.toString(),
                "recommendations", recommendations,
                "count", recommendations.size()
        ));
    }
}