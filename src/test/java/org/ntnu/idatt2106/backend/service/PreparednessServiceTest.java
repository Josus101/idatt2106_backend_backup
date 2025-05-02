package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreparednessServiceTest {

    @InjectMocks
    private PreparednessService preparednessService;

    @Mock
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return preparedness status for all user's households")
    void testGetPreparednessStatusByUserIdSuccess() {
        // Arrange
        User user = new User();
        Household household = new Household();
        household.setMembers(Collections.singletonList(new HouseholdMembers()));
        household.setInventory(Collections.emptyList());

        HouseholdMembers membership = new HouseholdMembers();
        membership.setHousehold(household);
        user.setHouseholdMemberships(List.of(membership));

        when(userRepo.findById(1)).thenReturn(java.util.Optional.of(user));

        // Act
        List<PreparednessStatus> result = preparednessService.getPreparednessStatusByUserId(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getDaysOfFood(), 0.01);
        assertEquals(0, result.get(0).getDaysOfWater(), 0.01);
        verify(userRepo).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testUserNotFound() {
        when(userRepo.findById(99)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                preparednessService.getPreparednessStatusByUserId(99)
        );

        verify(userRepo).findById(99);
    }

    @Test
    @DisplayName("Should throw exception when user has no households")
    void testUserWithNoHouseholds() {
        User user = new User();
        user.setHouseholdMemberships(Collections.emptyList());

        when(userRepo.findById(42)).thenReturn(java.util.Optional.of(user));

        assertThrows(NoSuchElementException.class, () ->
                preparednessService.getPreparednessStatusByUserId(42)
        );

        verify(userRepo).findById(42);
    }

    @Test
    @DisplayName("Should return 0 days when household has no members")
    void testNoMembersReturnsZeroDays() {
        Household emptyHousehold = new Household();
        emptyHousehold.setMembers(Collections.emptyList());
        emptyHousehold.setInventory(Collections.emptyList());

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(emptyHousehold);

        assertEquals(0, status.getDaysOfFood());
        assertEquals(0, status.getDaysOfWater());
    }

    @Test
    @DisplayName("Should correctly calculate food and water with mixed inventory")
    void testMixedInventoryCalculation() {
        Household household = new Household();

        // 1 medlem
        household.setMembers(List.of(new HouseholdMembers()));

        Calendar cal = Calendar.getInstance();

        // Utgått vann (skal ignoreres)
        Item expiredWater = new Item();
        expiredWater.setAmount(10);
        expiredWater.setUnit(new Unit("L"));
        expiredWater.setCategory(new Category(1, "Water", null, false));
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        expiredWater.setExpirationDate(cal.getTime());

        // Mat uten kcalPerUnit (skal ikke regnes som mat)
        Item invalidFood = new Item();
        invalidFood.setAmount(5);
        invalidFood.setUnit(new Unit("Stk"));
        invalidFood.setCategory(new Category(2, "Snacks", null, false));
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 10);
        invalidFood.setExpirationDate(cal.getTime());

        // Gyldig vann
        Item validWater = new Item();
        validWater.setAmount(6); // 6L
        validWater.setUnit(new Unit("L"));
        validWater.setCategory(new Category(3, "Vann", null, false));
        validWater.setExpirationDate(cal.getTime());

        // Gyldig mat
        Item validFood = new Item();
        validFood.setAmount(4); // 4 * 500 = 2000 kcal = 1 dag
        validFood.setUnit(new Unit("Stk"));
        validFood.setCategory(new Category(4, "Mat", 500, false));
        validFood.setExpirationDate(cal.getTime());

        household.setInventory(List.of(expiredWater, invalidFood, validWater, validFood));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);

        assertEquals(1, status.getDaysOfFood(), 0.01);
        assertEquals(2, status.getDaysOfWater(), 0.01); // 6L / (1*3)
    }


}

