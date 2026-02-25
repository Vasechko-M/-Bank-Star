package pro.sky.manager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pro.sky.manager.model.QueryType;

import java.util.List;

@Data
@Schema(description = "Условие запроса в правиле")
public class QueryConditionDTO {

    @NotNull(message = "Тип запроса не может быть null")
    @Schema(description = "Тип запроса", example = "USER_OF", required = true)
    private QueryType query;

    @NotEmpty(message = "Аргументы не могут быть пустыми")
    @Schema(description = "Аргументы запроса", example = "[\"CREDIT\"]", required = true)
    private List<String> arguments;

    @Schema(description = "Отрицание условия", example = "true", defaultValue = "false")
    private boolean negate;

    @JsonCreator
    public QueryConditionDTO(
            @JsonProperty("query") QueryType query,
            @JsonProperty("arguments") List<String> arguments,
            @JsonProperty("negate") boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }
}