package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdJoinCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


/**
 * Repository interface for managing Join code entities.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
public interface HouseholdJoinCodeRepo extends JpaRepository<HouseholdJoinCode, Long> {

  /**
   * Finds join code tokens associated with a specific household ID.
   *
   * @param householdId the ID of the household
   */
  Optional<HouseholdJoinCode> findByHouseholdId(int householdId);

  /**
   * Finds a join code token by its code.
   *
   * @param code the join code
   * @return an Optional containing the found token, or empty if not found
   */
  Optional<HouseholdJoinCode> findByCode(String code);

  /**
   * Finds a join code token by its id.
   *
   * @param id the join code id
   */
  Optional<HouseholdJoinCode> findById(int id);

  /**
   * Deletes tokens associated with a specific household ID.
   *
   * @param household the ID of the household
   */
  @Modifying
  @Transactional
  void deleteAllByHousehold(Household household);

  /**
   * Checks if a join code token exists by its code.
   *
   * @param token the join code
   * @return true if the token exists, false otherwise
   */
  boolean existsByCode(String token);
}
