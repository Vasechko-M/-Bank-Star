package pro.sky.manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ со списком правил")
public class RuleListResponseDTO {

    @Schema(description = "Список правил")
    private List<RuleResponseDTO> data;
}