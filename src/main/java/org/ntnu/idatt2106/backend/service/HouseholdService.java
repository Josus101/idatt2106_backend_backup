package org.ntnu.idatt2106.backend.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdJoinCode;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.HouseholdJoinCodeRepo;
import org.ntnu.idatt2106.backend.repo.HouseholdMembersRepo;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing households.
 * This class contains methods for creating, updating, and deleting households,
 * as well as managing household members and join codes.
 *
 * @Author Konrad Seime
 * @since 0.2
 */
@Service
public class HouseholdService {

  @Autowired
  HouseholdJoinCodeRepo householdJoinCodeRepo;

  @Autowired
  HouseholdRepo householdRepo;

  @Autowired
  HouseholdMembersRepo householdMembersRepo;

  @Autowired
  UserRepo userRepo;

  private static final long EXPIRATION_TIME = 24 * 60 * 60 * 100; // 24 hours
  private static final int JOIN_CODE_LENGTH = 8;
  private static final int MAX_TRIES = 1000;
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();


  /**
   * Verifies that the token is not taken
   *
   * @param token The join code token to verify.
   * @return true if the token is not taken, false otherwise.
   */
  private boolean verifyTokenNotTaken(String token) {
    return !householdJoinCodeRepo.existsByCode(token);
  }

  /**
   * Verifies if a join code is valid and not expired.
   *
   * @param token The join code token to verify.
   * @return true if the token is valid and not expired, false otherwise.
   */
  private boolean verifyTokenNotExpired(HouseholdJoinCode token) {
    return token.getExpirationDate().after(new Date());
  }

  /**
   * Verifies if a join code is valid and not expired.
   *
   * @param token The join code token to verify.
   * @return true if the token is valid and not expired, false otherwise.
   */
  private boolean verifyTokenNotExpired(String token) {
    return householdJoinCodeRepo.findByCode(token)
        .map(this::verifyTokenNotExpired)
        .orElse(false);
  }

  /**
   * Gets a household by its join code. Returns null if the code is invalid or expired.
   *
   * @param code The join code for the household.
   * @return The household that was found or null if tokan invalid
   */
  private Household getHouseholdByCode(String code) {
    if (code == null || code.isEmpty() || !verifyTokenNotExpired(code)) {
      return null;
    }
    return householdJoinCodeRepo.findByCode(code)
        .map(HouseholdJoinCode::getHousehold)
        .orElse(null);
  }

  /**
   * Adds a user to a household.
   *
   * @param household The household to which the user will be added.
   * @param user The user to be added.
   */
  private void addUserToHousehold(Household household, User user) {
    HouseholdMembers householdMembers = new HouseholdMembers(user, household,false);
    householdMembersRepo.save(householdMembers);
    user.getHouseholdMemberships().add(householdMembers);
    household.getMembers().add(householdMembers);
    userRepo.save(user);
    householdRepo.save(household);
    System.out.println(user + " added to household " + household.getName());
  }

  /**
   * Removes a user from a household.
   *
   * @param household The household from which the user will be removed.
   * @param user The user to be removed.
   */
  private void removeUserFromHousehold(Household household, User user) {
    Optional<HouseholdMembers> householdMembers = householdMembersRepo.findByUserAndHousehold(user, household);
    if (householdMembers.isPresent()) {
      householdMembersRepo.delete(householdMembers.get());
      System.out.println(user + " removed from household " + household.getName());
    } else {
      System.out.println(user + " not found in household " + household.getName());
    }

  }

  /**
   * Creates a new household.
   *
   * @param name The name of the household.
   * @param latitude The latitude of the household's location.
   * @param longitude The longitude of the household's location.
   * @return The created household.
   */
  public Household createHousehold(String name, double latitude, double longitude) {
    Household household = new Household();
    household.setName(name);
    household.setLatitude(latitude);
    household.setLongitude(longitude);
    return householdRepo.save(household);
  }

  /**
   * Generates a random join code for a household.
   * The code consists of x characters, which can be uppercase letters and digits.
   *
   * @param length The length of the join code.
   * @return A random join code.
   */
  private String generateRandomCode(int length) {
    String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder code = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = RANDOM.nextInt(CHARACTERS.length());
      code.append(CHARACTERS.charAt(index));
    }
    return code.toString();
  }

  /**
   * Generates a join code for a household.
   * The join code is valid for 24 hours.
   *
   * @param household The household for which the join code will be generated.
   * @return The generated join code.
   */
  public String generateJoinCode(Household household) {
    String code = null;
    for (int i = 0; i < MAX_TRIES; i++) {
      String candidate = generateRandomCode(JOIN_CODE_LENGTH);
      if (verifyTokenNotTaken(candidate)) {
        code = candidate;
        break;
      }
    }
    if (code == null) {
      throw new RuntimeException("Could not generate a unique join code after " + MAX_TRIES + " tries.");
    }
    HouseholdJoinCode joinCode = new HouseholdJoinCode(code, household,
        new Date(System.currentTimeMillis() + EXPIRATION_TIME));
    householdJoinCodeRepo.save(joinCode);
    return code;
  }


  /**
   * Joins a household using a join code.
   *
   * @param code The join code for the household.
   * @param user The user who wants to join the household.
   * @return The household that was joined, or null if the join code is invalid or expired.
   */
  public Household joinHousehold(String code, User user) {
    if (code == null || code.isEmpty() || !verifyTokenNotExpired(code)) {
      return null;
    }
    Household household = getHouseholdByCode(code);
    if (household != null) {
      addUserToHousehold(household, user);
    }
    return household;
  }
}
