package org.ntnu.idatt2106.backend.service;

import java.util.Optional;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.models.Admin;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling admin-related operations.
 */
@Service
public class AdminService {

  @Autowired
  private AdminRepo adminRepo;

  @Autowired
  private JWT_token jwt;

  private final BCryptHasher hasher = new BCryptHasher();


  /**
   * Validates that a password is not empty.
   *
   * @param password The password to validate.
   * @return true if the password is not empty, false otherwise.
   */
  public boolean validatePassword(String password) {
    return !password.isEmpty();
  }


  /**
   * Validates that the provided name contains only valid name characters.
   *
   * @param name The name to validate.
   * @return true if valid, false otherwise.
   */
  public boolean validateName(String name) {
    return name.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");
  }

  /**
   * Checks if the provided username is not already registered.
   *
   * @param username The username to check.
   * @return true if the username is available, false otherwise.
   */
  public boolean verifyUsernameNotInUse(String username) {
    return !adminRepo.existsByUsername(username);
  }

  /**
   * Validates the admin user credentials.
   *
   * @param username The username to validate.
   * @param password The password to validate.
   * @return true if the credentials are valid, false otherwise.
   */
  public boolean validateAdminUser(String username, String password) {
    return validateName(username) && verifyUsernameNotInUse(username) && validatePassword(password);
  }

  /**
   * Gets admin user by token
   * @param token The token to validate and extract admin from
   * @throws IllegalArgumentException if the token is invalid or the admin is not found
   */
  private Admin getAdminUserByToken(String token) {
    return jwt.getAdminUserByToken(token);
  }

  /**
   * Verifies if the admin is a superuser.
   *
   * @param token The token to validate and check if the admin is a superuser.
   * @throws IllegalArgumentException if the token is invalid or the admin is not a superuser
   */
  public void verifyAdminIsSuperUser(String token) {
    Admin admin = getAdminUserByToken(token);
    if (!admin.isSuperUser()) {
      throw new IllegalArgumentException("Admin is not a super user");
    }
  }

  /**
   * Authenticates an admin user using the provided username and password.
   *
   * @param username The admin's username.
   * @param password The admin's password.
   * @return A token response object on successful authentication.
   */
  public String authenticate(String username, String password) {
    Optional<Admin> admin = adminRepo.findByUsername(username);
    if (admin.isEmpty()) {
      throw new IllegalArgumentException("No admin found with given username and password");
    }
    if (!hasher.checkPassword(password, admin.get().getPassword())) {
      throw new IllegalArgumentException("Incorrect password for given username");
    }

    return jwt.generateJwtToken(admin.get()).getToken();
  }

  /**
   * Registers a new admin user if the username is not in use and the data is valid.
   * Registering a new admin requires superuser privileges, and will throw an exception if
   * the admin is not a superuser.
   *
   * @param admin The admin to register.
   * @throws IllegalArgumentException if admin data is invalid or username is already in use.
   * @throws UnauthorizedException if the admin is not authorized to register a new admin.
   */
  public void register(Admin admin, String token) {
    try {
      verifyAdminIsSuperUser(token);
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to register a new admin");
    }
    if (!verifyUsernameNotInUse(admin.getUsername())) {
      throw new IllegalArgumentException("Username is already in use");
    }
    if (!validateAdminUser(admin.getUsername(), admin.getPassword())) {
      throw new IllegalArgumentException("Invalid admin data");
    }
    admin.setPassword(hasher.hashPassword(admin.getPassword()));
    adminRepo.save(admin);
  }

  /**
   * Registers a new admin user with the provided username and password.
   *
   * @param username The admin's username.
   * @param password The admin's password.
   * @param token The token of the admin registering the new admin.
   * @throws IllegalArgumentException if the admin data is invalid or the username is already in use.
   */
  public void register(String username, String password, String token) {
    Admin admin = new Admin(username, password, false);
    register(admin, token);
  }

  /**
   * Elevates an admin user to a superuser.
   *
   * @param id The ID of the admin to elevate.
   * @param authorizationHeader The authorization header containing the token of the admin performing the action.
   * @throws IllegalArgumentException if the admin is not found or is already a superuser.
   * @throws UnauthorizedException if the admin is not authorized to elevate another admin.
   */
  public void elevateAdmin(String id, String authorizationHeader) {
    try {
      verifyAdminIsSuperUser(authorizationHeader);
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to elevate an admin");
    }
    Optional<Admin> admin = adminRepo.findById(Integer.parseInt(id));
    if (admin.isEmpty()) {
      throw new IllegalArgumentException("No admin found with given id");
    }
    Admin adminUser = admin.get();
    if (adminUser.isSuperUser()) {
      throw new IllegalArgumentException("Admin is already a super user");
    }
    adminUser.setSuperUser(true);
    adminRepo.save(adminUser);
  }

  /**
   * Deletes an admin user.
   *
   * @param id The ID of the admin to delete.
   * @param authorizationHeader The authorization header containing the token of the admin performing the action.
   * @throws IllegalArgumentException if the admin is not found or is a superuser.
   * @throws UnauthorizedException if the admin is not authorized to delete another admin.
   */
  public void exterminateAdmin(String id, String authorizationHeader) {
    try {
      verifyAdminIsSuperUser(authorizationHeader);
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to delete an admin");
    }
    Optional<Admin> admin = adminRepo.findById(Integer.parseInt(id));
    if (admin.isEmpty()) {
      throw new IllegalArgumentException("No admin found with given id");
    }
    Admin adminUser = admin.get();
    if (adminUser.isSuperUser()) {
      throw new IllegalArgumentException("Cannot delete a super user");
    }
    adminRepo.delete(adminUser);
  }
}
