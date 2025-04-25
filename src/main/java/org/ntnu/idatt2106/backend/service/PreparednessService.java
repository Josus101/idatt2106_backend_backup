package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Service class responsible for calculating the preparedness level of a household.
 * This includes assessing food, water, and essential item availability relative to household size.
 *
 * @author Erlend
 * @since 1.0
 */
@Service
public class PreparednessService {

    @Autowired
    private HouseholdRepo householdRepo;

    /**
     * Calculates the preparedness status of a given household.
     *
     * @param household The household to evaluate.
     * @return A {@link PreparednessStatus} object containing the preparedness percentage,
     * warning flag, and message.
     */
    public PreparednessStatus calculatePreparednessStatus(Household household) {
        int numPeople = household.getMembers().size();
        if (numPeople == 0) {
            return new PreparednessStatus(0, true, "Ingen medlemmer i husstanden");
        }

        // Krav for 3 og 7 dager
        double kcal3 = numPeople * 3 * 2000;
        double kcal7 = numPeople * 7 * 2000;
        double water3 = numPeople * 3 * 3;
        double water7 = numPeople * 7 * 3;

        double totalKcal = 0;
        double totalWater = 0;
        Set<String> essentials = new HashSet<>();

        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        for (Item item : household.getInventory()) {
            if (item.getExpirationDate().before(today)) continue;

            Category category = item.getCategory();
            double amount = item.getAmount();
            String unit = item.getUnit().getName().toLowerCase();

            if (category.getName().equalsIgnoreCase("vann") && unit.equals("l")) {
                totalWater += amount;
                continue;
            }

            if (category.getKcalPerUnit() != null) {
                totalKcal += amount * category.getKcalPerUnit();
            }

            if (Boolean.TRUE.equals(category.getIsEssential())) {
                essentials.add(category.getName());
            }
        }

        boolean hasEssentials = essentials.containsAll(List.of("Førstehjelp", "Gassbrenner"));

        // Beregn dekning i prosent (mot 7-dagers krav)
        double kcalPercent = (totalKcal / kcal7) * 100;
        double waterPercent = (totalWater / water7) * 100;

        // Samlet dekning (laveste verdi styrer)
        double preparednessPercent = Math.min(100.0, Math.min(kcalPercent, waterPercent));

        // Vurder nivå
        boolean below3days = totalKcal < kcal3 || totalWater < water3;
        boolean below7days = totalKcal < kcal7 || totalWater < water7;

        boolean warning = below3days || !hasEssentials;

        String message;
        if (below3days) {
            message = "Lageret dekker ikke 3 dager med mat og vann";
        } else if (below7days) {
            message = "Lageret dekker 3 dager, men ikke 7";
        } else {
            message = "Lageret dekker minst 7 dager";
        }

        return new PreparednessStatus((int) preparednessPercent, warning, message);
    }

    /**
     * Retrieves the preparedness status for a given household by its ID.
     * <p>
     * This method looks up the household in the database and calculates its
     * preparedness status based on its inventory and number of members.
     * </p>
     *
     * @param householdId The ID of the household to evaluate.
     * @return A {@link PreparednessStatus} object representing the household's preparedness level.
     * @throws NoSuchElementException if no household is found with the given ID.
     */
    public PreparednessStatus getPreparednessStatusByHouseholdId(int householdId) {
        Household household = householdRepo.findById(householdId)
                .orElseThrow(() -> new NoSuchElementException("Household not found"));

        return calculatePreparednessStatus(household);
    }
}
