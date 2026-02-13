package pro.sky.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.service.RecommendationService;
import pro.sky.manager.service.DynamicRuleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RecommendationsController {

    private final RecommendationService recommendationService;
    private final DynamicRuleService dynamicRuleService;

    @GetMapping("/recommendation/{user_id}")
    public ResponseEntity<Map<String, Object>> getRecommendations(@PathVariable("user_id") String userIdStr) {
        UUID userId = parseUserId(userIdStr);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid UUID format"));
        }

        try {
            List<RecommendationDTO> recommendations = new ArrayList<>();

            // Статические правила
            recommendations.addAll(recommendationService.getRecommendationsByUserId(userId));

            // Динамические правила
            recommendations.addAll(dynamicRuleService.getRecommendationsFromDynamicRules(userId));

            return ResponseEntity.ok(Map.of(
                    "user_id", userId.toString(),
                    "recommendations", recommendations,
                    "count", recommendations.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error retrieving recommendations",
                    "details", e.getMessage()
            ));
        }
    }

    private UUID parseUserId(String userIdStr) {
        try {
            String cleanedUserId = userIdStr.trim().replaceAll("\\s", "");
            return cleanedUserId.isEmpty() ? null : UUID.fromString(cleanedUserId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}