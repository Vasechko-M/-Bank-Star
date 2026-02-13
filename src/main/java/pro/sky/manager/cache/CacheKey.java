package pro.sky.manager.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CacheKey {
    private final String type;
    private final UUID userId;
    private final String productType;
}