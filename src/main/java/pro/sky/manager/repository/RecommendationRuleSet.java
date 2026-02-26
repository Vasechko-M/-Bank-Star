package pro.sky.manager.repository;

import pro.sky.manager.model.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> check(UUID userId);
}
