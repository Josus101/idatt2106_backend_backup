package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.HouseholdMembersId;
import org.ntnu.idatt2106.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing household data in the database.
 */
public interface HouseholdMembersRepo extends JpaRepository<HouseholdMembers, Integer> {

    /**
     * Finds a HouseholdMember by its ID.
     *
     * @param id The ID of the HouseholdMember.
     * @return An Optional containing the HouseholdMember if found, or empty if not.
     */
    Optional<HouseholdMembers> findById(HouseholdMembersId id);

    /**
     * Finds a HouseholdMember by its user and household.
     *
     * @param user The user.
     * @param household The household.
     * @return An Optional containing the HouseholdMember if found, or empty if not.
     */
    Optional<HouseholdMembers> findByUserAndHousehold(User user, Household household);

    /**
     * Checks if a HouseholdMember exists by its user and household.
     *
     * @param user The user.
     * @param household The household.
     * @return true if the HouseholdMember exists, false otherwise.
     */
    boolean existsByUserAndHousehold(User user, Household household);
}