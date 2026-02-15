package pro.sky.manager.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;

/**
 * Реализация с кешированием результатов проверки рекомендаций.
 */
@Service
public class RecommendationServiceImplRuleSet implements RecommendationRuleSet {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final Cache cache;

    public RecommendationServiceImplRuleSet(
            @Qualifier("recommendationsDataSource") DataSource dataSource,
            CacheManager cacheManager
    ) {
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.cache = cacheManager.getCache("recommendationCache");
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        String cacheKey = "recommendation_" + userId.toString();

        @SuppressWarnings("unchecked")
        Optional<RecommendationDTO> cachedResult = (Optional<RecommendationDTO>) cache.get(cacheKey, Optional.class);
        if (cachedResult != null) {
            return cachedResult;
        }

        Optional<RecommendationDTO> result = checkRecommendations(userId);

        cache.put(cacheKey, result);

        return result;
    }

    /**
     * Основной метод проверки рекомендаций, работающий без кеша.
     */
    public Optional<RecommendationDTO> checkRecommendations(UUID userId) {
        String[] productTypes = {"DEBIT", "CREDIT", "SAVING", "INVEST"};
        String transactionType = "DEPOSIT"; //под вопросом

        for (String productType : productTypes) {
            String sqlQuery = """
                    SELECT t.USER_ID, p.TYPE AS product_type, t.TYPE AS transaction_type, SUM(t.AMOUNT) AS total_sum
                    FROM TRANSACTIONS t
                    INNER JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                    WHERE t.USER_ID = :userId AND p.TYPE = :productType AND t.TYPE = :transactionType
                    GROUP BY t.USER_ID, p.TYPE, t.TYPE;
                    """;

            RowMapper<UserTransactionSummary> rowMapper = (rs, rowNum) ->
                    new UserTransactionSummary(
                            rs.getString("USER_ID"),
                            rs.getString("product_type"),
                            rs.getString("transaction_type"),
                            rs.getLong("total_sum")
                    );

            var results = namedJdbcTemplate.query(sqlQuery, Map.of(
                    "userId", userId.toString(),
                    "productType", productType,
                    "transactionType", transactionType
            ), rowMapper);

            Optional<RecommendationDTO> recommendation = analyzeResults(results, userId, productType);
            if (recommendation.isPresent()) {
                return recommendation;
            }
        }
        return Optional.empty();
    }

    private Optional<RecommendationDTO> analyzeResults(java.util.List<UserTransactionSummary> results, UUID userId, String productType) {
        boolean hasDebit = false;
        boolean hasInvest = false;
        boolean hasCredit = false;
        long sumSaving = 0L;
        long sumDebitDeposit = 0L;
        long sumSavingDeposit = 0L;
        long sumDeposit = 0L;
        long sumWithdrawal = 0L;
        long debitDepositSum = 0L;
        long debitWithdrawalSum = 0L;

        for (var summary : results) {
            switch (summary.productType()) {
                case "DEBIT":
                    hasDebit = true;
                    break;
                case "INVESTMENT":
                    hasInvest = true;
                    break;
                case "CREDIT":
                    hasCredit = true;
                    break;
            }

            switch (summary.transactionType()) {
                case "SAVING":
                    sumSaving += summary.totalSum();
                    break;
                case "DEPOSIT_DEBIT":
                    sumDebitDeposit += summary.totalSum();
                    debitDepositSum += summary.totalSum();
                    break;
                case "DEPOSIT_SAVING":
                    sumSavingDeposit += summary.totalSum();
                    break;
                case "WITHDRAWAL":
                    sumWithdrawal += summary.totalSum();
                    if ("DEBIT".equals(summary.productType())) {
                        debitWithdrawalSum += summary.totalSum();
                    }
                    break;
                default:
                    sumDeposit += summary.totalSum();
            }
        }

        if ((hasDebit && !hasInvest && sumSaving > 1000)) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
            ));
        } else if ((hasDebit &&
                (debitDepositSum > debitWithdrawalSum) &&
                (sumDebitDeposit >= 50000 || sumSavingDeposit >= 50000))) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                    "Top Saving",
                    "Текст  для Top Saving скопировать надо..."
            ));
        } else if (!hasCredit && (sumDeposit > sumWithdrawal) && (sumWithdrawal > 100000)) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                    "Простой кредит",
                    "Текст для кредита скопировать не забыть..."
            ));
        }
        return Optional.empty();
    }

    // Вспомогательная сущность для промежуточных вычислений
    record UserTransactionSummary(String userId, String productType, String transactionType, long totalSum) {
    }
}
