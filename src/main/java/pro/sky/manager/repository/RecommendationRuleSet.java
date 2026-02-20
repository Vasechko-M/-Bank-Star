package pro.sky.manager.repository;

import pro.sky.manager.model.DynamicRule;
import pro.sky.manager.model.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    void onRuleFired(DynamicRule rule);

    Optional<RecommendationDTO> check(UUID userId);
}
