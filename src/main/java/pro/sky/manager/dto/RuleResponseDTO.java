package pro.sky.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ с информацией о правиле")
public class RuleResponseDTO {

    @Schema(description = "ID правила", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Название продукта", example = "Простой кредит")
    private String productName;

    @Schema(description = "UUID продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
    private UUID productId;

    @Schema(description = "Текст рекомендации", example = "Откройте мир выгодных кредитов...")
    private String productText;

    @Schema(description = "Список условий правила")
    private List<QueryConditionDTO> rule;

    @Schema(description = "Дата создания", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Дата обновления", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}