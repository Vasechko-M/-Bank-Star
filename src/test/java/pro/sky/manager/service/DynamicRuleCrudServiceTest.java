package pro.sky.manager.service;


import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.sky.manager.cache.CacheKey;
import pro.sky.manager.cache.QueryKey;
import pro.sky.manager.dto.*;
import pro.sky.manager.exception.RuleAlreadyExistsException;
import pro.sky.manager.exception.RuleNotFoundException;
import pro.sky.manager.model.DynamicRule;
import pro.sky.manager.model.QueryCondition;
import pro.sky.manager.repository.DynamicRuleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DynamicRuleCrudServiceTest {

    private DynamicRuleRepository repository;
    private RuleMapper mapper;
    private DynamicRuleValidator validator;

    private Cache<UUID, List<String>> userProductTypesCache;
    private Cache<CacheKey, Boolean> userProductCache;
    private Cache<QueryKey, Double> transactionSumCache;
    private Cache<CacheKey, DepositWithdrawSum> depositWithdrawCache;

    private DynamicRuleCrudService service;

    @BeforeEach
    void setUp() {
        repository = mock(DynamicRuleRepository.class);
        mapper = mock(RuleMapper.class);
        validator = mock(DynamicRuleValidator.class);

        userProductTypesCache = mock(Cache.class);
        userProductCache = mock(Cache.class);
        transactionSumCache = mock(Cache.class);
        depositWithdrawCache = mock(Cache.class);

        service = new DynamicRuleCrudService(
                repository,
                mapper,
                validator,
                userProductTypesCache,
                userProductCache,
                transactionSumCache,
                depositWithdrawCache
        );
    }

    @Test
    void createRule_success() {
        RuleRequestDTO request = mock(RuleRequestDTO.class);
        DynamicRule entity = new DynamicRule();
        DynamicRule saved = new DynamicRule();
        RuleResponseDTO responseDTO = mock(RuleResponseDTO.class);

        UUID productId = UUID.randomUUID();
        when(request.getProductId()).thenReturn(productId);

        when(repository.existsByProductId(productId)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponseDTO(saved)).thenReturn(responseDTO);

        RuleResponseDTO result = service.createRule(request);

        assertEquals(responseDTO, result);
        verify(repository).save(entity);

        verify(userProductTypesCache).invalidateAll();
        verify(userProductCache).invalidateAll();
        verify(transactionSumCache).invalidateAll();
        verify(depositWithdrawCache).invalidateAll();
    }

    @Test
    void createRule_alreadyExists() {
        RuleRequestDTO request = mock(RuleRequestDTO.class);
        UUID productId = UUID.randomUUID();
        when(request.getProductId()).thenReturn(productId);
        when(repository.existsByProductId(productId)).thenReturn(true);

        assertThrows(RuleAlreadyExistsException.class, () -> service.createRule(request));

        verify(repository, never()).save(any());
        verifyNoInteractions(userProductTypesCache, userProductCache, transactionSumCache, depositWithdrawCache);
    }

    @Test
    void getAllRules_success() {
        DynamicRule rule = new DynamicRule();
        RuleResponseDTO dto = mock(RuleResponseDTO.class);

        when(repository.findAllWithConditions()).thenReturn(List.of(rule));
        when(mapper.toResponseDTO(rule)).thenReturn(dto);

        RuleListResponseDTO result = service.getAllRules();

        assertEquals(1, result.getRules().size());
        verify(repository).findAllWithConditions();
    }

    @Test
    void deleteRule_success() {
        UUID productId = UUID.randomUUID();

        when(repository.existsByProductId(productId)).thenReturn(true);

        service.deleteRule(productId);

        verify(repository).deleteByProductId(productId);
        verify(userProductTypesCache).invalidateAll();
        verify(userProductCache).invalidateAll();
        verify(transactionSumCache).invalidateAll();
        verify(depositWithdrawCache).invalidateAll();
    }

    @Test
    void deleteRule_notFound() {
        UUID productId = UUID.randomUUID();
        when(repository.existsByProductId(productId)).thenReturn(false);

        assertThrows(RuleNotFoundException.class, () -> service.deleteRule(productId));

        verify(repository, never()).deleteByProductId(any());
        verifyNoInteractions(userProductTypesCache, userProductCache, transactionSumCache, depositWithdrawCache);
    }

    @Test
    void getRuleByProductId_success() {
        UUID productId = UUID.randomUUID();
        DynamicRule rule = new DynamicRule();
        RuleResponseDTO dto = mock(RuleResponseDTO.class);

        when(repository.findByProductId(productId)).thenReturn(Optional.of(rule));
        when(mapper.toResponseDTO(rule)).thenReturn(dto);

        RuleResponseDTO result = service.getRuleByProductId(productId);

        assertEquals(dto, result);
    }

    @Test
    void getRuleByProductId_notFound() {
        UUID productId = UUID.randomUUID();
        when(repository.findByProductId(productId)).thenReturn(Optional.empty());

        assertThrows(RuleNotFoundException.class, () -> service.getRuleByProductId(productId));
    }
}