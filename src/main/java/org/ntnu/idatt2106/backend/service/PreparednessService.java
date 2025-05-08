package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.household.MyHouseholdStatusGetResponse;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.HouseholdMembersRepo;
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

    @Autowired
    private HouseholdMembersRepo householdMembersRepo;

    /**
     * Calculates the preparedness status of a given household.
     *
     * @param household The household to evaluate.
     * @return A {@link PreparednessStatus} object containing the preparedness percentage,
     * warning flag, and message.
     */
    public MyHouseholdStatusGetResponse calculatePreparednessStatus(Household household) {
        int numPeople = household.getMembers().size();
        if (numPeople == 0) {
            return new MyHouseholdStatusGetResponse(household.getId(), household.getName(), new PreparednessStatus(0, 0));
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
        double kcalPerAdultPerDay = 2000;
        double waterPerAdultPerDay = 3;

        double kcalPerChildPerDay = 1400;
        double waterPerChildPerDay = 1.5;

        double kcalPerPetPerDay = 500;
        double waterPerPetPerDay = 1.0;

        double kcalPerDay = ((numPeople + household.getUnregisteredAdultCount()) * kcalPerAdultPerDay +
            household.getUnregisteredChildCount() * kcalPerChildPerDay +
            household.getUnregisteredPetCount() * kcalPerPetPerDay);
        double waterPerDay = ((numPeople + household.getUnregisteredAdultCount()) * waterPerAdultPerDay +
            household.getUnregisteredChildCount() * waterPerChildPerDay +
            household.getUnregisteredPetCount() * waterPerPetPerDay);

        double daysOfFood = totalKcal / (kcalPerDay);
        double daysOfWater = totalWater / (waterPerDay);

        return new MyHouseholdStatusGetResponse(household.getId(), household.getName(), new PreparednessStatus(daysOfFood, daysOfWater));
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
    public List<MyHouseholdStatusGetResponse> getPreparednessStatusByUserId(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<HouseholdMembers> memberships = user.getHouseholdMemberships();

        if (memberships.isEmpty()) {
            throw new NoSuchElementException("No households found for user");
        }

        List<MyHouseholdStatusGetResponse> householdStatuses = memberships.stream()
                .map(HouseholdMembers::getHousehold)
                .map(this::calculatePreparednessStatus)
                .toList();

        Household primaryHousehold = user.getPrimaryHousehold();

        if (primaryHousehold == null) return householdStatuses;
        return householdStatuses.stream()
                .sorted((a, b) -> {
                  boolean aIsPrimary = a.getId() == primaryHousehold.getId();
                    boolean bIsPrimary = b.getId() == primaryHousehold.getId();
                    return Boolean.compare(!aIsPrimary, !bIsPrimary);
                })
                .toList();
    }
}
