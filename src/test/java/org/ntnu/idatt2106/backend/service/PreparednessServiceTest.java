package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.Unit;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreparednessServiceTest {

    @InjectMocks
    private PreparednessService preparednessService;

    @Mock
    private HouseholdRepo householdRepo;

    private Household household;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        household = new Household();
    }

    @Test
    @DisplayName("Should return 0 days if no members")
    void testNoMembers() {
        household.setMembers(Collections.emptyList());

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertEquals(0, status.getDaysOfFood(), 0.01);
        assertEquals(0, status.getDaysOfWater(), 0.01);
    }

    @Test
    @DisplayName("Should ignore expired items")
    void testExpiredItemsIgnored() {
        household.setMembers(Collections.singletonList(new HouseholdMembers()));

        Item expired = new Item();
        expired.setCategory(new Category(1, "Vann", null, false));
        expired.setAmount(5);
        expired.setUnit(new Unit("l"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1); // Yesterday
        expired.setExpirationDate(cal.getTime());

        household.setInventory(Collections.singletonList(expired));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertEquals(0, status.getDaysOfFood(), 0.01);
        assertEquals(0, status.getDaysOfWater(), 0.01);
    }

    @Test
    @DisplayName("Should calculate under 3 days supply")
    void testUnder3DaysSupply() {
        household.setMembers(Collections.singletonList(new HouseholdMembers()));

        Item water = new Item();
        water.setCategory(new Category(1, "Vann", null, false));
        water.setAmount(2); // 2L
        water.setUnit(new Unit("l"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 5);
        water.setExpirationDate(cal.getTime());

        household.setInventory(Collections.singletonList(water));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertTrue(status.getDaysOfWater() < 3);
        assertEquals(0, status.getDaysOfFood(), 0.01);
    }

    @Test
    @DisplayName("Should calculate between 3 and 7 days supply")
    void testThreeToSevenDaysSupply() {
        household.setMembers(Collections.singletonList(new HouseholdMembers()));

        // 6000 kcal, 10L water
        Item food = new Item();
        food.setCategory(new Category(1, "Mat", 500, false));
        food.setAmount(12); // 6000 kcal
        food.setUnit(new Unit("stk"));

        Item water = new Item();
        water.setCategory(new Category(2, "Vann", null, false));
        water.setAmount(10); // 10L
        water.setUnit(new Unit("l"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 10);
        Date futureDate = cal.getTime();

        food.setExpirationDate(futureDate);
        water.setExpirationDate(futureDate);

        household.setInventory(Arrays.asList(food, water));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertTrue(status.getDaysOfFood() >= 3 && status.getDaysOfFood() < 7);
        assertTrue(status.getDaysOfWater() >= 3 && status.getDaysOfWater() < 7);
    }

    @Test
    @DisplayName("Should calculate 7+ days supply")
    void testSevenDaysOrMoreSupply() {
        household.setMembers(Collections.singletonList(new HouseholdMembers()));

        Item food = new Item();
        food.setCategory(new Category(1, "Mat", 500, false));
        food.setAmount(30); // 15000 kcal
        food.setUnit(new Unit("stk"));

        Item water = new Item();
        water.setCategory(new Category(2, "Vann", null, false));
        water.setAmount(25); // 25L
        water.setUnit(new Unit("l"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 10);
        Date futureDate = cal.getTime();

        food.setExpirationDate(futureDate);
        water.setExpirationDate(futureDate);

        household.setInventory(Arrays.asList(food, water));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertTrue(status.getDaysOfFood() >= 7);
        assertTrue(status.getDaysOfWater() >= 7);
    }

    @Test
    @DisplayName("Should return status for valid household ID")
    void testGetPreparednessStatusByIdSuccess() {
        Household household = new Household();
        household.setMembers(Collections.singletonList(new HouseholdMembers()));
        household.setInventory(Collections.emptyList());

        when(householdRepo.findById(1)).thenReturn(java.util.Optional.of(household));

        PreparednessStatus status = preparednessService.getPreparednessStatusByHouseholdId(1);

        assertEquals(0, status.getDaysOfFood(), 0.01);
        assertEquals(0, status.getDaysOfWater(), 0.01);
        verify(householdRepo).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when household not found")
    void testGetPreparednessStatusByIdNotFound() {
        when(householdRepo.findById(99)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                preparednessService.getPreparednessStatusByHouseholdId(99)
        );

        verify(householdRepo).findById(99);
    }
}

