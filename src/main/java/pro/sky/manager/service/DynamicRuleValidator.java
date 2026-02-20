package pro.sky.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.manager.model.rules.QueryCondition;

import java.util.List;

@Component
@Slf4j
public class DynamicRuleValidator {

    private static final List<String> VALID_PRODUCT_TYPES = List.of("DEBIT", "CREDIT", "INVEST", "SAVING");
    private static final List<String> VALID_TRANSACTION_TYPES = List.of("DEPOSIT", "WITHDRAWAL");
    private static final List<String> VALID_OPERATORS = List.of(">", "<", "=", ">=", "<=");

    public void validateRuleConditions(List<QueryCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Rule must contain at least one condition");
        }

        for (QueryCondition condition : conditions) {
            validateQueryCondition(condition);
        }
    }

    private void validateQueryCondition(QueryCondition condition) {
        if (condition.getQuery() == null) {
            throw new IllegalArgumentException("Query type cannot be null");
        }

        List<String> arguments = condition.getArguments();
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Arguments cannot be empty for query type: %s", condition.getQuery())
            );
        }

        switch (condition.getQuery()) {
            case USER_OF:
            case ACTIVE_USER_OF:
                validateUserOfArguments(arguments);
                break;
            case TRANSACTION_SUM_COMPARE:
                validateTransactionSumCompareArguments(arguments);
                break;
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW:
                validateDepositWithdrawArguments(arguments);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type: " + condition.getQuery());
        }
    }

    private void validateUserOfArguments(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("USER_OF/ACTIVE_USER_OF requires exactly 1 argument (product type)");
        }
        validateProductType(arguments.get(0));
    }

    private void validateTransactionSumCompareArguments(List<String> arguments) {
        if (arguments.size() != 4) {
            throw new IllegalArgumentException("TRANSACTION_SUM_COMPARE requires exactly 4 arguments");
        }
        validateProductType(arguments.get(0));
        validateTransactionType(arguments.get(1));
        validateOperator(arguments.get(2));
        validateConstant(arguments.get(3));
    }

    private void validateDepositWithdrawArguments(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW requires exactly 2 arguments");
        }
        validateProductType(arguments.get(0));
        validateOperator(arguments.get(1));
    }

    private void validateProductType(String productType) {
        if (!VALID_PRODUCT_TYPES.contains(productType)) {
            throw new IllegalArgumentException(
                    String.format("Invalid product type: %s. Must be one of: %s",
                            productType, VALID_PRODUCT_TYPES)
            );
        }
    }

    private void validateTransactionType(String transactionType) {
        if (!VALID_TRANSACTION_TYPES.contains(transactionType)) {
            throw new IllegalArgumentException(
                    String.format("Invalid transaction type: %s. Must be one of: %s",
                            transactionType, VALID_TRANSACTION_TYPES)
            );
        }
    }

    private void validateOperator(String operator) {
        if (!VALID_OPERATORS.contains(operator)) {
            throw new IllegalArgumentException(
                    String.format("Invalid operator: %s. Must be one of: %s",
                            operator, VALID_OPERATORS)
            );
        }
    }

    private void validateConstant(String constantStr) {
        try {
            int constant = Integer.parseInt(constantStr);
            if (constant < 0) {
                throw new IllegalArgumentException("Constant must be non-negative: " + constant);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Constant must be an integer: " + constantStr);
        }
    }
}