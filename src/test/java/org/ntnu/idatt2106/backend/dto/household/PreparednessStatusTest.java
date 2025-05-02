package org.ntnu.idatt2106.backend.dto.household;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PreparednessStatusTest {

  @Test
  @DisplayName("Test PreparednessStatus constructor sets fields correctly")
  void testConstructorSetsFields() {
    PreparednessStatus dto = new PreparednessStatus(4.5, 3.2);
    assertEquals(4.5, dto.getDaysOfFood(), 0.001);
    assertEquals(3.2, dto.getDaysOfWater(), 0.001);
  }

  @Test
  @DisplayName("Test PreparednessStatus getters and setters")
  void testGettersAndSetters() {
    PreparednessStatus dto = new PreparednessStatus(4.5, 3.2);
    dto.setDaysOfFood(6.0);
    dto.setDaysOfWater(2.0);

    assertEquals(6.0, dto.getDaysOfFood(), 0.001);
    assertEquals(2.0, dto.getDaysOfWater(), 0.001);
  }
}