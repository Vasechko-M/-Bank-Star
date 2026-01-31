package pro.sky.manager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;

import java.util.*;

@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    private List<RecommendationRuleSet> ruleSets;

    @Autowired
    private final JdbcTemplate jdbcTemplate;


    public RecommendationService(List<RecommendationRuleSet> ruleSets, JdbcTemplate jdbcTemplate) {
        this.ruleSets = ruleSets;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Возвращает список реккомендаций по идентификатору пользователя
     */
    public List<RecommendationDTO> getRecommendationsByUserId (UUID userId) {

        List<Map<String, Object>> transactions = jdbcTemplate.queryForList(
                "SELECT * FROM TRANSACTIONS WHERE USER_ID = ?", userId.toString()
        );

        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (Map<String, Object> transaction : transactions) {
            for (RecommendationRuleSet ruleSet : ruleSets) {
                Optional<RecommendationDTO> recommendation = ruleSet.check(userId);
                recommendation.ifPresent(recommendations::add);
            }
        }
        return recommendations;
    }

    /**
     * Проверяет, удовлетворяет ли переданный пользователь набору правил
     */
    public boolean checkConditionsForSetRules(UUID userId) {
        return ruleSets.stream()
                .anyMatch(rule -> rule.check(userId).isPresent());
    }
}