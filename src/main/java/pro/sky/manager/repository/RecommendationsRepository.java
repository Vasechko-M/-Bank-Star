package pro.sky.manager.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pro.sky.manager.cache.CacheKey;
import pro.sky.manager.cache.DepositWithdrawCache;
import pro.sky.manager.cache.QueryKey;
import pro.sky.manager.cache.TransactionSumCache;
import pro.sky.manager.cache.UserActivityCache;
import pro.sky.manager.dto.DepositWithdrawSum;
import pro.sky.manager.model.rules.RecommendationDTO;

import java.util.List;
import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DepositWithdrawCache depositCache;
    private final TransactionSumCache transactionSumCache;
    private final UserActivityCache userActivityCache;

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate,
            DepositWithdrawCache depositCache,
            TransactionSumCache transactionSumCache,
            UserActivityCache userActivityCache) {
        this.jdbcTemplate = jdbcTemplate;
        this.depositCache = depositCache;
        this.transactionSumCache = transactionSumCache;
        this.userActivityCache = userActivityCache;
    }

    // ========== ОСНОВНЫЕ МЕТОДЫ БЕЗ КЭША ==========

    /**
     * Получает случайную сумму транзакции пользователя (один пример).
     */
    public int getRandomTransactionAmount(UUID user) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                user);
        return result != null ? result : 0;
    }

    /**
     * Получает сумму пополнений по продуктам указанного типа пользователя.
     */
    public double getTotalDeposits(UUID userId, String productType) {
        String sql = "SELECT COALESCE(SUM(t.amount), 0) "
                + "FROM transactions t "
                + "JOIN products p ON t.product_id = p.id "
                + "WHERE t.user_id = ? AND p.type = ? AND t.type = 'DEPOSIT'";
        return jdbcTemplate.queryForObject(sql, Double.class, userId, productType);
    }

    /**
     * Получает сумму расходов по продуктам указанного типа пользователя.
     */
    public double getTotalSpent(UUID userId, String productType) {
        String sql = "SELECT COALESCE(SUM(t.amount), 0) "
                + "FROM transactions t "
                + "JOIN products p ON t.product_id = p.id "
                + "WHERE t.user_id = ? AND p.type = ? AND t.type = 'WITHDRAWAL'";
        return jdbcTemplate.queryForObject(sql, Double.class, userId, productType);
    }

    /**
     * Получает список уникальных типов продуктов, который есть у пользователя.
     */
    public List<String> getProductTypes(UUID userId) {
        String sql = "SELECT DISTINCT p.type FROM products p "
                + "JOIN transactions t ON t.product_id = p.id "
                + "WHERE t.user_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    /**
     * Получает сумму транзакций по типу продукта и типу транзакции.
     */
    public double getTransactionSum(UUID userId, String productType, String transactionType) {
        String sql = "SELECT COALESCE(SUM(t.amount), 0) "
                + "FROM transactions t "
                + "JOIN products p ON t.product_id = p.id "
                + "WHERE t.user_id = ? AND p.type = ? AND t.type = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, userId, productType, transactionType);
    }

    /**
     * Получает количество транзакций по типу продукта.
     */
    public int getTransactionCount(UUID userId, String productType) {
        String sql = "SELECT COUNT(*) "
                + "FROM transactions t "
                + "JOIN products p ON t.product_id = p.id "
                + "WHERE t.user_id = ? AND p.type = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        return result != null ? result : 0;
    }

    /**
     * Получает суммы депозитов и снятий для указанного типа продукта.
     */
    public DepositWithdrawSum getDepositWithdrawSums(UUID userId, String productType) {
        String depositSql = "SELECT COALESCE(SUM(t.amount), 0) "
                + "FROM transactions t "
                + "JOIN products p ON t.product_id = p.id "
                + "WHERE t.user_id = ? AND p.type = ? AND t.type = 'DEPOSIT'";

        String withdrawSql = "SELECT COALESCE(SUM(t.amount), 0) "
                + "FROM transactions t "
                + "JOIN products p ON t.product_id = p.id "
                + "WHERE t.user_id = ? AND p.type = ? AND t.type = 'WITHDRAWAL'";

        Double depositSum = jdbcTemplate.queryForObject(depositSql, Double.class, userId, productType);
        Double withdrawSum = jdbcTemplate.queryForObject(withdrawSql, Double.class, userId, productType);

        return new DepositWithdrawSum(
                depositSum != null ? depositSum : 0.0,
                withdrawSum != null ? withdrawSum : 0.0
        );
    }

    // ========== МЕТОДЫ С КЭШИРОВАНИЕМ (ПАТТЕРН GET-IF-ABSENT-COMPUTE) ==========

    /**
     * Получает сумму депозитов с кешированием
     */
    public double getTotalDepositsCached(UUID userId, String productType) {
        CacheKey key = new CacheKey("DEPOSIT", userId, productType);
        return depositCache.get(key, k -> getDepositWithdrawSums(userId, productType)).getDepositSum();
    }

    /**
     * Получает сумму расходов с кешированием
     */
    public double getTotalSpentCached(UUID userId, String productType) {
        CacheKey key = new CacheKey("WITHDRAWAL", userId, productType);
        return depositCache.get(key, k -> getDepositWithdrawSums(userId, productType)).getWithdrawSum();
    }

    /**
     * Получает список типов продуктов пользователя с кешем
     */
    public List<String> getProductTypesCached(UUID userId) {
        CacheKey key = new CacheKey("PRODUCT_TYPES", userId, "");
        return userActivityCache.getProductTypes(key, k -> getProductTypes(userId));
    }

    /**
     * Получает количество транзакций с кешированием (>=5)
     */
    public boolean isActiveUserCached(UUID userId, String productType) {
        CacheKey key = new CacheKey("ACTIVE_USER", userId, productType);
        return userActivityCache.getBoolean(key, k -> getTransactionCount(userId, productType) >= 5);
    }

    /**
     * Получает сумму транзакций с кешированием
     */
    public double getTransactionSumCached(UUID userId, String productType, String transactionType, String operator, int constant) {
        QueryKey key = new QueryKey(userId, productType, transactionType, operator, constant);
        return transactionSumCache.get(key, k -> getTransactionSum(userId, productType, transactionType));
    }

    /**
     * Получает суммы депозитов и снятий с кешированием
     */
    public DepositWithdrawSum getDepositWithdrawSumsCached(UUID userId, String productType) {
        CacheKey key = new CacheKey("DEPOSIT_WITHDRAW", userId, productType);
        return depositCache.get(key, k -> getDepositWithdrawSums(userId, productType));
    }

    // ========== МЕТОДЫ ДЛЯ УПРАВЛЕНИЯ ПРАВИЛАМИ ==========

    /**
     * Сохраняет правило в базу данных dynamic_rules
     */
    public void save(RecommendationDTO rule) {
        String sql = "INSERT INTO dynamic_rules (id, product_id, product_name, product_text) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                rule.getId(),
                rule.getProductId(),
                rule.getProductName(),
                rule.getProductText()
        );
    }

    /**
     * Удаляет правило по ID из базы dynamic_rules
     */
    public void deleteById(UUID ruleId) {
        String sql = "DELETE FROM dynamic_rules WHERE id = ?";
        jdbcTemplate.update(sql, ruleId);
    }

    /**
     * Находит правило по ID
     */
    public RecommendationDTO findById(UUID id) {
        String sql = "SELECT id, product_id, product_name, product_text FROM dynamic_rules WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new RecommendationDTO(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("product_id")),
                        rs.getString("product_name"),
                        rs.getString("product_text")
                ),
                id);
    }

    /**
     * Получает все правила
     */
    public List<RecommendationDTO> findAll() {
        String sql = "SELECT id, product_id, product_name, product_text FROM dynamic_rules";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new RecommendationDTO(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("product_id")),
                        rs.getString("product_name"),
                        rs.getString("product_text")
                ));
    }
}