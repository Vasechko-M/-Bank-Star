package pro.sky.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.model.DynamicRule;
import pro.sky.manager.model.QueryCondition;
import pro.sky.manager.model.RecommendationRuleStat;
import pro.sky.manager.repository.DynamicRuleRepository;
import pro.sky.manager.repository.RecommendationRuleStatsRepository;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;
    private final RecommendationsRepository recommendationsRepository;

    @Autowired
    private RecommendationRuleStatsRepository statsRepo;

    /**
     * Увеличение счётчика срабатываний правила
     */
    public void incrementCountForRule(UUID ruleId) {
        RecommendationRuleStat stat = statsRepo.findByRuleId(ruleId);

        if (stat != null) {
            int newCount = stat.getCount() + 1;
            stat.setCount(newCount);
            statsRepo.save(stat);
        } else {
            // Если статистика ещё не создана, создаём её с начальным счётом 1
            RecommendationRuleStat newStat = new RecommendationRuleStat(ruleId);
            newStat.setCount(1);
            statsRepo.save(newStat);
        }
    }

    public List<RecommendationDTO> getRecommendationsFromDynamicRules(UUID userId) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        List<DynamicRule> rules = dynamicRuleRepository.findAll();

        for (DynamicRule rule : rules) {
            if (evaluateRule(userId, rule)) {
                recommendations.add(new RecommendationDTO(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }

        return recommendations;
    }

    private boolean evaluateRule(UUID userId, DynamicRule rule) {
        for (QueryCondition condition : rule.getRule()) {
            if (!evaluateCondition(userId, condition)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(UUID userId, QueryCondition condition) {
        boolean result = switch (condition.getQuery()) {
            case USER_OF -> checkUserOf(userId, condition.getArguments());
            case ACTIVE_USER_OF -> checkActiveUserOf(userId, condition.getArguments());
            case TRANSACTION_SUM_COMPARE -> checkTransactionSumCompare(userId, condition.getArguments());
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> checkDepositWithdrawCompare(userId, condition.getArguments());
        };

        return condition.isNegate() ? !result : result;
    }

    private boolean checkUserOf(UUID userId, List<String> arguments) {
        if (arguments.size() < 1) {
            throw new IllegalArgumentException("USER_OF requires product type argument");
        }
        String productType = arguments.get(0);

        List<String> userProductTypes = recommendationsRepository.getProductTypes(userId);
        return userProductTypes.contains(productType);
    }

    private boolean checkActiveUserOf(UUID userId, List<String> arguments) {
        if (arguments.size() < 1) {
            throw new IllegalArgumentException("ACTIVE_USER_OF requires product type argument");
        }
        String productType = arguments.get(0);

        int transactionCount = recommendationsRepository.getTransactionCount(userId, productType);
        return transactionCount >= 5;
    }

    private boolean checkTransactionSumCompare(UUID userId, List<String> arguments) {
        if (arguments.size() < 4) {
            throw new IllegalArgumentException("TRANSACTION_SUM_COMPARE requires 4 arguments");
        }

        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        int constant;

        try {
            constant = Integer.parseInt(arguments.get(3));
            if (constant < 0) {
                throw new IllegalArgumentException("Constant must be non-negative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Constant must be an integer");
        }

        double sum = recommendationsRepository.getTransactionSum(userId, productType, transactionType);

        return switch (operator) {
            case ">" -> sum > constant;
            case "<" -> sum < constant;
            case "=" -> Math.abs(sum - constant) < 0.001;
            case ">=" -> sum >= constant;
            case "<=" -> sum <= constant;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private boolean checkDepositWithdrawCompare(UUID userId, List<String> arguments) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW requires 2 arguments");
        }

        String productType = arguments.get(0);
        String operator = arguments.get(1);

        var sums = recommendationsRepository.getDepositWithdrawSums(userId, productType);
        double depositSum = sums.getDepositSum();
        double withdrawSum = sums.getWithdrawSum();

        return switch (operator) {
            case ">" -> depositSum > withdrawSum;
            case "<" -> depositSum < withdrawSum;
            case "=" -> Math.abs(depositSum - withdrawSum) < 0.001;
            case ">=" -> depositSum >= withdrawSum;
            case "<=" -> depositSum <= withdrawSum;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}