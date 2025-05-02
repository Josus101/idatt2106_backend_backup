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
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EssentialItemServiceTest {

    @Mock
    private HouseholdRepo householdRepo;

    @InjectMocks
    private EssentialItemService essentialItemService;

    private Household household;

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
}
