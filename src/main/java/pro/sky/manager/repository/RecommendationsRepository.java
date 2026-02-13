package pro.sky.manager.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pro.sky.manager.dto.DepositWithdrawSum;

import java.util.List;
import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
     * Нужен для TRANSACTION_SUM_COMPARE запросов.
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
     * Нужен для ACTIVE_USER_OF запросов.
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
     * Нужен для TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW запросов.
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
}