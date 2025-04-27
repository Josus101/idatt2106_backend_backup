package org.ntnu.idatt2106.backend.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * DataSeeder is a component that seeds the database with initial data.
 * It implements CommandLineRunner to run the seed method after the application starts.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@Component
public class DataSeeder implements CommandLineRunner {

  private final AdminRepo adminRepo;
  private final UserRepo userRepo;

  private final BCryptHasher hasher;

  private final LoginService loginService;

  /**
   * Constructor for DataSeeder.
   * @param adminRepo the AdminRepo to use for seeding admin data
   * @param userRepo the UserRepo to use for seeding user data
   * @param hasher the BCryptHasher to use for hashing passwords
   * @param loginService the LoginService to use for user login operations
   */
  public DataSeeder( AdminRepo adminRepo, UserRepo userRepo,
      BCryptHasher hasher, LoginService loginService) {
    this.userRepo = userRepo;
    this.adminRepo = adminRepo;
    this.hasher = hasher;
    this.loginService = loginService;
  }

  /**
   * This method is called after the application context is loaded.
   * It seeds the database with initial data if the database is empty.
   */
  @Override
  @Transactional
  public void run(String... args) throws Exception {
    if (userRepo.count() > 0) {
      System.out.println("Skipper seeding – brukere finnes allerede.");
      return;
    }

    seedCategories();
    seedUsers();
    seedAdminUsers();
    seedHouseholdWithMembers();
    seedItems();
    seedTypes();
    seedEmergencyService();
    System.out.println("Seeding complete.");
  }

  /**
   * This method seeds the database with initial categories.
   */
  private void seedCategories() {
    //TODO seed categories
  }

  /**
   * This method seeds the database with initial users.
   */
  private void seedUsers() {
    if (userRepo.findByEmail("test@example.com").isEmpty()) {
      User user = new User(
          "test@example.com",
          hasher.hashPassword("test123"),
          "Test",
          "Bruker",
          "12345678"
      );
      loginService.verifyEmail(user);
      userRepo.save(user);
    }
    if (userRepo.findByEmail("krekar@gmail.com").isEmpty()) {
      User user = new User(
          "krekar@gmail.com",
          hasher.hashPassword("test123"),
          "Krekar",
          "Design",
          "11111111"
      );
      loginService.verifyEmail(user);
      userRepo.save(user);
    }
    if (userRepo.findByEmail("kalle@gmail.com").isEmpty()) {
      User user = new User(
          "kalle@gmail.com",
          hasher.hashPassword("test123"),
          "Kalle",
          "Kontainer",
          "2222222"
      );
      loginService.verifyEmail(user);
      userRepo.save(user);
    }
    if (userRepo.findByEmail("kare@gmail.com").isEmpty()) {
      User user = new User(
          "kare@gmail.com",
          hasher.hashPassword("test123"),
          "Kåre",
          "Kakkelovn",
          "33333333"
      );
      loginService.verifyEmail(user);
      userRepo.save(user);
    }
    if (userRepo.findByEmail("Albert@example.com").isEmpty()){
      User user = new User(
          "Albert@example.com",
          hasher.hashPassword("password123"),
          "Albert",
          "Zindel",
          "98765432"
      );
      loginService.verifyEmail(user);
      userRepo.save(user);
    }

  }

  /**
   * This method seeds the database with initial admin users.
   */
  public void seedAdminUsers() {
    if (!adminRepo.existsByUsername("admin")) {
      Admin admin = new Admin(
          "admin",
          hasher.hashPassword("admin123"),
          false
      );
      adminRepo.save(admin);
    }
    if (!adminRepo.existsByUsername("urekmazino")) {
      Admin superAdmin = new Admin(
          "urekmazino",
          hasher.hashPassword("admin123"),
          true
      );
      adminRepo.save(superAdmin);
    }
  }

  public void seedHouseholdWithMembers() {
    //TODO seed household
  }

  public void seedItems() {
    //TODO seed items
  }

  public void seedTypes() {
    //TODO seed types
  }

  public void seedEmergencyService()
  {
    //TODO seed emergency service
  }
}



