package pro.sky.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Component

public class UserActivityCache {
    // Кеш для boolean-значений, активности пользователя(использую его)
    private final Cache<String, Boolean> booleanCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    // Кеш для списков типов продуктов(а его не использую, но пока оставлю)
    private final Cache<String, List<String>> productTypesCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    // Получение boolean-значения по ключу
    public Boolean getBooleanResult(String key) {
        return booleanCache.getIfPresent(key);
    }

    // Запись boolean-значения по ключу
    public void putBooleanResult(String key, Boolean value) {
        booleanCache.put(key, value);
    }

    // Получение списка типов продуктов из кеша
    public List<String> getProductTypesFromCache(String key) {
        return productTypesCache.getIfPresent(key);
    }

    // Запись списка типов продуктов в кеш
    public void putProductTypesInCache(String key, List<String> result) {
        productTypesCache.put(key, result);
    }

    // Генерировать уникальный ключ по параметрам
    public String generateKey(String queryType, String userId, String productType) {
        return queryType + ":" + userId + ":" + productType;
    }
}
