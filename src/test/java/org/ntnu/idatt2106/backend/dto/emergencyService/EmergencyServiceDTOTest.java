package org.ntnu.idatt2106.backend.dto.emergencyService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmergencyServiceDTOTest {
    @Test
    @DisplayName("Test EmergencyServiceDTO constructor sets fields correctly")
    void testConstructorSetsFields() {
        EmergencyServiceDTO dto = new EmergencyServiceDTO(
                "Torget 6",
                "Plasser: 390",
                59.123456,
                10.654321,
                "Bunker"
        );

        assertEquals("Torget 6", dto.getName());
        assertEquals("Plasser: 390", dto.getDescription());
        assertEquals(59.123456, dto.getLatitude());
        assertEquals(10.654321, dto.getLongitude());
        assertEquals("Bunker", dto.getType());
    }

    @Test
    @DisplayName("Test EmergencyServiceDTO getters and setters")
    void testGettersAndSetters() {
        EmergencyServiceDTO dto = new EmergencyServiceDTO(
                "Old Name",
                "Old Description",
                0.0,
                0.0,
                "Old Type"
        );

        dto.setName("New Name");
        dto.setDescription("New Description");
        dto.setLatitude(58.987654);
        dto.setLongitude(11.123456);
        dto.setType("Shelter");

        assertEquals("New Name", dto.getName());
        assertEquals("New Description", dto.getDescription());
        assertEquals(58.987654, dto.getLatitude());
        assertEquals(11.123456, dto.getLongitude());
        assertEquals("Shelter", dto.getType());
    }

}