package pro.sky.manager.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pro.sky.manager.dto.RuleListResponseDTO;
import pro.sky.manager.dto.RuleRequestDTO;
import pro.sky.manager.dto.RuleResponseDTO;
import pro.sky.manager.service.DynamicRuleCrudService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleControllerTest {

    @Mock
    private DynamicRuleCrudService ruleService;

    @InjectMocks
    private RuleController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ createRule
    @Test
    void createRule_success() {
        RuleRequestDTO request = mock(RuleRequestDTO.class);
        RuleResponseDTO responseDto = mock(RuleResponseDTO.class);

        when(ruleService.createRule(request)).thenReturn(responseDto);

        ResponseEntity<RuleResponseDTO> response =
                controller.createRule(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());

        verify(ruleService).createRule(request);
    }

    // ✅ getAllRules
    @Test
    void getAllRules_success() {
        RuleListResponseDTO listResponse = mock(RuleListResponseDTO.class);

        when(ruleService.getAllRules()).thenReturn(listResponse);

        ResponseEntity<RuleListResponseDTO> response =
                controller.getAllRules();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listResponse, response.getBody());

        verify(ruleService).getAllRules();
    }

    // ✅ deleteRule
    @Test
    void deleteRule_success() {
        UUID productId = UUID.randomUUID();

        doNothing().when(ruleService).deleteRule(productId);

        ResponseEntity<String> response =
                controller.deleteRule(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains(productId.toString()));

        verify(ruleService).deleteRule(productId);
    }

    // ✅ getRuleByProductId
    @Test
    void getRuleByProductId_success() {
        UUID productId = UUID.randomUUID();
        RuleResponseDTO responseDto = mock(RuleResponseDTO.class);

        when(ruleService.getRuleByProductId(productId))
                .thenReturn(responseDto);

        ResponseEntity<RuleResponseDTO> response =
                controller.getRuleByProductId(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());

        verify(ruleService).getRuleByProductId(productId);
    }
}