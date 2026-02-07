package pro.sky.manager.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;



@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate rulesJdbcTemplate;

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate,
            @Qualifier("rulesJdbcTemplate") JdbcTemplate rulesJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rulesJdbcTemplate = rulesJdbcTemplate;
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
 * вот тут методы из задания:
 * Количество запросов в вашей системе фиксированное,
 * и при правильной декомпозиции у вас должно получиться не больше трех методов репозитория и,
 * соответственно, не больше трех кешей.
 */

}