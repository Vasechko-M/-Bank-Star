package pro.sky.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.manager.model.RecommendationDto;
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
//
//    @GetMapping("/recommendation/{user_id}")
//    @Operation(summary = "Получить рекомендации по пользователю",
//            description = "Возвращает список рекомендаций для указанного пользователя")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200",
//                    description = "Успешный ответ",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "400",
//                    description = "Неверный формат UUID",
//                    content = @Content(mediaType = "application/json"))
//    })
//    public ResponseEntity<Map<String, Object>> getRecommendations(
//            @PathVariable("user_id") String userIdStr) {
//
//        UUID userId;
//        try {
//            String cleanedUserId = userIdStr.trim().replaceAll("\\s", "");
//            if (cleanedUserId.isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "User ID cannot be empty"));
//            }
//
//            userId = UUID.fromString(cleanedUserId);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of(
//                            "error", "Invalid UUID format",
//                            "received", userIdStr.trim(),
//                            "expected", "Valid UUID (e.g., 123e4567-e89b-12d3-a456-426614174000)"
//                    ));
//        }
//
//        boolean rulesApplicable = recommendationService.checkConditionsForSetRules(userId);
//
//        if (!rulesApplicable) {
//            return ResponseEntity.ok(Map.of(
//                    "user_id", userId.toString(),
//                    "recommendations", null,
//                    "message", "Условия не выполнены"
//            ));
//        }
//
//        List<RecommendationDto> recommendations = recommendationService.getRecommendationsByUserId(userId);
//
//        return ResponseEntity.ok(Map.of(
//                "user_id", userId.toString(),
//                "recommendations", recommendations,
//                "count", recommendations.size()
//        ));
//    }

    @GetMapping("/recommendation/{user_id}")
    public ResponseEntity<Map<String, Object>> getRecommendations(@PathVariable("user_id") String userIdStr) {

        UUID userId;
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
            // Логируем или смотрим стек
            return ResponseEntity.status(500).body(Map.of("error", "Error checking conditions", "details", e.getMessage()));
        }

        if (!rulesApplicable) {
            return ResponseEntity.ok(Map.of(
                    "user_id", userId.toString(),
                    "recommendations", null,
                    "message", "Условия не выполнены"
            ));
        }

        List<RecommendationDto> recommendations;
        try {
            recommendations = recommendationService.getRecommendationsByUserId(userId);
        } catch (Exception e) {
            // Логируем ошибку
            return ResponseEntity.status(500).body(Map.of("error", "Error retrieving recommendations", "details", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of(
                "user_id", userId.toString(),
                "recommendations", recommendations,
                "count", recommendations.size()
        ));
    }
}