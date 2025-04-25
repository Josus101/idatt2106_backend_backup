package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.Unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PreparednessServiceTest {

    @InjectMocks
    private PreparednessService preparednessService;

    private Household household;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        household = new Household();
    }

    @Test
    @DisplayName("Should return warning if inventory does not cover 3 days")
    void testBelow3DaysCoverage() {
        household.setMembers(Arrays.asList(new HouseholdMembers(), new HouseholdMembers())); // Mocked 2 members

        Item water = new Item();
        water.setCategory(new Category(1, "Vann", null, false)); // Fixed constructor
        water.setAmount(5); // 5 liters
        water.setUnit(new Unit("l"));

        // Set expiration date to a future date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 10); // 10 days in the future
        water.setExpirationDate(calendar.getTime());

        household.setInventory(Collections.singletonList(water));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertTrue(status.isWarning());
        assertEquals("Lageret dekker ikke 3 dager med mat og vann", status.getMessage());
    }
}
