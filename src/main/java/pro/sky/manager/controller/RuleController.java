package pro.sky.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.manager.dto.RuleListResponseDTO;
import pro.sky.manager.dto.RuleRequestDTO;
import pro.sky.manager.dto.RuleResponseDTO;
import pro.sky.manager.service.RuleService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
@Tag(name = "Rules", description = "API для управления динамическими правилами рекомендаций")
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    @Operation(summary = "Создать новое правило")
    public ResponseEntity<RuleResponseDTO> createRule(@Valid @RequestBody RuleRequestDTO request) {
        RuleResponseDTO response = ruleService.createRule(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Получить все правила")
    public ResponseEntity<RuleListResponseDTO> getAllRules() {
        RuleListResponseDTO response = ruleService.getAllRules();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить правило по ID продукта")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID productId) {
        ruleService.deleteRule(productId);
        return ResponseEntity.noContent().build();
    }
}