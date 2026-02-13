package pro.sky.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import pro.sky.manager.cache.QueryKey;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TransactionSumCache {

    private final Cache<QueryKey, Double> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public Double getResult(QueryKey key) {
        return cache.getIfPresent(key);
    }

    public void putResult(QueryKey key, Double value) {
        cache.put(key, value);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    // ✅ НОВЫЙ МЕТОД с паттерном get-if-absent-compute
    public Double get(QueryKey key, Function<QueryKey, Double> mappingFunction) {
        return cache.get(key, mappingFunction);
    }
}