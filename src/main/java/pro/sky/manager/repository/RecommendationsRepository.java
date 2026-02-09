package pro.sky.manager.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pro.sky.manager.cache.DepositWithdrawCache;
import pro.sky.manager.cache.TransactionSumCache;
import pro.sky.manager.cache.UserActivityCache;
import pro.sky.manager.model.RecommendationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate rulesJdbcTemplate;

    private final DepositWithdrawCache depositCache;
    private final TransactionSumCache spentCache;
    private final UserActivityCache userActivityCache;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Сохраняет рекомендацию в базу данных.
     */
    @Transactional
    public void save(RecommendationDTO recommendation) {
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("Recommendation or ID is null");
        }
        entityManager.persist(recommendation);
    }

    /**
     * Удаляет рекомендацию по UUID из базы данных.
     */
    @Transactional
    public void deleteById(UUID id) {
        RecommendationDTO recommendation = entityManager.find(RecommendationDTO.class, id);
        if (recommendation != null) {
            entityManager.remove(recommendation);
        }
    }

    /**
     * Находит рекомендацию по UUID.
     */
    public RecommendationDTO findById(UUID id) {
        return entityManager.find(RecommendationDTO.class, id);
    }

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate,
            @Qualifier("rulesJdbcTemplate") JdbcTemplate rulesJdbcTemplate,
            DepositWithdrawCache depositCache,
            TransactionSumCache spentCache,
            UserActivityCache userActivityCache) {
        this.jdbcTemplate = jdbcTemplate;
        this.rulesJdbcTemplate = rulesJdbcTemplate;
        this.depositCache = depositCache;
        this.spentCache = spentCache;
        this.userActivityCache = userActivityCache;
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
 * методы из задания:
 * Количество запросов в вашей системе фиксированное,
 * и при правильной декомпозиции у вас должно получиться не больше трех методов репозитория и,
 * соответственно, не больше трех кешей.
 */
    /**
     * Получает сумму депозитов с кешированием
     */
    public double getTotalDepositsCached(UUID userId, String productType) {
        String key = depositCache.generateKey(userId.toString(), productType, "DEPOSIT", "sum");
        Double cachedResult = depositCache.getResult(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        double result = getTotalDeposits(userId, productType);
        depositCache.putResult(key, result);
        return result;
    }

    /**
     * Получает сумму расходов с кешированием
     */
    public double getTotalSpentCached(UUID userId, String productType) {
        String key = spentCache.generateKey(userId.toString(), productType, "WITHDRAWAL", "sum");
        Double cachedResult = spentCache.getResult(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        double result = getTotalSpent(userId, productType);
        spentCache.putResult(key, result);
        return result;
    }


    /**
     * Получает список типов продуктов пользователя с кешем
     */
    public List<String> getProductTypesCached(UUID userId) {
        String key = userActivityCache.generateKey("PRODUCT_TYPES", userId.toString(), "");

        List<String> cachedResult = userActivityCache.getProductTypesFromCache(key);

        if (cachedResult != null) {
            return cachedResult;
        }

        List<String> result = getProductTypes(userId);

        userActivityCache.putProductTypesInCache(key, result);
        return result;
    }

}