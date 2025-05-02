package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.household.EssentialItemStatusDTO;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for checking the presence of essential items in a household inventory.
 *
 * @author Erlend Eide Zindel
 * @version 0.2
 * @since 0.2
 */
@Service
public class EssentialItemService {

    @Autowired
    private HouseholdRepo householdRepo;

    // Alle essensielle items (alle navn må være lowercase!)
    private static final List<String> ESSENTIAL_ITEMS = List.of(
            "grill", "kokeapparat", "stormkjøkken", "gassbeholder", "brennstoff",
            "varme klær", "pledd", "dyne", "sovepose", "fyrstikker", "stearinlys", "ved",
            "gassovn", "parafinovn", "lommelykt", "hodelykt",
            "dab-radio", "batterier", "batteribank",
            "førstehjelp", "jodtabletter", "legemidler", "våtservietter", "håndsprit",
            "bleier", "toalettpapir", "bind", "tamponger", "camping stove", "storm kitchen",
            "gas canister", "fuel", "warm clothes", "blanket", "duvet", "sleeping bag",
            "matches", "candles", "firewood", "gas heater", "paraffin heater", "flashlight",
            "headlamp", "dab radio", "batteries", "power bank", "first aid", "iodine tablets",
            "medication", "wet wipes", "hand sanitizer", "diapers", "toilet paper", "sanitary pads", "tampons"
    );

    // Items som krever at man har minst én per person
    private static final Set<String> PER_PERSON_ITEMS = Set.of(
            "varme klær", "pledd", "dyne", "sovepose",
            "warm clothes", "blanket", "duvet", "sleeping bag"
    );

    /**
     * Checks which essential items are present in the household's inventory.
     *
     * @param householdId ID of the household.
     * @return List of essential items and whether each is present.
     */
    public List<EssentialItemStatusDTO> getEssentialItemStatus(int householdId) {
        Household household = householdRepo.findById(householdId)
                .orElseThrow(() -> new NoSuchElementException("Household not found"));

        int numPeople = household.getMembers().size();

        // Lag en liste med alle item-navn og summer mengdene (lowercase)
        Map<String, Double> inventoryMap = new HashMap<>();
        for (Item item : household.getInventory()) {
            String name = item.getName().toLowerCase();
            inventoryMap.merge(name, item.getAmount(), Double::sum);
        }

        List<EssentialItemStatusDTO> statusList = new ArrayList<>();
        for (String essential : ESSENTIAL_ITEMS) {
            boolean isPerPerson = PER_PERSON_ITEMS.contains(essential);

            // Summer mengde for dette itemet (inkludert varianter som "stor dyne", "grå sovepose")
            double totalAmount = inventoryMap.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(essential))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            boolean present = isPerPerson
                    ? totalAmount >= numPeople
                    : totalAmount > 0;

            statusList.add(new EssentialItemStatusDTO(essential, present));
        }

        return statusList;
    }
}