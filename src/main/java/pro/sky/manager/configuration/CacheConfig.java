package pro.sky.manager.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.sky.manager.cache.CacheKey;
import pro.sky.manager.cache.QueryKey;
import pro.sky.manager.dto.DepositWithdrawSum;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean("userProductTypesCacheBean")
    public Cache<UUID, List<String>> userProductTypesCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean("userProductCacheBean")
    public Cache<CacheKey, Boolean> userProductCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean("transactionSumCacheBean")
    public Cache<QueryKey, Double> transactionSumCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean("depositWithdrawCacheBean")
    public Cache<CacheKey, DepositWithdrawSum> depositWithdrawCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("recommendationCache", "userRecommendations");
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    private Caffeine caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .maximumSize(1024 * 1024 * 256);
    }
}