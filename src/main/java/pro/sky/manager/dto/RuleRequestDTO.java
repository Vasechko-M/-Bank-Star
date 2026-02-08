package pro.sky.manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pro.sky.manager.model.QueryType;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Запрос на создание правила рекомендации")
public class RuleRequestDTO {

    @NotBlank(message = "Название продукта не может быть пустым")
    @Schema(description = "Название продукта", example = "Простой кредит", required = true)
    private String productName;

    @NotNull(message = "ID продукта не может быть null")
    @Schema(description = "UUID продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f", required = true)
    private UUID productId;

    @NotBlank(message = "Текст рекомендации не может быть пустым")
    @Schema(description = "Текст рекомендации", example = "Откройте мир выгодных кредитов...", required = true)
    private String productText;

    @NotEmpty(message = "Правило не может быть пустым")
    @Valid
    @Schema(description = "Список условий правила", required = true)
    private List<QueryConditionDTO> rule;
}