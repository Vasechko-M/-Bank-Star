package pro.sky.manager.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.manager.cache.CacheKey;
import pro.sky.manager.cache.QueryKey;
import pro.sky.manager.dto.DepositWithdrawSum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final Cache<UUID, List<String>> userProductTypesCache;
    private final Cache<CacheKey, Boolean> userProductCache;
    private final Cache<QueryKey, Double> transactionSumCache;
    private final Cache<CacheKey, DepositWithdrawSum> depositWithdrawCache;

    /**
     * Очищает все Caffeine-кэши
     */
    public void clearAllCaffeineCaches() {
        userProductTypesCache.invalidateAll();
        userProductCache.invalidateAll();
        transactionSumCache.invalidateAll();
        depositWithdrawCache.invalidateAll();
    }

    /**
     * Возвращает статистику по кэшам (для мониторинга)
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("userProductTypesCache", Map.of(
                "size", userProductTypesCache.estimatedSize(),
                "stats", userProductTypesCache.stats()
        ));

        stats.put("userProductCache", Map.of(
                "size", userProductCache.estimatedSize(),
                "stats", userProductCache.stats()
        ));

        stats.put("transactionSumCache", Map.of(
                "size", transactionSumCache.estimatedSize(),
                "stats", transactionSumCache.stats()
        ));

        stats.put("depositWithdrawCache", Map.of(
                "size", depositWithdrawCache.estimatedSize(),
                "stats", depositWithdrawCache.stats()
        ));

        return stats;
    }
}