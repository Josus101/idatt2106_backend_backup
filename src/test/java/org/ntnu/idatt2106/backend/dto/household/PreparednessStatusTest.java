package org.ntnu.idatt2106.backend.dto.household;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PreparednessStatusTest {

  @Test
  @DisplayName("Test PreparednessStatus constructor sets fields correctly")
  void testConstructorSetsFields() {
    PreparednessStatus dto = new PreparednessStatus(
        75,
        true,
        "Lageret ditt dekker minst 7 dager"
    );
    assertEquals(75, dto.getPreparednessPercent());
    assertEquals(true, dto.isWarning());
    assertEquals("Lageret ditt dekker minst 7 dager", dto.getMessage());
  }

  @Test
  @DisplayName("Test PreparednessStatus getters and setters")
  void testGettersAndSetters() {
    PreparednessStatus dto = new PreparednessStatus(
        75,
        true,
        "Lageret ditt dekker minst 7 dager"
    );
    dto.setPreparednessPercent(50);
    dto.setWarning(false);
    dto.setMessage("Lageret ditt dekker kun 3 dager");

    assertEquals(50, dto.getPreparednessPercent());
    assertEquals(false, dto.isWarning());
    assertEquals("Lageret ditt dekker kun 3 dager", dto.getMessage());
  }
}
