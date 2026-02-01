package pro.sky.manager.service;

import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<RecommendationDTO> getRecommendationsByUserId(UUID userId) {

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