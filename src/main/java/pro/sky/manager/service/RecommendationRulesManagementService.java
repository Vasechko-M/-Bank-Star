package pro.sky.manager.service;

import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.UUID;

@Service
public class RecommendationRulesManagementService {

    private final RecommendationsRepository ruleRepository;
    private final RecommendationService recommendationService;

    public RecommendationRulesManagementService(RecommendationsRepository ruleRepository,
                                                RecommendationService recommendationService) {
        this.ruleRepository = ruleRepository;
        this.recommendationService = recommendationService;
    }

    public void addRule(RecommendationDTO rule) {
        ruleRepository.save(rule);
        recommendationService.clearUserRecommendationsCache();
    }

    public void removeRule(UUID ruleId) {
        ruleRepository.deleteById(ruleId);
        recommendationService.clearUserRecommendationsCache();
    }

    public void updateRule(RecommendationDTO rule) {
        ruleRepository.save(rule);
        recommendationService.clearUserRecommendationsCache();
    }
}
