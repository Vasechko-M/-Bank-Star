package pro.sky.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.manager.model.RecommendationRuleStat;
import pro.sky.manager.repository.RecommendationRuleStatsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class StatsController {

    @Autowired
    private RecommendationRuleStatsRepository statsRepo;

    @GetMapping("/stats")
    public ResponseEntity<List<Map<String, Object>>> getAllRuleStatistics() {
        List<RecommendationRuleStat> allStats = statsRepo.findAll();
        return ResponseEntity.ok().body(allStats.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList()));
    }

    private Map<String, Object> convertToMap(RecommendationRuleStat stat) {
        Map<String, Object> result = new HashMap<>();
        result.put("rule_id", stat.getRuleId());
        result.put("count", stat.getCount());
        return result;
    }}
