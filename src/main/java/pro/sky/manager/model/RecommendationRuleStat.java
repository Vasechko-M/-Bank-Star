package pro.sky.manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "recommendation_rule_stats")
public class RecommendationRuleStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private UUID ruleId;

    @Column(nullable = false)
    private Integer count = 0;

    public RecommendationRuleStat() {}

    public RecommendationRuleStat(UUID ruleId) {
        this.ruleId = ruleId;
    }
}