package pro.sky.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.manager.dto.RuleListResponseDTO;
import pro.sky.manager.dto.RuleRequestDTO;
import pro.sky.manager.dto.RuleResponseDTO;
import pro.sky.manager.service.DynamicRuleCrudService;

import java.util.UUID;

@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
@Tag(name = "Динамические правила", description = "API для управления динамическими правилами рекомендаций")
public class RuleController {

    private final DynamicRuleCrudService ruleService;

    @PostMapping
    @Operation(summary = "Создать новое динамическое правило")
    public ResponseEntity<RuleResponseDTO> createRule(@Valid @RequestBody RuleRequestDTO request) {
        RuleResponseDTO response = ruleService.createRule(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Получить все динамические правила")
    public ResponseEntity<RuleListResponseDTO> getAllRules() {
        RuleListResponseDTO response = ruleService.getAllRules();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить правило по ID продукта")
    public ResponseEntity<Void> deleteRule(
            @Parameter(description = "ID продукта", required = true)
            @PathVariable UUID productId) {
        ruleService.deleteRule(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Получить правило по ID продукта")
    public ResponseEntity<RuleResponseDTO> getRuleByProductId(@PathVariable UUID productId) {
        RuleResponseDTO response = ruleService.getRuleByProductId(productId);
        return ResponseEntity.ok(response);
    }
}