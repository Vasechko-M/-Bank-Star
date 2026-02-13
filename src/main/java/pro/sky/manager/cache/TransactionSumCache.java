package pro.sky.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component

public class TransactionSumCache {
    private final Cache<String, Double> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public Double getResult(String key) {
        return cache.getIfPresent(key);
    }

    public void putResult(String key, Double value) {
        cache.put(key, value);
    }

    public String generateKey(String userId, String productType, String operation, String value) {
        return "TRANSACTION_SUM_COMPARE:" + userId + ":" + productType + ":" + operation + ":" + value;
    }
}