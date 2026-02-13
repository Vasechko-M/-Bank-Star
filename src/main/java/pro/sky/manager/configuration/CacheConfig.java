package pro.sky.manager.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.sky.manager.cache.CacheKey;
import pro.sky.manager.cache.QueryKey;
import pro.sky.manager.dto.DepositWithdrawSum;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean("userProductTypesCache")
    public Cache<UUID, List<String>> userProductTypesCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean("userProductCache")
    public Cache<CacheKey, Boolean> userProductCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean("transactionSumCache")
    public Cache<QueryKey, Double> transactionSumCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean("depositWithdrawCache")
    public Cache<CacheKey, DepositWithdrawSum> depositWithdrawCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }
}