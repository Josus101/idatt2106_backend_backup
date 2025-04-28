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

    @Test
    @DisplayName("Should return warning when household has no members")
    void testNoMembers() {
        household.setMembers(Collections.emptyList());

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertTrue(status.isWarning());
        assertEquals(0, status.getPreparednessPercent());
        assertEquals("Ingen medlemmer i husstanden", status.getMessage());
    }

    @Test
    @DisplayName("Should ignore expired items in inventory")
    void testExpiredItemsAreIgnored() {
        household.setMembers(Collections.singletonList(new HouseholdMembers())); // 1 member

        Item expiredItem = new Item();
        expiredItem.setCategory(new Category(1, "Vann", null, false));
        expiredItem.setAmount(5);
        expiredItem.setUnit(new Unit("l"));

        // Set expiration date to past
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // yesterday
        expiredItem.setExpirationDate(calendar.getTime());

        household.setInventory(Collections.singletonList(expiredItem));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertTrue(status.isWarning());
        assertEquals("Lageret dekker ikke 3 dager med mat og vann", status.getMessage());
    }

    @Test
    @DisplayName("Should cover 3 days but not 7")
    void testCover3DaysNot7() {
        household.setMembers(Collections.singletonList(new HouseholdMembers())); // 1 member

        // Water
        Item water = new Item();
        water.setCategory(new Category(1, "vann", null, false));
        water.setAmount(10); // 10 liters
        water.setUnit(new Unit("l"));

        // Food
        Category foodCategory = new Category(2, "Mat", 500, false); // 500 kcal per unit
        Item food = new Item();
        food.setCategory(foodCategory);
        food.setAmount(12); // 12 * 500 = 6000 kcal (enough for 3 days but not 7 days)
        food.setUnit(new Unit("stk"));

        // Future expiration
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        Date futureDate = calendar.getTime();
        water.setExpirationDate(futureDate);
        food.setExpirationDate(futureDate);

        household.setInventory(Arrays.asList(water, food));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

//        assertFalse(status.isWarning());
        assertEquals("Lageret dekker 3 dager, men ikke 7", status.getMessage());
    }

    @Test
    @DisplayName("Should cover 7 days")
    void testCover7Days() {
        household.setMembers(Collections.singletonList(new HouseholdMembers())); // 1 member

        // Water
        Item water = new Item();
        water.setCategory(new Category(1, "Vann", null, false));
        water.setAmount(25); // 25 liters
        water.setUnit(new Unit("l"));

        // Food
        Category foodCategory = new Category(2, "Mat", 500, false); // 500 kcal per unit
        Item food = new Item();
        food.setCategory(foodCategory);
        food.setAmount(28); // 28 * 500 = 14000 kcal (enough for 7 days)
        food.setUnit(new Unit("stk"));

        // Future expiration
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        Date futureDate = calendar.getTime();
        water.setExpirationDate(futureDate);
        food.setExpirationDate(futureDate);

        household.setInventory(Arrays.asList(water, food));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

//        assertFalse(status.isWarning());
        assertEquals("Lageret dekker minst 7 dager", status.getMessage());
    }



    @Test
    @DisplayName("Should trigger warning when essentials are missing")
    void testMissingEssentials() {
        household.setMembers(Collections.singletonList(new HouseholdMembers())); // 1 member

        // Enough food or water
        Item water = new Item();
        water.setCategory(new Category(1, "Vann", null, false));
        water.setAmount(30);
        water.setUnit(new Unit("l"));

        // Future expiration
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        water.setExpirationDate(calendar.getTime());

        household.setInventory(Collections.singletonList(water));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        // Should warn because Førstehjelp and Gassbrenner are missing
        assertTrue(status.isWarning());
    }


}
