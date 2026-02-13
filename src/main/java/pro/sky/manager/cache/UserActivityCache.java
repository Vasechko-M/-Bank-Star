package pro.sky.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class UserActivityCache {

    private final Cache<CacheKey, Boolean> booleanCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<CacheKey, List<String>> productTypesCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    // ✅ НОВЫЕ МЕТОДЫ с паттерном get-if-absent-compute
    public Boolean getBoolean(CacheKey key, Function<CacheKey, Boolean> mappingFunction) {
        return booleanCache.get(key, mappingFunction);
    }

    public List<String> getProductTypes(CacheKey key, Function<CacheKey, List<String>> mappingFunction) {
        return productTypesCache.get(key, mappingFunction);
    }

    // Старые методы
    public Boolean getBooleanResult(CacheKey key) {
        return booleanCache.getIfPresent(key);
    }

    public void putBooleanResult(CacheKey key, Boolean value) {
        booleanCache.put(key, value);
    }

    public List<String> getProductTypesFromCache(CacheKey key) {
        return productTypesCache.getIfPresent(key);
    }

    public void putProductTypesInCache(CacheKey key, List<String> result) {
        productTypesCache.put(key, result);
    }

    public void invalidateAll() {
        booleanCache.invalidateAll();
        productTypesCache.invalidateAll();
    }
}