package pro.sky.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import pro.sky.manager.dto.DepositWithdrawSum;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class DepositWithdrawCache {

    private final Cache<CacheKey, DepositWithdrawSum> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public DepositWithdrawSum getResult(CacheKey key) {
        return cache.getIfPresent(key);
    }

    public void putResult(CacheKey key, DepositWithdrawSum value) {
        cache.put(key, value);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }


    public DepositWithdrawSum get(CacheKey key, Function<CacheKey, DepositWithdrawSum> mappingFunction) {
        return cache.get(key, mappingFunction);
    }
}