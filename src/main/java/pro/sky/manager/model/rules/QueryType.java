package pro.sky.manager.model.rules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QueryType {
    USER_OF("USER_OF"),
    ACTIVE_USER_OF("ACTIVE_USER_OF"),
    TRANSACTION_SUM_COMPARE("TRANSACTION_SUM_COMPARE"),
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");

    private final String value;

    QueryType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static QueryType fromString(String value) {
        for (QueryType type : QueryType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown query type: " + value);
    }
}