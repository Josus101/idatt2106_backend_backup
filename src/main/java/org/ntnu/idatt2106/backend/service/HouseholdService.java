package org.ntnu.idatt2106.backend.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.ntnu.idatt2106.backend.dto.household.HouseholdCreate;
import org.ntnu.idatt2106.backend.dto.household.HouseholdRequest;
import org.ntnu.idatt2106.backend.dto.user.UserPositionResponse;
import org.ntnu.idatt2106.backend.exceptions.JoinCodeException;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdJoinCode;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.Item;
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
  HouseholdJoinCodeRepo joinCodeRepo;

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
    return !joinCodeRepo.existsByCode(token);
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
    return joinCodeRepo.findByCode(token)
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
    return joinCodeRepo.findByCode(code)
        .map(HouseholdJoinCode::getHousehold)
        .orElse(null);
  }

  /**
   * Adds a user to a household.
   *
   * @param household The household to which the user will be added.
   * @param user The user to be added.
   */
  public void addUserToHousehold(Household household, User user) {
    if (householdMembersRepo.existsByUserAndHousehold(user, household)) {
      System.out.println(user + " already in household " + household.getName());
      return;
    }
    HouseholdMembers householdMembers = new HouseholdMembers(user, household,false);
    householdMembersRepo.save(householdMembers);
    user.getHouseholdMemberships().add(householdMembers);
    household.getMembers().add(householdMembers);  }

  /**
   * Adds a user to a household.
   *
   * @param household The household to which the user will be added.
   * @param user The user to be added.
   */
  public void addUserToHousehold(Household household, User user, boolean isAdmin) {
    if (householdMembersRepo.existsByUserAndHousehold(user, household)) {
      System.out.println(user + " already in household " + household.getName());
      return;
    }
    HouseholdMembers householdMembers = new HouseholdMembers(user, household,isAdmin);
    householdMembersRepo.save(householdMembers);
    user.getHouseholdMemberships().add(householdMembers);
    household.getMembers().add(householdMembers);  }

  /**
   * Verifies that a user is in the household.
   *
   * @param household The household to check.
   * @param user The user to check.
   * @throws NoSuchElementException if the user is not found in the household.
   */
  private void verifyUserInHousehold(Household household, User user) {
    if (!householdMembersRepo.existsByUserAndHousehold(user, household)) {
      throw new NoSuchElementException("User not found in household");
    }
  }

  /**
   * Removes a user from a household.
   *
   * @param household The household from which the user will be removed.
   * @param user The user to be removed.
   * @throws NoSuchElementException if the user is not found in the household.
   */
  private void removeUserFromHousehold(Household household, User user) {
    Optional<HouseholdMembers> householdMembers = householdMembersRepo.findByUserAndHousehold(user, household);
    if (householdMembers.isEmpty()) {
      System.out.println(user + " not found in household " + household.getName());
      return;
    }
    householdMembersRepo.delete(householdMembers.get());
    user.getHouseholdMemberships().remove(householdMembers.get());
    household.getMembers().remove(householdMembers.get());
    householdRepo.save(household);
    System.out.println(user + " removed from household " + household.getName());

  }

  /**
   * Lets a user leave a household.
   *
   * @param household The household from which the user will leave.
   * @param user The user who is leaving the household.
   * @throws NoSuchElementException if the user is not found in the household.
   * @throws IllegalArgumentException if the user is the admin of the household.
   */
  public void leaveHousehold(Household household, User user) {
    verifyUserInHousehold(household, user);
    Optional<HouseholdMembers> householdMember = householdMembersRepo.findByUserAndHousehold(user, household);
    if (householdMember.isEmpty()) {
      throw new NoSuchElementException("User not found in household");
    }
    if (householdMember.get().isAdmin()) {
      throw new IllegalArgumentException("User is the admin of the household. Cannot leave.");
    }
    removeUserFromHousehold(household, user);
  }

  /**
   * Lets a user leave a household.
   *
   * @param householdId The id household from which the user will leave.
   * @param user The user who is leaving the household.
   */
  public void leaveHousehold(int householdId, User user) {
    Optional<Household> household = householdRepo.findById(householdId);
    if (household.isPresent()) {
      leaveHousehold(household.get(), user);
    } else {
      throw new NoSuchElementException("Household not found");
    }
  }

  /**
   * Verifies if the user can be kicked from the household.
   *
   * @param household The household from which the user will be kicked.
   * @param user The user who is being kicked from the household.
   * @param admin The admin who is kicking the user.
   */
  private void verifyCanKickUserFromHousehold(Household household, User user, User admin) {
   verifyUserInHousehold(household, user);
    Optional<HouseholdMembers> adminMember = householdMembersRepo.findByUserAndHousehold(admin, household);
    if (adminMember.isEmpty() || !adminMember.get().isAdmin()) {
      throw new UnauthorizedException("Admin does not have authority to kick user");
    }
  }

  /**
   * Kicks a user from a household.
   * Is only possible if the user is an admin of the household.
   *
   * @param household The household from which the user will be kicked.
   * @param user The user who is being kicked from the household.
   * @param admin The admin who is kicking the user.
   * @throws NoSuchElementException if the user is not found in the household.
   * @throws UnauthorizedException if the admin does not have authority to kick the user.
   */
  public void kickUserFromHousehold(Household household, User user, User admin) {
    verifyCanKickUserFromHousehold(household, user, admin);
    removeUserFromHousehold(household, user);
  }

  /**
   * Kicks a user from a household.
   * Is only possible if the user is an admin of the household.
   *
   * @param householdId The id of the household from which the user will be kicked.
   * @param userId The id of the user who is being kicked from the household.
   * @param admin The admin who is kicking the user.
   * @throws NoSuchElementException if the household or user is not found.
   * @throws UnauthorizedException if the admin does not have authority to kick the user.
   */
  public void kickUserFromHousehold(int householdId, int userId, User admin) {
    Optional<Household> household = householdRepo.findById(householdId);
    Optional<User> user = userRepo.findById(userId);
    if (household.isEmpty() || user.isEmpty()) {
      throw new NoSuchElementException("Household or User not found");
    }
    kickUserFromHousehold(household.get(), user.get(), admin);
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
   * Creates a new household using the provided HouseholdCreate object.
   *
   * @param household The HouseholdCreate object containing the household's details.
   * @return The created household.
   */
  public Household createHousehold(HouseholdCreate household) {
    return createHousehold(household.getName(), household.getLatitude(), household.getLongitude());
  }

  /**
   * Generates a random join code for a household.
   * The code consists of x characters, which can be uppercase letters and digits.
   *
   * @param length The length of the join code.
   * @return A random join code.
   */
  private String generateRandomCode(int length) {
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
   * @param user The user who is generating the join code.
   * @return The generated join code.
   */
  public String generateJoinCode(Household household, User user) {
    if (household == null || user == null) {
      throw new IllegalArgumentException("Household and user cannot be null");
    }
    if (!householdMembersRepo.existsByUserAndHousehold(user, household)) {
      throw new UnauthorizedException("User is not a member of the household");
    }
    String code = null;
    for (int i = 0; i < MAX_TRIES; i++) {
      String candidate = generateRandomCode(JOIN_CODE_LENGTH);
      if (verifyTokenNotTaken(candidate)) {
        code = candidate;
        break;
      }
    }
    if (code == null) {
      throw new JoinCodeException("Could not generate a unique join code after " + MAX_TRIES + " tries.");
    }
    HouseholdJoinCode joinCode = new HouseholdJoinCode(code, household,
        new Date(System.currentTimeMillis() + EXPIRATION_TIME));
    joinCodeRepo.save(joinCode);
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


  /**
   * Gets households by user.
   *
   * @param user The user whose households are to be retrieved.
   * @return The households that the user is a member of.
   */
  public List<HouseholdRequest> getHouseholdsByUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    List<HouseholdMembers> householdMemberships = user.getHouseholdMemberships();

    List<HouseholdRequest> households = new ArrayList<>();
    for (HouseholdMembers householdMember : householdMemberships) {
      System.out.println("Household member: " + householdMember);
      Household household = householdMember.getHousehold();

      List<String> members = new ArrayList<>();
      List<String> inventory = new ArrayList<>();
      if (household.getMembers() != null) {
        for (HouseholdMembers member : household.getMembers()) {
          members.add(member.getUser().toString());
        }
      }
      if (household.getInventory() != null) {
        for (Item item : household.getInventory()) {
          inventory.add(item.toString());
        }
      }

      households.add(new HouseholdRequest(household.getId(), household.getName(), household.getLatitude(), household.getLongitude(), members, inventory));
    }
    return households;
  }

  /**
   * Gets all user positions in a household.
   *
   * @param household The household whose user positions are to be retrieved.
   * @param user The user whose positions are to be retrieved.
   * @return A list of user positions in the household.
   */
  public List<UserPositionResponse> getUserPositions(Household household, User user) {
    if (household == null || user == null) {
      throw new IllegalArgumentException("Household and user cannot be null");
    }
    if (!householdMembersRepo.existsByUserAndHousehold(user, household)) {
      throw new UnauthorizedException("User is not a member of the household");
    }
    List<HouseholdMembers> members = householdMembersRepo.findAllByHousehold(household);
    List<UserPositionResponse> userPositions = new ArrayList<>();
    for (HouseholdMembers member : members) {
      UserPositionResponse userPosition = new UserPositionResponse(
          member.getUser().getLatitude(),
          member.getUser().getLongitude(),
          member.getUser().getFormattedPositionUpdateTime(),
          member.getUser().getId(),
          member.getUser().toString()
      );
    }
    return userPositions;
  }

  /**
   * Gets all user positions in a household.
   *
   * @param householdId The id of the household whose user positions are to be retrieved.
   * @param user The user whose positions are to be retrieved.
   * @return A list of user positions in the household.
   */
  public List<UserPositionResponse> getUserPositions(int householdId, User user) {
    Optional<Household> foundHousehold = householdRepo.findById(householdId);
    if (foundHousehold.isEmpty()) {
      throw new NoSuchElementException("Household not found");
    }
    return getUserPositions(foundHousehold.get(), user);
  }

  /**
   * Gets position of all users in all households a user is a member of.
   *
   * @param user The user whose positions are to be retrieved.
   * @return A list of user positions in the households.
   */
  public List<UserPositionResponse> getUserPositions(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    List<HouseholdMembers> householdMemberships = user.getHouseholdMemberships();
    List<UserPositionResponse> userPositions = new ArrayList<>();
    for (HouseholdMembers householdMember : householdMemberships) {
      Household household = householdMember.getHousehold();
      List<HouseholdMembers> members = householdMembersRepo.findAllByHousehold(household);
      for (HouseholdMembers member : members) {
        UserPositionResponse userPosition = new UserPositionResponse(
            member.getUser().getLatitude(),
            member.getUser().getLongitude(),
            member.getUser().getFormattedPositionUpdateTime(),
            member.getUser().getId(),
            member.getUser().toString()
        );
        userPositions.add(userPosition);
      }
    }
    return userPositions;
  }
}
