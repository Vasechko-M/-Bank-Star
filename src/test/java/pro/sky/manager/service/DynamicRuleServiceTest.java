package pro.sky.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.sky.manager.dto.DepositWithdrawSum;
import pro.sky.manager.model.rules.DynamicRule;
import pro.sky.manager.model.rules.QueryCondition;
import pro.sky.manager.model.rules.QueryType;
import pro.sky.manager.model.rules.RecommendationDTO;
import pro.sky.manager.repository.DynamicRuleRepository;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DynamicRuleServiceTest {

    private DynamicRuleRepository dynamicRuleRepository;
    private RecommendationsRepository recommendationsRepository;
    private DynamicRuleService service;

    @BeforeEach
    void setUp() {
        dynamicRuleRepository = mock(DynamicRuleRepository.class);
        recommendationsRepository = mock(RecommendationsRepository.class);
        service = new DynamicRuleService(dynamicRuleRepository, recommendationsRepository);
    }

    @Test
    void getRecommendations_userOfConditionTrue() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.USER_OF);
        condition.setArguments(List.of("LOAN"));
        condition.setNegate(false);

        DynamicRule rule = new DynamicRule();
        rule.setProductId(productId);
        rule.setProductName("Test Product");
        rule.setProductText("Text");
        rule.setRule(List.of(condition));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationsRepository.getProductTypes(userId)).thenReturn(List.of("LOAN"));

        List<RecommendationDTO> result = service.getRecommendationsFromDynamicRules(userId);

        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProductId());
    }

    @Test
    void getRecommendations_userOfConditionNegateTrue() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.USER_OF);
        condition.setArguments(List.of("LOAN"));
        condition.setNegate(true); // negate true

        DynamicRule rule = new DynamicRule();
        rule.setProductId(productId);
        rule.setProductName("Test Product");
        rule.setProductText("Text");
        rule.setRule(List.of(condition));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationsRepository.getProductTypes(userId)).thenReturn(List.of("LOAN"));

        // user has LOAN -> negate -> false
        List<RecommendationDTO> result = service.getRecommendationsFromDynamicRules(userId);

        assertEquals(0, result.size());
    }

    @Test
    void getRecommendations_activeUserOfTrue() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.ACTIVE_USER_OF);
        condition.setArguments(List.of("LOAN"));
        condition.setNegate(false);

        DynamicRule rule = new DynamicRule();
        rule.setProductId(productId);
        rule.setProductName("Active Product");
        rule.setProductText("Text");
        rule.setRule(List.of(condition));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationsRepository.getTransactionCount(userId, "LOAN")).thenReturn(5);

        List<RecommendationDTO> result = service.getRecommendationsFromDynamicRules(userId);

        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProductId());
    }

    @Test
    void getRecommendations_transactionSumCompare() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.TRANSACTION_SUM_COMPARE);
        condition.setArguments(List.of("LOAN", "DEPOSIT", ">", "100"));
        condition.setNegate(false);

        DynamicRule rule = new DynamicRule();
        rule.setProductId(productId);
        rule.setProductName("SumCompare Product");
        rule.setProductText("Text");
        rule.setRule(List.of(condition));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationsRepository.getTransactionSum(userId, "LOAN", "DEPOSIT")).thenReturn(150.0);

        List<RecommendationDTO> result = service.getRecommendationsFromDynamicRules(userId);

        assertEquals(1, result.size());
    }

    @Test
    void getRecommendations_depositWithdrawCompare() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW);
        condition.setArguments(List.of("LOAN", ">"));
        condition.setNegate(false);

        DynamicRule rule = new DynamicRule();
        rule.setProductId(productId);
        rule.setProductName("DepositWithdraw Product");
        rule.setProductText("Text");
        rule.setRule(List.of(condition));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationsRepository.getDepositWithdrawSums(userId, "LOAN"))
                .thenReturn(new DepositWithdrawSum(200.0, 100.0));

        List<RecommendationDTO> result = service.getRecommendationsFromDynamicRules(userId);

        assertEquals(1, result.size());
    }
}