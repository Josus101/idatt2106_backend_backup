package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.household.MyHouseholdStatusGetResponse;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
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
        User user = new User();
        Household household = new Household();
        household.setMembers(Collections.singletonList(new HouseholdMembers()));
        household.setInventory(Collections.emptyList());

        HouseholdMembers membership = new HouseholdMembers();
        membership.setHousehold(household);
        user.setHouseholdMemberships(List.of(membership));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        
        List<MyHouseholdStatusGetResponse> result = preparednessService.getPreparednessStatusByUserId(1);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStatus().getDaysOfFood(), 0.01);
        assertEquals(0, result.get(0).getStatus().getDaysOfWater(), 0.01);
        verify(userRepo).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testUserNotFound() {
        when(userRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                preparednessService.getPreparednessStatusByUserId(99)
        );

        verify(userRepo).findById(99);
    }

    @Test
    @DisplayName("Should throw exception when user has no households")
    void testUserWithNoHouseholds() {
        User user = new User();
        user.setHouseholdMemberships(Collections.emptyList());

        when(userRepo.findById(42)).thenReturn(Optional.of(user));

        assertThrows(NoSuchElementException.class, () ->
                preparednessService.getPreparednessStatusByUserId(42)
        );

        verify(userRepo).findById(42);
    }

    @Test
    @DisplayName("")
    void testGetPreparednessStatusByUserIdWithPrimaryHousehold() {
        User user = new User();
        Household primaryHousehold = new Household();
        primaryHousehold.setId(1);
        primaryHousehold.setMembers(Collections.singletonList(new HouseholdMembers()));
        primaryHousehold.setInventory(Collections.emptyList());

        Household secondaryHousehold = new Household();
        secondaryHousehold.setId(2);
        secondaryHousehold.setMembers(Collections.singletonList(new HouseholdMembers()));
        secondaryHousehold.setInventory(Collections.emptyList());

        user.setHouseholdMemberships(List.of(
                new HouseholdMembers(user, primaryHousehold, false, true),
                new HouseholdMembers(user, secondaryHousehold, false, false)
        ));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        List<MyHouseholdStatusGetResponse> result = preparednessService.getPreparednessStatusByUserId(1);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }
    @Test
    @DisplayName("Should return household statuses without sorting when primary household is null")
    void testGetPreparednessStatusByUserId_NoPrimaryHousehold() {
        User user = new User();
        Household household1 = new Household();
        household1.setId(1);
        Household household2 = new Household();
        household2.setId(2);

        user.setHouseholdMemberships(List.of(
                new HouseholdMembers(user, household1, false, false),
                new HouseholdMembers(user, household2, false, false)
        ));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        
        List<MyHouseholdStatusGetResponse> result = preparednessService.getPreparednessStatusByUserId(1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    @DisplayName("Should return household statuses with primary household first when already first")
    void testGetPreparednessStatusByUserId_PrimaryHouseholdAlreadyFirst() {
        User user = new User();
        Household primaryHousehold = new Household();
        primaryHousehold.setId(1);
        Household secondaryHousehold = new Household();
        secondaryHousehold.setId(2);

        user.setHouseholdMemberships(List.of(
                new HouseholdMembers(user, primaryHousehold, false, true),
                new HouseholdMembers(user, secondaryHousehold, false, false)
        ));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        List<MyHouseholdStatusGetResponse> result = preparednessService.getPreparednessStatusByUserId(1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    @DisplayName("Should move primary household to the first position when not already first")
    void testGetPreparednessStatusByUserId_PrimaryHouseholdNotFirst() {
        User user = new User();
        Household primaryHousehold = new Household();
        primaryHousehold.setId(2);
        Household secondaryHousehold = new Household();
        secondaryHousehold.setId(1);

        user.setHouseholdMemberships(List.of(
                new HouseholdMembers(user, secondaryHousehold, false, false),
                new HouseholdMembers(user, primaryHousehold, false, true)
        ));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        
        List<MyHouseholdStatusGetResponse> result = preparednessService.getPreparednessStatusByUserId(1);
        
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getId());
        assertEquals(1, result.get(1).getId());
    }

    @Test
    @DisplayName("Should handle an empty list of household memberships")
    void testGetPreparednessStatusByUserId_EmptyHouseholds() {
        
        User user = new User();
        user.setHouseholdMemberships(Collections.emptyList());

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                preparednessService.getPreparednessStatusByUserId(1)
        );

        assertEquals("No households found for user", exception.getMessage());
    }

    @Test
    @DisplayName("Should return 0 days when household has no members")
    void testNoMembersReturnsZeroDays() {
        Household emptyHousehold = new Household();
        emptyHousehold.setMembers(Collections.emptyList());
        emptyHousehold.setInventory(Collections.emptyList());

        MyHouseholdStatusGetResponse myHouseholdStatusGetResponse = preparednessService.calculatePreparednessStatus(emptyHousehold);

        assertEquals(0, myHouseholdStatusGetResponse.getStatus().getDaysOfFood());
        assertEquals(0, myHouseholdStatusGetResponse.getStatus().getDaysOfWater());
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

        MyHouseholdStatusGetResponse myHouseholdStatusGetResponse = preparednessService.calculatePreparednessStatus(household);

        assertEquals(1, myHouseholdStatusGetResponse.getStatus().getDaysOfFood(), 0.01);
        assertEquals(2, myHouseholdStatusGetResponse.getStatus().getDaysOfWater(), 0.01); // 6L / (1*3)
    }




}

