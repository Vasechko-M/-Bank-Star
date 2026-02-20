package pro.sky.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.manager.model.RecommendationRuleStat;

import java.util.UUID;

@Repository
public interface RecommendationRuleStatsRepository extends JpaRepository<RecommendationRuleStat, Long> {
    RecommendationRuleStat findByRuleId(UUID ruleId);
}