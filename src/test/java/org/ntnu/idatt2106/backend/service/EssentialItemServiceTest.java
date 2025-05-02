package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.household.EssentialItemStatusDTO;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EssentialItemServiceTest {

    @Mock
    private HouseholdRepo householdRepo;

    @InjectMocks
    private EssentialItemService essentialItemService;

    private Household household;

    @Mock
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        household = new Household();
    }

    @Test
    @DisplayName("Should return item present when total amount is enough")
    void testItemPresent() {
        household.setMembers(Arrays.asList(new HouseholdMembers(), new HouseholdMembers())); // 2 members

        Item item = new Item();
        item.setName("Pledd");
        item.setAmount(2);  // Enough for 2 people
        household.setInventory(List.of(item));

        when(householdRepo.findById(1)).thenReturn(Optional.of(household));

        List<EssentialItemStatusDTO> result = essentialItemService.getEssentialItemStatus(1);

        Optional<EssentialItemStatusDTO> found = result.stream().filter(dto -> dto.getName().equals("pledd")).findFirst();
        assertTrue(found.isPresent());
        assertTrue(found.get().isPresent());
    }

    @Test
    @DisplayName("Should return item not present when amount is not enough per person")
    void testItemNotPresentPerPerson() {
        household.setMembers(Arrays.asList(new HouseholdMembers(), new HouseholdMembers(), new HouseholdMembers())); // 3 members

        Item item = new Item();
        item.setName("Varme klær");
        item.setAmount(2);  // Not enough for 3 people
        household.setInventory(List.of(item));

        when(householdRepo.findById(1)).thenReturn(Optional.of(household));

        List<EssentialItemStatusDTO> result = essentialItemService.getEssentialItemStatus(1);

        Optional<EssentialItemStatusDTO> found = result.stream().filter(dto -> dto.getName().equals("varme klær")).findFirst();
        assertTrue(found.isPresent());
        assertFalse(found.get().isPresent());
    }

    @Test
    @DisplayName("Should return item present for non-per-person item if any amount exists")
    void testItemPresentNonPerPerson() {
        household.setMembers(Collections.singletonList(new HouseholdMembers()));

        Item item = new Item();
        item.setName("Grill");
        item.setAmount(1);  // Just one grill needed
        household.setInventory(List.of(item));

        when(householdRepo.findById(1)).thenReturn(Optional.of(household));

        List<EssentialItemStatusDTO> result = essentialItemService.getEssentialItemStatus(1);

        Optional<EssentialItemStatusDTO> found = result.stream().filter(dto -> dto.getName().equals("grill")).findFirst();
        assertTrue(found.isPresent());
        assertTrue(found.get().isPresent());
    }

    @Test
    @DisplayName("Should throw exception when household not found")
    void testHouseholdNotFound() {
        when(householdRepo.findById(42)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            essentialItemService.getEssentialItemStatus(42);
        });
    }

    @Test
    @DisplayName("Should throw when user not found")
    void testGetEssentialItemsByUserId_userNotFound() {
        when(userRepo.findById(404)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                essentialItemService.getEssentialItemStatusByUserId(404)
        );

        verify(userRepo).findById(404);
    }

    @Test
    @DisplayName("Should throw when user has no households")
    void testGetEssentialItemsByUserId_noHouseholds() {
        User user = new User();
        user.setHouseholdMemberships(Collections.emptyList());

        when(userRepo.findById(7)).thenReturn(Optional.of(user));

        assertThrows(NoSuchElementException.class, () ->
                essentialItemService.getEssentialItemStatusByUserId(7)
        );

        verify(userRepo).findById(7);
    }

    @Test
    @DisplayName("Should return essential items for all households of a user")
    void testGetEssentialItemStatusByUserIdSuccess() {
        // Arrange
        int userId = 1;
        int householdId = 10;

        // Setup user with one household membership
        User user = new User();
        Household household = new Household();
        household.setId(householdId);
        household.setMembers(Collections.singletonList(new HouseholdMembers())); // Important to prevent NPE

        HouseholdMembers membership = new HouseholdMembers();
        membership.setHousehold(household);
        user.setHouseholdMemberships(List.of(membership));

        // Add essential item
        Item item = new Item();
        item.setName("Grill");
        item.setAmount(1);
        household.setInventory(List.of(item));

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(householdRepo.findById(householdId)).thenReturn(Optional.of(household));

        // Act
        List<List<EssentialItemStatusDTO>> result = essentialItemService.getEssentialItemStatusByUserId(userId);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).stream().anyMatch(dto -> dto.getName().equals("grill") && dto.isPresent()));

        verify(userRepo).findById(userId);
        verify(householdRepo).findById(householdId);
    }



}
