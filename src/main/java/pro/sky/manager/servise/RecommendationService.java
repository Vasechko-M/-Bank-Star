package pro.sky.manager.servise;

import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDto;
import pro.sky.manager.repository.RecommendationRuleSet;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<RecommendationDto> getRecommendationsByUserId(UUID userId) {

        return ruleSets.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean checkConditionsForSetRules(UUID userId) {
        return ruleSets.stream()
                .anyMatch(rule -> rule.check(userId).isPresent());
    }
}