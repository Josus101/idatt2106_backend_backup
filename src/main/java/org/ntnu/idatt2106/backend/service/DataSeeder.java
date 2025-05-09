package org.ntnu.idatt2106.backend.service;

import com.fasterxml.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ntnu.idatt2106.backend.controller.BunkerImportController;
import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.model.map.MapEntityType;
import org.ntnu.idatt2106.backend.model.map.MapMarkerType;
import org.ntnu.idatt2106.backend.model.map.MapZoneType;
import org.ntnu.idatt2106.backend.repo.*;
import org.ntnu.idatt2106.backend.repo.map.MapEntityTypeRepo;
import org.ntnu.idatt2106.backend.repo.map.MapMarkerTypeRepo;
import org.ntnu.idatt2106.backend.repo.map.MapZoneTypeRepo;
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
  private final HouseholdRepo householdRepo;
  private final ItemRepo itemRepo;
  private final UnitRepo unitRepo;
  private final HouseholdMembersRepo householdMembersRepo;
  private final MapEntityTypeRepo mapEntityTypeRepo;
  private final MapMarkerTypeRepo mapMarkerTypeRepo;
  private final MapZoneTypeRepo mapZoneTypeRepo;
  private final BCryptHasher hasher;
  private final LoginService loginService;
  private final UserSettingsService userSettingsService;
  private final MapEntityService mapEntityService;

  private final BunkerImportController bunkerImportController;

  /**
   * Constructor for DataSeeder.
   *
   * @param adminRepo Admin repository
   * @param userRepo User repository
   * @param hasher Password hasher
   * @param loginService Login service
   * @param categoryRepo Category repository
   * @param householdRepo Household repository
   * @param itemRepo Item repository
   * @param unitRepo Unit repository
   * @param householdMembersRepo Household members repository
   * @param userSettingsService User settings service
   */
  public DataSeeder(AdminRepo adminRepo, UserRepo userRepo,
                    BCryptHasher hasher, LoginService loginService, CategoryRepo categoryRepo,
                    HouseholdRepo householdRepo, ItemRepo itemRepo, UnitRepo unitRepo,
                    MapEntityTypeRepo mapEntityTypeRepo, MapMarkerTypeRepo mapMarkerTypeRepo,
                    MapZoneTypeRepo mapZoneTypeRepo, MapEntityService mapEntityService,
                    HouseholdMembersRepo householdMembersRepo, UserSettingsService userSettingsService,
                    BunkerImportController bunkerImportController) {
    this.userRepo = userRepo;
    this.adminRepo = adminRepo;
    this.categoryRepo = categoryRepo;
    this.householdRepo = householdRepo;
    this.itemRepo = itemRepo;
    this.unitRepo = unitRepo;
    this.householdMembersRepo = householdMembersRepo;
    this.hasher = hasher;
    this.loginService = loginService;
    this.userSettingsService = userSettingsService;
    this.mapEntityTypeRepo = mapEntityTypeRepo;
    this.mapMarkerTypeRepo = mapMarkerTypeRepo;
    this.mapZoneTypeRepo = mapZoneTypeRepo;
    this.mapEntityService = mapEntityService;
    this.bunkerImportController = bunkerImportController;
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
    seedHouseholdFive();
    seedMapEntities();

    createTestUserAndAdminData();
    bunkerImportController.importBunkerData();
    System.out.println("\nSeeding complete.\nZ");
  }

  /**
   * Seeds the database with categories and units.
   * This method is called during the application startup.
   */
  public void seedCategoriesAndUnits() {
    if (categoryRepo.count() == 0) {
      List<Category> categories = List.of(
          new Category("Other", "Annet", 0, false),
          new Category("Water", "Vann", 0, true),
          new Category("Canned Food", "Hermetikk", 200, true),
          new Category("Dried Food", "Tørket mat", 350, true),
          new Category("Medical Supplies", "Førstehjelp", 0, true),
          new Category("Snacks", "Snacks", 500, false),
          new Category("Beverages", "Drikkevarer", 45, false),
          new Category("Sugar Free Monster", "Sugar Free Monster", 11, false),
          new Category("Pet Food", "Dyremat", 300, true),
          new Category("Baby Supplies", "Forsyninger baby", 100, true),
          new Category("Batteries", "Batterier", 0, true),
          new Category("Hygiene Products", "Hygiene produkter", 0, true),
          new Category("Cooking Fuel", "Gass brenner drivstoff", 0, true),
          new Category("Fresh Vegetables", "Grønnsaker", 35, false),
          new Category("Fresh Fruits", "Frukt", 52, false),
          new Category("Frozen Food", "Frossen mat", 250, true),
          new Category("Grains (Rice, Pasta)", "Kornprodukter (Ris, Pasta)", 360, true),
          new Category("Kitchen Appliances", "Kjøkkenutstyr", 0, true)
      );
      categoryRepo.saveAll(categories);
    }
    if (unitRepo.count() == 0) {
      List<Unit> units = List.of(
          new Unit("KG", "KG"),
          new Unit("L", "L"),
          new Unit("PCS", "Stk")
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
      Admin admin = new Admin("admin", hasher.hashPassword("admin123"), "admin@krisefikser.no", false);
      admin.setActive(true);
      admin.setTwoFactorEnabled(false);
      adminRepo.save(admin);
    }
    if (!adminRepo.existsByUsername("urekmazino")) {
      Admin urek = new Admin("urekmazino", hasher.hashPassword("admin123"), "urek@krisefikser.no", true);
      urek.setActive(true);
      urek.setTwoFactorEnabled(false);
      adminRepo.save(urek);
    }
    if (!adminRepo.existsByUsername("albert")) {
      Admin albert = new Admin("albert", hasher.hashPassword("admin123"), "albert@example.com", true);
      albert.setActive(true);
      albert.setTwoFactorEnabled(false);
      adminRepo.save(albert);
    }
  }

  /**
   * Seeds the database with household data.
   * This method is called during the application startup.
   */
  public void seedHouseholdOne() {
    User testUser = createVerifiedUser("test@example.com", "test123", "Test", "Bruker", "12345677");
    User albert = createVerifiedUser("albert@example.com", "password123", "Albert", "Zindel", "98765432");
    Household household = createHousehold("Test Household", 59.9139, 10.7522);
    Household household2 = createHousehold("Albert household", 40.0815, 124.4993);

    addHouseholdMember(household, testUser, true, true);
    addHouseholdMember(household, albert, false, false);

    addHouseholdMember(household2, albert, true, true);

    List<Item> items = createItemsForHouseholdOne();
    household.setInventory(items);
    householdRepo.save(household);


    UserStoreSettingsRequest defaultSettings = new UserStoreSettingsRequest(true, true);
    userSettingsService.saveUserSettings(albert.getId(), defaultSettings);
    
    List<Item> items2 = createItemsForHouseholdTwo();
    household2.setInventory(items2);
    householdRepo.save(household2);
  }

  /**
   * Seeds the database with household data.
   * This method is called during the application startup.
   */
  public void seedHouseholdTwo() {
    User krekar = createVerifiedUser("krekar@gmail.com", "test123456", "Krekar", "Design", "11111111");


    Household household = createHousehold("Krekar's Household", 60.3913, 5.3221);
    addHouseholdMember(household, krekar, true, false);

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
    addHouseholdMember(household, kalle, true, true );

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
    addHouseholdMember(household, kare, true, true);

    List<Item> items = createItemsForHouseholdFour();
    household.setInventory(items);
    householdRepo.save(household);
  }

  public void seedHouseholdFive() {
    User jonas = createVerifiedUser("jonas@email.com", "password123", "Jonas", "Reiher", "12121212");
    User erlend = createVerifiedUser("erlend@email.com", "password123", "Erlend Eide", "Zindel", "34343434");
    User andré = createVerifiedUser("andre@email.com", "password123", "André Wikheim", "Merkesdal", "56565656");
    User christian = createVerifiedUser("christian@email.com", "password123", "Christian Hess", "Bore", "78787878");
    User ekslid = createVerifiedUser("eskild@email.com", "password123", "Eskild", "Smestu", "90909090");
    User konrad = createVerifiedUser("konrad@email.com", "password123", "Konrad", "Seime", "10101010");
    User oleThomas = createVerifiedUser("oleThomas@email.com", "password123", "Ole-Thomas Sundvoll", "Moe", "21212121");
    User sigurd = createVerifiedUser("sigurd@email.com", "password123", "Sigurd", "Spook", "23232323");

    Household household = createHousehold("Team 1", 63.419302, 10.401528);
    addHouseholdMember(household, jonas, true, true);
    addHouseholdMember(household, erlend, true, true);
    addHouseholdMember(household, andré, false, true);
    addHouseholdMember(household, christian, true, true);
    addHouseholdMember(household, ekslid, false, true);
    addHouseholdMember(household, konrad, false, true);
    addHouseholdMember(household, oleThomas, false, true);
    addHouseholdMember(household, sigurd, false, true);

    List<Item> items = createItemsForHouseholdFive();
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
  public void addHouseholdMember(Household household, User user, boolean isAdmin, boolean isPrimary) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    } else if (household == null) {
      throw new IllegalArgumentException("Household cannot be null");
    }
    if (!householdMembersRepo.existsByUserAndHousehold(user, household)) {
      HouseholdMembers member = new HouseholdMembers(user, household, isAdmin, isPrimary);
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

    Unit kg = unitRepo.findByEnglishName("KG").orElseThrow();
    Unit liter = unitRepo.findByEnglishName("L").orElseThrow();
    Unit count = unitRepo.findByEnglishName("PCS").orElseThrow();

    Category water = categoryRepo.findByEnglishName("Water").orElseThrow();
    Category cannedFood = categoryRepo.findByEnglishName("Canned Food").orElseThrow();
    Category driedFood = categoryRepo.findByEnglishName("Dried Food").orElseThrow();
    Category medicalSupplies = categoryRepo.findByEnglishName("Medical Supplies").orElseThrow();
    Category snacks = categoryRepo.findByEnglishName("Snacks").orElseThrow();
    Category whiteMonster = categoryRepo.findByEnglishName("Sugar Free Monster").orElseThrow();
    Category kitchenAppliances = categoryRepo.findByEnglishName("Kitchen Appliances").orElseThrow();


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
    items.add(new Item("Kokeapparat", 1, count, getFutureDate(cal, Calendar.YEAR, 1), kitchenAppliances));



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

    Unit liter = unitRepo.findByEnglishName("L").orElseThrow();
    Unit kg = unitRepo.findByEnglishName("KG").orElseThrow();

    Category beverages = categoryRepo.findByEnglishName("Beverages").orElseThrow();
    Category snacks = categoryRepo.findByEnglishName("Snacks").orElseThrow();

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

    Unit kg = unitRepo.findByEnglishName("KG").orElseThrow();

    Category petFood = categoryRepo.findByEnglishName("Pet Food").orElseThrow();
    Category frozenFood = categoryRepo.findByEnglishName("Frozen Food").orElseThrow();
    Category freshFruits = categoryRepo.findByEnglishName("Fresh Fruits").orElseThrow();

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

    Unit kg = unitRepo.findByEnglishName("KG").orElseThrow();
    Unit count = unitRepo.findByEnglishName("PCS").orElseThrow();

    Category grains = categoryRepo.findByEnglishName("Grains (Rice, Pasta)").orElseThrow();
    Category babySupplies = categoryRepo.findByEnglishName("Baby Supplies").orElseThrow();
    Category cookingFuel = categoryRepo.findByEnglishName("Cooking Fuel").orElseThrow();
    Category freshVegetables = categoryRepo.findByEnglishName("Fresh Vegetables").orElseThrow();
    Category hygieneProducts = categoryRepo.findByEnglishName("Hygiene Products").orElseThrow();
    Category batteries = categoryRepo.findByEnglishName("Batteries").orElseThrow();

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
   * Creates items for household five.
   *
   * @return List of created items
   */
  public List<Item> createItemsForHouseholdFive() {
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);

    Unit kg = unitRepo.findByEnglishName("KG").orElseThrow();
    Unit count = unitRepo.findByEnglishName("PCS").orElseThrow();
    Unit liter = unitRepo.findByEnglishName("L").orElseThrow();

    Category water = categoryRepo.findByEnglishName("Water").orElseThrow();
    Category cannedFood = categoryRepo.findByEnglishName("Canned Food").orElseThrow();
    Category driedFood = categoryRepo.findByEnglishName("Dried Food").orElseThrow();
    Category snacks = categoryRepo.findByEnglishName("Snacks").orElseThrow();
    Category beverages = categoryRepo.findByEnglishName("Beverages").orElseThrow();
    Category batteries = categoryRepo.findByEnglishName("Batteries").orElseThrow();
    Category hygieneProducts = categoryRepo.findByEnglishName("Hygiene Products").orElseThrow();

    List<Item> items = new ArrayList<>();

    // Water and beverages
    items.add(new Item("Bottled Water", 15, liter, getFutureDate(cal, Calendar.MONTH, 6), water));
    items.add(new Item("Energy Drink", 19, liter, getFutureDate(cal, Calendar.MONTH, 3), beverages));

    // Canned and dried foods
    items.add(new Item("Canned Beans", 3, kg, getFutureDate(cal, Calendar.YEAR, 2), cannedFood));
    items.add(new Item("Canned Soup", 4, kg, getFutureDate(cal, Calendar.YEAR, 2), cannedFood));
    items.add(new Item("Dried Lentils", 2, kg, getFutureDate(cal, Calendar.YEAR, 1), driedFood));
    items.add(new Item("Dried Fruit Mix", 1, kg, getFutureDate(cal, Calendar.MONTH, 6), driedFood));
    items.add(new Item("Nuts and Seeds", 1, kg, getFutureDate(cal, Calendar.MONTH, 6), snacks));
    items.add(new Item("Chips", 3, count, getFutureDate(cal, Calendar.MONTH, 3), snacks));

    // Utility items
    items.add(new Item("Hand Sanitizer", 2, count, getFutureDate(cal, Calendar.YEAR, 1), hygieneProducts));
    items.add(new Item("AA Batteries", 8, count, getFutureDate(cal, Calendar.YEAR, 1), batteries));
    items.add(new Item("Rechargeable Battery Pack", 1, count, getFutureDate(cal, Calendar.YEAR, 3), batteries));

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
   * Seeds the database with map entities.
   * This method is called during the application startup.
   */
  public void seedMapEntities() {
    MapEntityType markerType = new MapEntityType();
    markerType.setName("marker");
    MapEntityType zoneType = new MapEntityType();
    zoneType.setName("zone");
    mapEntityTypeRepo.save(markerType);
    mapEntityTypeRepo.save(zoneType);

    MapMarkerType defib = new MapMarkerType();
    defib.setName("Hjertestarter");
    MapMarkerType bunker = new MapMarkerType();
    bunker.setName("Bunker");
    MapMarkerType assembingArea = new MapMarkerType();
    assembingArea.setName("Møteplass");
    MapMarkerType foodEmergency = new MapMarkerType();
    foodEmergency.setName("Matstasjon");
    mapMarkerTypeRepo.save(defib);
    mapMarkerTypeRepo.save(bunker);
    mapMarkerTypeRepo.save(assembingArea);
    mapMarkerTypeRepo.save(foodEmergency);


    MapZoneType flood = new MapZoneType();
    flood.setName("Flom");
    MapZoneType fire = new MapZoneType();
    fire.setName("Brann");
    MapZoneType earthquake = new MapZoneType();
    earthquake.setName("Jordskjelv");
    MapZoneType gas = new MapZoneType();
    gas.setName("Gassutslipp");
    mapZoneTypeRepo.save(flood);
    mapZoneTypeRepo.save(fire);
    mapZoneTypeRepo.save(earthquake);
    mapZoneTypeRepo.save(gas);
  }

  /**
   * Creates test user and admin data.
   * This method is called during the application startup.
   */
  public void createTestUserAndAdminData() {
    User userNormal = createVerifiedUser("test.user@example.com", "password123", "Test", "User", "12345678");
    Admin admin = new Admin("admin123", "password123", "test.admin@example.com", false);
    Admin adminSuper = new Admin("super123", "password123", "test.super@example.com", true);

    admin.setTwoFactorEnabled(false);
    admin.setActive(true);
    adminSuper.setTwoFactorEnabled(false);
    adminSuper.setActive(true);

    userRepo.save(userNormal);
    adminRepo.save(admin);
    adminRepo.save(adminSuper);
  }
}