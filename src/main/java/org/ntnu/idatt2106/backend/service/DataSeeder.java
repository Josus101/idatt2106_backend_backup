package org.ntnu.idatt2106.backend.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.*;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Calendar;
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
  private final CategoryRepo categoryRepo;
  private final EmergencyServiceRepo emergencyServiceRepo;
  private final HouseholdRepo householdRepo;
  private final ItemRepo itemRepo;
  private final TypeRepo typeRepo;
  private final UnitRepo unitRepo;
  private final HouseholdMembersRepo householdMembersRepo;
  private final BCryptHasher hasher;
  private final LoginService loginService;

  /**
   * Constructor for DataSeeder.
   *
   * @param adminRepo Admin repository
   * @param userRepo User repository
   * @param hasher Password hasher
   * @param loginService Login service
   * @param categoryRepo Category repository
   * @param emergencyServiceRepo Emergency service repository
   * @param householdRepo Household repository
   * @param itemRepo Item repository
   * @param typeRepo Type repository
   * @param unitRepo Unit repository
   * @param householdMembersRepo Household members repository
   */
  public DataSeeder(AdminRepo adminRepo, UserRepo userRepo,
      BCryptHasher hasher, LoginService loginService, CategoryRepo categoryRepo,
      EmergencyServiceRepo emergencyServiceRepo, HouseholdRepo householdRepo,
      ItemRepo itemRepo, TypeRepo typeRepo, UnitRepo unitRepo,
      HouseholdMembersRepo householdMembersRepo) {
    this.userRepo = userRepo;
    this.adminRepo = adminRepo;
    this.categoryRepo = categoryRepo;
    this.emergencyServiceRepo = emergencyServiceRepo;
    this.householdRepo = householdRepo;
    this.itemRepo = itemRepo;
    this.typeRepo = typeRepo;
    this.unitRepo = unitRepo;
    this.householdMembersRepo = householdMembersRepo;
    this.hasher = hasher;
    this.loginService = loginService;
  }

  /**
   * Seeds the database with initial data.
   * This method is called after the application starts.
   *
   * @param args Command line arguments
   * @throws Exception if an error occurs during seeding
   */
  @Override
  @Transactional
  public void run(String... args) throws Exception {
    if (userRepo.count() > 0) {
      System.out.println("Skipping seeding - users already exist.");
      return;
    }

    seedCategoriesAndUnits();
    seedAdminUsers();
    seedHouseholdOne();
    seedHouseholdTwo();
    seedHouseholdThree();
    seedHouseholdFour();
    seedEmergencyServicesWithTypes();
    System.out.println("\nSeeding complete.\nZ");
  }

  /**
   * Seeds the database with categories and units.
   * This method is called during the application startup.
   */
  public void seedCategoriesAndUnits() {
    if (categoryRepo.count() == 0) {
      List<Category> categories = List.of(
          new Category("Water", 0, true),
          new Category("Canned Food", 200, true),
          new Category("Dried Food", 350, true),
          new Category("Medical Supplies", 0, true),
          new Category("Snacks", 500, false),
          new Category("Beverages", 45, false),
          new Category("Sugar Free Monster", 11, false),
          new Category("Pet Food", 300, true),
          new Category("Baby Supplies", 100, true),
          new Category("Batteries", 0, true),
          new Category("Hygiene Products", 0, true),
          new Category("Cooking Fuel", 0, true),
          new Category("Fresh Vegetables", 35, false),
          new Category("Fresh Fruits", 52, false),
          new Category("Frozen Food", 250, true),
          new Category("Grains (Rice, Pasta)", 360, true),
          new Category("Kjøkkenutstyr", 0, true)

      );
      categoryRepo.saveAll(categories);
    }
    if (unitRepo.count() == 0) {
      List<Unit> units = List.of(
          new Unit("KG"),
          new Unit("L"),
          new Unit("Stk")
      );
      unitRepo.saveAll(units);
    }
  }

  /**
   * Seeds the database with admin users.
   * This method is called during the application startup.
   */
  public void seedAdminUsers() {
    if (!adminRepo.existsByUsername("admin")) {
      adminRepo.save(new Admin("admin", hasher.hashPassword("admin123"), "admin@krisefikser.no", false));
    }
    if (!adminRepo.existsByUsername("urekmazino")) {
      adminRepo.save(new Admin("urekmazino", hasher.hashPassword("admin123"), "urek@krisefikser.no", true));
    }
  }

  /**
   * Seeds the database with household data.
   * This method is called during the application startup.
   */
  public void seedHouseholdOne() {
    User testUser = createVerifiedUser("test@example.com", "test123", "Test", "Bruker", "12345678");
    User albert = createVerifiedUser("albert@example.com", "password123", "Albert", "Zindel", "98765432");
    Household household = createHousehold("Test Household", 59.9139, 10.7522);

    addHouseholdMember(household, testUser, true);
    addHouseholdMember(household, albert, false);

    List<Item> items = createItemsForHouseholdOne();
    household.setInventory(items);
    householdRepo.save(household);
  }

  /**
   * Seeds the database with household data.
   * This method is called during the application startup.
   */
  public void seedHouseholdTwo() {
    User krekar = createVerifiedUser("krekar@gmail.com", "test123", "Krekar", "Design", "11111111");

    Household household = createHousehold("Krekar's Household", 60.3913, 5.3221);
    addHouseholdMember(household, krekar, true);

    List<Item> items = createItemsForHouseholdTwo();
    household.setInventory(items);
    householdRepo.save(household);
  }

  /**
   * Seeds the database with household data.
   * This method is called during the application startup.
   */
  public void seedHouseholdThree() {
    User kalle = createVerifiedUser("kalle@gmail.com", "test123", "Kalle", "Kontainer", "2222222");
    System.out.println(kalle);
    Household household = createHousehold("Kalle's Container", 58.9690, 5.7331);
    addHouseholdMember(household, kalle, true);

    List<Item> items = createItemsForHouseholdThree();
    household.setInventory(items);
    householdRepo.save(household);
  }

  /**
   * Seeds the database with household data.
   * This method is called during the application startup.
   */
  public void seedHouseholdFour() {
    User kare = createVerifiedUser("kare@gmail.com", "test123", "Kåre", "Kakkelovn", "33333333");

    Household household = createHousehold("Kåre's Fireplace", 59.9139, 10.7522);
    addHouseholdMember(household, kare, true);

    List<Item> items = createItemsForHouseholdFour();
    household.setInventory(items);
    householdRepo.save(household);
  }

  /**
   * Creates a verified user with the given details.
   *
   * @param email       The email of the user
   * @param password    The password of the user
   * @param firstName   The first name of the user
   * @param lastName    The last name of the user
   * @param phoneNumber The phone number of the user
   * @return The created and verified user
   */
  public User createVerifiedUser(String email, String password, String firstName, String lastName, String phoneNumber) {
    User user = new User(
        email,
        hasher.hashPassword(password),
        firstName,
        lastName,
        phoneNumber
    );
    loginService.verifyEmail(user);
    userRepo.save(user);
    return user;
  }

  /**
   * Creates a household with the given details.
   *
   * @param name      The name of the household
   * @param latitude  The latitude of the household
   * @param longitude The longitude of the household
   * @return The created household
   */
  public Household createHousehold(String name, double latitude, double longitude) {
    Household household = new Household();
    household.setName(name);
    household.setLatitude(latitude);
    household.setLongitude(longitude);
    household.setMembers(new ArrayList<>());
    household.setInventory(new ArrayList<>());
    householdRepo.save(household);
    return household;
  }

  /**
   * Adds a household member to the given household.
   *
   * @param household The household to add the member to
   * @param user      The user to add as a member
   * @param isAdmin   Whether the user is an admin
   */
  public void addHouseholdMember(Household household, User user, boolean isAdmin) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    } else if (household == null) {
      throw new IllegalArgumentException("Household cannot be null");
    }
    if (!householdMembersRepo.existsByUserAndHousehold(user, household)) {
      HouseholdMembers member = new HouseholdMembers(user, household, isAdmin);
      householdMembersRepo.save(member);

      if (user.getHouseholdMemberships() == null) {
        user.setHouseholdMemberships(new ArrayList<>());
      }
      user.getHouseholdMemberships().add(member);
      if (household.getMembers() == null) {
        household.setMembers(new ArrayList<>());
      }
      household.getMembers().add(member);
    }
  }

  /**
   * Creates items for household one.
   *
   * @return List of created items
   */
  public List<Item> createItemsForHouseholdOne() {
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);

    Unit kg = unitRepo.findByName("KG").orElseThrow();
    Unit liter = unitRepo.findByName("L").orElseThrow();
    Unit count = unitRepo.findByName("Stk").orElseThrow();

    Category water = categoryRepo.findByName("Water").orElseThrow();
    Category cannedFood = categoryRepo.findByName("Canned Food").orElseThrow();
    Category driedFood = categoryRepo.findByName("Dried Food").orElseThrow();
    Category medicalSupplies = categoryRepo.findByName("Medical Supplies").orElseThrow();
    Category snacks = categoryRepo.findByName("Snacks").orElseThrow();
    Category whiteMonster = categoryRepo.findByName("Sugar Free Monster").orElseThrow();
    Category kjøkkenutstyr = categoryRepo.findByName("Kjøkkenutstyr").orElseThrow();


    List<Item> items = new ArrayList<>();

    items.add(new Item("Water Bottle", 4, liter, getFutureDate(cal, Calendar.MONTH, 1), water));
    items.add(new Item("Massive Water Bottle", 38, liter, getFutureDate(cal, Calendar.MONTH, 1), water));
    items.add(new Item("Canned Beans", 60, kg, getFutureDate(cal, Calendar.MONTH, 3), cannedFood));
    items.add(new Item("Canned Corn", 0.1, kg, getFutureDate(cal, Calendar.YEAR, 1), cannedFood));
    items.add(new Item("Canned Soup from the soup store", 0.1, kg, getFutureDate(cal, Calendar.YEAR, 2), cannedFood));
    items.add(new Item("Dried mango", 0.1, kg, getFutureDate(cal, Calendar.YEAR, 1), driedFood));
    items.add(new Item("Bandages", 10, count, getFutureDate(cal, Calendar.YEAR, 2), medicalSupplies));
    items.add(new Item("Pain Relievers", 20, count, getFutureDate(cal, Calendar.YEAR, 1), medicalSupplies));
    items.add(new Item("Chips", 0.1, kg, getFutureDate(cal, Calendar.DAY_OF_YEAR, 10), snacks));
    items.add(new Item("You", 0.1, kg, getFutureDate(cal, Calendar.DAY_OF_YEAR, 10), snacks));
    items.add(new Item("White Monster", 0.1, liter, getFutureDate(cal, Calendar.WEEK_OF_YEAR, 2), whiteMonster));
    items.add(new Item("Førstehjelp", 1, count, getFutureDate(cal, Calendar.YEAR, 1), medicalSupplies));
    items.add(new Item("Jodtabletter", 10, count, getFutureDate(cal, Calendar.YEAR, 1), medicalSupplies));
    items.add(new Item("Kokeapparat", 1, count, getFutureDate(cal, Calendar.YEAR, 1), kjøkkenutstyr));



    return itemRepo.saveAll(items);
  }

  /**
   * Creates items for household two.
   *
   * @return List of created items
   */
  public List<Item> createItemsForHouseholdTwo() {
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);

    Unit liter = unitRepo.findByName("L").orElseThrow();
    Unit kg = unitRepo.findByName("KG").orElseThrow();

    Category beverages = categoryRepo.findByName("Beverages").orElseThrow();
    Category snacks = categoryRepo.findByName("Snacks").orElseThrow();

    List<Item> items = new ArrayList<>();

    items.add(new Item("Soda", 1, liter, getFutureDate(cal, Calendar.WEEK_OF_YEAR, 2), beverages));
    items.add(new Item("Juice", 1, liter, getFutureDate(cal, Calendar.WEEK_OF_YEAR, 2), beverages));
    items.add(new Item("Candy", 100, kg, getFutureDate(cal, Calendar.DAY_OF_YEAR, 10), snacks));

    return itemRepo.saveAll(items);
  }

  /**
   * Creates items for household three.
   *
   * @return List of created items
   */
  public List<Item> createItemsForHouseholdThree() {
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);

    Unit kg = unitRepo.findByName("KG").orElseThrow();

    Category petFood = categoryRepo.findByName("Pet Food").orElseThrow();
    Category frozenFood = categoryRepo.findByName("Frozen Food").orElseThrow();
    Category freshFruits = categoryRepo.findByName("Fresh Fruits").orElseThrow();

    List<Item> items = new ArrayList<>();

    items.add(new Item("The finest steak", 5, kg, getFutureDate(cal, Calendar.DAY_OF_YEAR, 10), petFood));
    items.add(new Item("Grandiosa", 1, kg, getFutureDate(cal, Calendar.YEAR, 1), frozenFood));
    items.add(new Item("Dragonfruit", 1, kg, getFutureDate(cal, Calendar.WEEK_OF_YEAR, 2), freshFruits));

    return itemRepo.saveAll(items);
  }

  /**
   * Creates items for household four.
   *
   * @return List of created items
   */
  public List<Item> createItemsForHouseholdFour() {
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);

    Unit kg = unitRepo.findByName("KG").orElseThrow();
    Unit count = unitRepo.findByName("Stk").orElseThrow();

    Category grains = categoryRepo.findByName("Grains (Rice, Pasta)").orElseThrow();
    Category babySupplies = categoryRepo.findByName("Baby Supplies").orElseThrow();
    Category cookingFuel = categoryRepo.findByName("Cooking Fuel").orElseThrow();
    Category freshVegetables = categoryRepo.findByName("Fresh Vegetables").orElseThrow();
    Category hygieneProducts = categoryRepo.findByName("Hygiene Products").orElseThrow();
    Category batteries = categoryRepo.findByName("Batteries").orElseThrow();

    List<Item> items = new ArrayList<>();

    items.add(new Item("Rice", 1, kg, getFutureDate(cal, Calendar.YEAR, 2), grains));
    items.add(new Item("Pasta", 1, kg, getFutureDate(cal, Calendar.YEAR, 2), grains));
    items.add(new Item("Baby Formula", 1, kg, getFutureDate(cal, Calendar.MONTH, 1), babySupplies));
    items.add(new Item("Propane Canister", 1, kg, getFutureDate(cal, Calendar.YEAR, 2), cookingFuel));
    items.add(new Item("Paprika", 1, kg, getFutureDate(cal, Calendar.WEEK_OF_YEAR, 2), freshVegetables));
    items.add(new Item("Toilet Paper", 12, count, getFutureDate(cal, Calendar.YEAR, 1), hygieneProducts));
    items.add(new Item("Soap", 5, count, getFutureDate(cal, Calendar.YEAR, 1), hygieneProducts));
    items.add(new Item("AA Batteries", 4, count, getFutureDate(cal, Calendar.YEAR, 1), batteries));
    items.add(new Item("9000V Batteries", 4, count, getFutureDate(cal, Calendar.YEAR, 1), batteries));

    return itemRepo.saveAll(items);
  }

  /**
   * Gets a future date by adding the specified amount to the given field.
   *
   * @param cal    The calendar instance
   * @param field  The field to add to (e.g., Calendar.MONTH)
   * @param amount The amount to add
   * @return The future date
   */
  public Date getFutureDate(Calendar cal, int field, int amount) {
    cal.add(field, amount);
    Date futureDate = cal.getTime();
    cal.add(field, -amount);
    return futureDate;
  }

  /**
   * Seeds the database with emergency services and their types.
   * This method is called during the application startup.
   */
  public void seedEmergencyServicesWithTypes() {
    Type shelter = typeRepo.findByName("Shelter").orElseGet(() -> typeRepo.save(new Type("Shelter")));
    Type hospital = typeRepo.findByName("Hospital").orElseGet(() -> typeRepo.save(new Type("Hospital")));
    Type fireStation = typeRepo.findByName("Fire Station").orElseGet(() -> typeRepo.save(new Type("Fire Station")));
    Type police = typeRepo.findByName("Police Station").orElseGet(() -> typeRepo.save(new Type("Police Station")));
    Type heartStarter = typeRepo.findByName("Heart Starter").orElseGet(() -> typeRepo.save(new Type("Heart Starter")));
    Type foodDistribution = typeRepo.findByName("Food Distribution").orElseGet(() -> typeRepo.save(new Type("Food Distribution")));
    Type waterStation = typeRepo.findByName("Water Station").orElseGet(() -> typeRepo.save(new Type("Water Station")));

    if (emergencyServiceRepo.count() == 0) {
      List<EmergencyService> services = List.of(
          new EmergencyService("Bomb Shelter - Oslo Center", "Capacity: 150",59.9139, 10.7522, null,shelter),
          new EmergencyService("Community Hospital - Bergen", "Hospital",60.3913, 5.3221, null, hospital),
          new EmergencyService("Central Fire Station - Stavanger", "Police Station",58.9690, 5.7331, null, fireStation),
          new EmergencyService("City Police HQ - Trondheim", "Police Station", 63.4305, 10.3951, null, police),
          new EmergencyService("Temporary Shelter - Tromsø", "Capacity: 50", 69.6496, 18.9560, null, shelter),
          new EmergencyService("Emergency Food Distribution - Drammen", "Food Distribution", 59.7439, 10.2045, null, foodDistribution),
          new EmergencyService("Water Station - Kristiansand", "Water station", 58.1467, 7.9956, null, waterStation),
          new EmergencyService("Water Tanker - Bodø Harbor", "Water tanker", 67.2804, 14.4049, null, waterStation),
          new EmergencyService("Defibrillator - Oslo Train Station", "Defibrillator", 59.9115, 10.7553, null, heartStarter),
          new EmergencyService("Defibrillator - Bergen Airport", "Defibrillator", 60.2934, 5.2181, null, heartStarter)
      );
      emergencyServiceRepo.saveAll(services);
    }
  }
}