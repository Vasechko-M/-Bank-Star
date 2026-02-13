package pro.sky.manager.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class QueryKey {
    private final UUID userId;
    private final String productType;
    private final String transactionType;
    private final String operator;
    private final int constant;
}