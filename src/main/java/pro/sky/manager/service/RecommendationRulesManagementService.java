package pro.sky.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.manager.model.rules.RecommendationDTO;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRulesManagementService {

    private final RecommendationsRepository ruleRepository;
    private final RecommendationService recommendationService;

    public void addRule(RecommendationDTO rule) {
        ruleRepository.save(rule);
        recommendationService.clearUserRecommendationsCache();
    }

    public void removeRule(UUID ruleId) {
        ruleRepository.deleteById(ruleId);
        recommendationService.clearUserRecommendationsCache();
    }
}