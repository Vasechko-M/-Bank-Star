package pro.sky.manager.service;

import org.junit.jupiter.api.Test;
import pro.sky.manager.model.QueryCondition;
import pro.sky.manager.model.QueryType;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DynamicRuleValidatorTest {

    private final DynamicRuleValidator validator = new DynamicRuleValidator();

    @Test
    void validateRuleConditions_EmptyList_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                validator.validateRuleConditions(List.of()));
    }

    @Test
    void validateQueryCondition_InvalidProductType_ThrowsException() {
        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.USER_OF);
        condition.setArguments(List.of("INVALID_TYPE"));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateRuleConditions(List.of(condition)));
    }

    @Test
    void validateTransactionSumCompare_ValidArgs_Success() {
        QueryCondition condition = new QueryCondition();
        condition.setQuery(QueryType.TRANSACTION_SUM_COMPARE);
        condition.setArguments(List.of("DEBIT", "DEPOSIT", ">", "1000"));

        assertDoesNotThrow(() -> validator.validateRuleConditions(List.of(condition)));
    }
}