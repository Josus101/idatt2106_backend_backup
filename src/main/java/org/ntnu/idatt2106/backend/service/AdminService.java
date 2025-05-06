package org.ntnu.idatt2106.backend.service;

import java.util.List;
import java.util.Optional;

import org.ntnu.idatt2106.backend.dto.admin.AdminGetResponse;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;

/**
 * Service class for handling admin-related operations.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class AdminService {

  @Autowired
  private AdminRepo adminRepo;

  @Autowired
  private JWT_token jwt;

  @Autowired
  private EmailService emailService;

  private final BCryptHasher hasher = new BCryptHasher();


  /**
   * Validates that a password is not empty.
   *
   * @param password The password to validate.
   * @return true if the password is not empty, false otherwise.
   */
  public boolean validatePassword(String password) {
    return password.length() >= 8 && password.length() <= 50;
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
   * Gets admin user by token.
   *
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
      throw new UnauthorizedException("Admin is not a super user");
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
      throw new UserNotFoundException("No admin found with given username and password");
    }
    if (!admin.get().isActive()) {
      throw new UserNotVerifiedException("Admin is not active");
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
    if (!validateName(admin.getUsername()) || !verifyUsernameNotInUse(admin.getUsername())) {
      throw new IllegalArgumentException("Invalid admin data");
    }
    admin.setPassword(hasher.hashPassword(admin.getPassword()));
    adminRepo.save(admin);
  }

  /**
   * Registers a new admin user with the provided username and password.
   *
   * @param username The admin's username.
   * @param token The token of the admin registering the new admin.
   * @throws IllegalArgumentException if admin data is invalid or username is already in use.
   */
  public void register(String username, String email, String token) {
    Admin admin = new Admin(username, "", email, false);
    register(admin, token);
  }

  /**
   * Elevates an admin user to a superuser.
   *
   * @param id The ID of the admin to elevate.
   * @param authorizationHeader The authorization header containing the
   *                            token of the admin performing the action.
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
   * @param authorizationHeader The authorization header containing the
   *                            token of the admin performing the action.
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

  /**
   * Retrieves all admin users.
   *
   * @param authorizationHeader The authorization header containing the bearer token.
   * @return A list of all admin users.
   */
  public List<AdminGetResponse> getAllAdmins(String authorizationHeader) {
    try {
      verifyAdminIsSuperUser(authorizationHeader);
      List<AdminGetResponse> admins = adminRepo.findAll().stream().map(admin -> new AdminGetResponse(
          admin.getId(),
          admin.getUsername(),
          admin.isSuperUser()
      )).toList();
      if (admins.isEmpty()) {
        throw new UserNotFoundException("No admins found");
      }
      return admins;
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to get all admins");
    }
  }

  /**
   * Changes password for the admin user.
   *
   * @param admin The admin user.
   * @param newPassword The new password.
   * @throws IllegalArgumentException if the password is invalid.
   *
   */
  public void changePassword(Admin admin, String newPassword) {
    if (!validatePassword(newPassword)) {
      throw new IllegalArgumentException("Invalid password");
    }
    admin.setPassword(hasher.hashPassword(newPassword));
    if (!admin.isActive()) {
      admin.setActive(true);
    }
    adminRepo.save(admin);
  }

  /**
   * Send email to activate the admin user.
   *
   * @param admin The admin user.
   * @throws IllegalArgumentException if the admin is already active.
   * @throws MailSendingFailedException if the email sending fails.
   */
  public void sendActivateEmail(Admin admin) {
    try {
      if (admin.isActive()) {
        throw new IllegalArgumentException("Admin is already active");
      }
      emailService.sendAdminActivationEmail(admin);
    } catch (Exception e) {
      throw new MailSendingFailedException("Failed to send activation email", e.getCause());
    }

  }

  /**
   * Activates the admin user.
   *
   * @param admin The admin user.
   * @param newPassword The new password.
   * @throws IllegalArgumentException if the token is invalid or the admin is already active.
   */
  public void activateAdmin(Admin admin, String newPassword) {
    if (admin.isActive()) {
      throw new IllegalArgumentException("Admin is already active");
    }
    admin.setActive(true);
    adminRepo.save(admin);
  }
}
