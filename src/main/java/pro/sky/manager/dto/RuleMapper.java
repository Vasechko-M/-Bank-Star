package pro.sky.manager.dto;

import org.springframework.stereotype.Component;
import pro.sky.manager.model.DynamicRule;
import pro.sky.manager.model.QueryCondition;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RuleMapper {

    public DynamicRule toEntity(RuleRequestDTO dto) {
        DynamicRule rule = new DynamicRule();
        rule.setProductId(dto.getProductId());
        rule.setProductName(dto.getProductName());
        rule.setProductText(dto.getProductText());

        List<QueryCondition> conditions = dto.getRule().stream()
                .map(this::toQueryEntity)
                .collect(Collectors.toList());
        rule.setRule(conditions);

        return rule;
    }

    public RuleResponseDTO toResponseDTO(DynamicRule entity) {
        RuleResponseDTO dto = new RuleResponseDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductText(entity.getProductText());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<QueryConditionDTO> queries = entity.getRule().stream()
                .map(this::toQueryDTO)
                .collect(Collectors.toList());
        dto.setRule(queries);

        return dto;
    }

    private QueryCondition toQueryEntity(QueryConditionDTO dto) {
        QueryCondition condition = new QueryCondition();
        condition.setQuery(dto.getQuery());
        condition.setArguments(dto.getArguments());
        condition.setNegate(dto.isNegate());
        return condition;
    }

    private QueryConditionDTO toQueryDTO(QueryCondition entity) {
        return new QueryConditionDTO(
                entity.getQuery(),
                entity.getArguments(),
                entity.isNegate()
        );
    }
}