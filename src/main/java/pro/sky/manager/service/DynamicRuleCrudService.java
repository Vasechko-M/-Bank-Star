package pro.sky.manager.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.manager.cache.CacheKey;
import pro.sky.manager.cache.QueryKey;
import pro.sky.manager.dto.DepositWithdrawSum;
import pro.sky.manager.dto.RuleListResponseDTO;
import pro.sky.manager.dto.RuleRequestDTO;
import pro.sky.manager.dto.RuleResponseDTO;
import pro.sky.manager.dto.RuleMapper;
import pro.sky.manager.exception.RuleAlreadyExistsException;
import pro.sky.manager.exception.RuleNotFoundException;
import pro.sky.manager.model.DynamicRule;
import pro.sky.manager.model.QueryCondition;
import pro.sky.manager.model.RecommendationRuleStat;
import pro.sky.manager.repository.DynamicRuleRepository;
import pro.sky.manager.repository.RecommendationRuleStatsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DynamicRuleCrudService {

    private final DynamicRuleRepository ruleRepository;
    private final RuleMapper ruleMapper;
    private final DynamicRuleValidator ruleValidator;

    @Autowired
    private RecommendationRuleStatsRepository statsRepo;

    @Qualifier("userProductTypesCacheBean")
    private final Cache<UUID, List<String>> userProductTypesCache;

    @Qualifier("userProductCacheBean")
    private final Cache<CacheKey, Boolean> userProductCache;

    @Qualifier("transactionSumCacheBean")
    private final Cache<QueryKey, Double> transactionSumCache;

    @Qualifier("depositWithdrawCacheBean")
    private final Cache<CacheKey, DepositWithdrawSum> depositWithdrawCache;

    /**
     * Удаляет правило вместе с соответствующей статистикой.
     */
    public void deleteRuleStat(UUID ruleId) {
        Optional<DynamicRule> ruleOptional = ruleRepository.findById(ruleId);
        if(ruleOptional.isPresent()) {
            DynamicRule rule = ruleOptional.get();
            log.info("Deleting rule stat for ruleId: {}", ruleId);

            RecommendationRuleStat stats = statsRepo.findByRuleId(rule.getId());
            if(stats != null) {
                statsRepo.delete(stats);
            }
            log.info("Rule stat deleted for ruleId: {}", ruleId);
        }
    }

    @Transactional
    public RuleResponseDTO createRule(RuleRequestDTO request) {
        try {
        log.info("Creating new rule for product: {}", request.getProductName());

        if (ruleRepository.existsByProductId(request.getProductId())) {
            throw new RuleAlreadyExistsException(
                    String.format("Rule for productId %s already exists", request.getProductId())
            );
        }

        DynamicRule rule = ruleMapper.toEntity(request);

        if (rule.getRule() != null) {
            for (int i = 0; i < rule.getRule().size(); i++) {
                QueryCondition condition = rule.getRule().get(i);
                condition.setOrder(i);
                condition.setRule(rule);
            }
            ruleValidator.validateRuleConditions(rule.getRule());
        }

        DynamicRule savedRule = ruleRepository.save(rule);
        log.info("Rule created with id: {}", savedRule.getId());

        invalidateAllCaches();

        return ruleMapper.toResponseDTO(savedRule);
        } catch (Exception e) {
            log.error("Error while creating rule", e);
            throw e;

        }
    }

    public RuleListResponseDTO getAllRules() {
        log.debug("Fetching all rules");
        List<DynamicRule> rules = ruleRepository.findAllWithConditions();

        List<RuleResponseDTO> ruleDTOs = rules.stream()
                .map(ruleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new RuleListResponseDTO(ruleDTOs);
    }

    @Transactional
    public void deleteRule(UUID productId) {
        log.info("Deleting rule and stat for productId: {}", productId);

        if (!ruleRepository.existsByProductId(productId)) {
            throw new RuleNotFoundException(
                    String.format("Rule for productId %s not found", productId)
            );
        }

        ruleRepository.deleteByProductId(productId);
        Optional<DynamicRule> ruleOptional = ruleRepository.findById(productId);
        if(ruleOptional.isPresent()) {
            DynamicRule rule = ruleOptional.get();
            RecommendationRuleStat stats = statsRepo.findByRuleId(rule.getId());
            if (stats != null) {
                statsRepo.delete(stats);
            }
        }
        log.info("Rule and stat deleted for productId: {}", productId);

        invalidateAllCaches();
    }

    public RuleResponseDTO getRuleByProductId(UUID productId) {
        DynamicRule rule = ruleRepository.findByProductId(productId)
                .orElseThrow(() -> new RuleNotFoundException(
                        String.format("Rule for productId %s not found", productId)
                ));
        return ruleMapper.toResponseDTO(rule);
    }

    private void invalidateAllCaches() {
        log.info("Invalidating all caches");
        userProductTypesCache.invalidateAll();
        userProductCache.invalidateAll();
        transactionSumCache.invalidateAll();
        depositWithdrawCache.invalidateAll();
        log.info("All caches invalidated");
    }
}