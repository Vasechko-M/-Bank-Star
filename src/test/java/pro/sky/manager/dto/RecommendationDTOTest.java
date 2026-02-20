package pro.sky.manager.dto;

import org.junit.jupiter.api.Test;
import pro.sky.manager.model.rules.RecommendationDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RecommendationDTOTest {

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        RecommendationDTO dto1 = new RecommendationDTO(id, "Name", "Text");
        RecommendationDTO dto2 = new RecommendationDTO(id, "Name", "Text");
        RecommendationDTO dto3 = new RecommendationDTO(UUID.randomUUID(), "Name", "Text");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        RecommendationDTO dto = new RecommendationDTO(id, "Name", "Text");

        assertEquals(id, dto.getId());
        assertEquals("Name", dto.getName());
        assertEquals("Text", dto.getText());

        UUID newId = UUID.randomUUID();
        dto.setId(newId);
        dto.setName("New Name");
        dto.setText("New Text");

        assertEquals(newId, dto.getId());
        assertEquals("New Name", dto.getName());
        assertEquals("New Text", dto.getText());
    }
}
