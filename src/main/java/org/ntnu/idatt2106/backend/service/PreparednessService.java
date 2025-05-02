package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
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
 * @since 0.1
 * @version 0.2
 */
@Service
public class PreparednessService {

    @Autowired
    private HouseholdRepo householdRepo;

    @Autowired
    private UserRepo userRepo;

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
            return new PreparednessStatus(0, 0);
        }
        // Beregn total mengde vann og kalorier
        double totalWater = 0;
        double totalKcal = 0;

        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        for (Item item : household.getInventory()) {
            if (item.getExpirationDate().before(today)) continue;

            Category category = item.getCategory();
            double amount = item.getAmount();
            String unit = item.getUnit().getName().toLowerCase();
            String catName = category.getName().toLowerCase();

            // Beregn vann
            if ((catName.equals("vann") || catName.equals("water")) && unit.equals("l")) {
                totalWater += amount;
                continue;
            }

            // Beregn kalorier (hvis tilgjengelig)
            if (category.getKcalPerUnit() != null) {
                totalKcal += amount * category.getKcalPerUnit();
            }
        }

        // Antall dager med mat og vann
        double kcalPerPersonPerDay = 2000;
        double waterPerPersonPerDay = 3;

        double daysOfFood = totalKcal / (numPeople * kcalPerPersonPerDay);
        double daysOfWater = totalWater / (numPeople * waterPerPersonPerDay);

        return new PreparednessStatus(daysOfFood, daysOfWater);
    }

    /**
     * Retrieves the preparedness status for a given household by its ID.
     * <p>
     * This method looks up the household in the database and calculates its
     * preparedness status based on its inventory and number of members.
     * </p>
     *
     * @param userId The ID of the user whose household's preparedness status is to be retrieved.
     * @return A {@link PreparednessStatus} object representing the household's preparedness level.
     * @throws NoSuchElementException if user with given userId is not found.
     * @throws NoSuchElementException if no households are found for the user.
     */
    public List<PreparednessStatus> getPreparednessStatusByUserId(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Household> households = user.getHouseholdMemberships()
                .stream()
                .map(HouseholdMembers::getHousehold)
                .toList();
        if (households.isEmpty()) {
            throw new NoSuchElementException("No households found for user");
        }

        return households.stream()
                .map(this::calculatePreparednessStatus)
                .toList();
    }
}
