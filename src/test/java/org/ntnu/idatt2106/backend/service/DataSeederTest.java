package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.*;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

  @Mock
  private AdminRepo adminRepo;

  @Mock
  private UserRepo userRepo;

  @Mock
  private CategoryRepo categoryRepo;

  @Mock
  private EmergencyServiceRepo emergencyServiceRepo;

  @Mock
  private HouseholdRepo householdRepo;

  @Mock
  private ItemRepo itemRepo;

  @Mock
  private TypeRepo typeRepo;

  @Mock
  private UnitRepo unitRepo;

  @Mock
  private HouseholdMembersRepo householdMembersRepo;

  @Mock
  private BCryptHasher hasher;

  @Mock
  private LoginService loginService;

  @InjectMocks
  private DataSeeder dataSeeder;

  @Test
  @DisplayName("Test run method when users do not exist")
  void testRunSkipsSeedingWhenUsersExist() throws Exception {
    when(userRepo.count()).thenReturn(1L);

    dataSeeder.run(String.valueOf(new DefaultApplicationArguments()));

    verify(userRepo, times(1)).count();
    verifyNoMoreInteractions(userRepo);
  }

  @Test
  @DisplayName("Test run method when no users exist — all seed methods should be called")
  void testRun_WhenNoUsersExist_ShouldCallAllSeedMethods() throws Exception {
    // Given
    when(userRepo.count()).thenReturn(0L);

    // Spy so we can verify internal method calls
    DataSeeder spySeeder = spy(dataSeeder);

    // Use doNothing to avoid actual execution of methods (optional but clean)
    doNothing().when(spySeeder).seedCategoriesAndUnits();
    doNothing().when(spySeeder).seedAdminUsers();
    doNothing().when(spySeeder).seedHouseholdOne();
    doNothing().when(spySeeder).seedHouseholdTwo();
    doNothing().when(spySeeder).seedHouseholdThree();
    doNothing().when(spySeeder).seedHouseholdFour();
    doNothing().when(spySeeder).seedEmergencyServicesWithTypes();

    // When
    spySeeder.run();

    // Then
    verify(userRepo).count();
    verify(spySeeder).seedCategoriesAndUnits();
    verify(spySeeder).seedAdminUsers();
    verify(spySeeder).seedHouseholdOne();
    verify(spySeeder).seedHouseholdTwo();
    verify(spySeeder).seedHouseholdThree();
    verify(spySeeder).seedHouseholdFour();
    verify(spySeeder).seedEmergencyServicesWithTypes();
  }

  @Test
  @DisplayName("Test run method skips seeding if users already exist")
  void testRun_WhenUsersExist_ShouldSkipSeeding() throws Exception {
    when(userRepo.count()).thenReturn(5L);

    DataSeeder spySeeder = spy(dataSeeder);

    // Prevent seeding methods from being accidentally called
    lenient().doThrow(new RuntimeException("Should not be called")).when(spySeeder).seedCategoriesAndUnits();

    spySeeder.run();

    verify(userRepo).count();
    verify(spySeeder, never()).seedCategoriesAndUnits();
    verify(spySeeder, never()).seedAdminUsers();
    verify(spySeeder, never()).seedHouseholdOne();
    verify(spySeeder, never()).seedHouseholdTwo();
    verify(spySeeder, never()).seedHouseholdThree();
    verify(spySeeder, never()).seedHouseholdFour();
    verify(spySeeder, never()).seedEmergencyServicesWithTypes();
  }


  @Test
  @DisplayName("Test run method when users exist")
  void testSeedCategoriesAndUnitsWhenEmpty() {
    when(categoryRepo.count()).thenReturn(0L);
    when(unitRepo.count()).thenReturn(0L);

    dataSeeder.seedCategoriesAndUnits();


    verify(categoryRepo, times(1)).saveAll(anyList());
    verify(unitRepo, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("Test run method when categories and units exist")
  void testSeedCategoriesAndUnitsWhenNotEmpty() {
    when(categoryRepo.count()).thenReturn(1L);
    when(unitRepo.count()).thenReturn(1L);

    dataSeeder.seedCategoriesAndUnits();

    verify(categoryRepo, never()).saveAll(anyList());
    verify(unitRepo, never()).saveAll(anyList());
  }

  @Test
  @DisplayName("Test run method when emergency services exist")
  void testSeedAdminUsersWhenNotExist() {
    when(adminRepo.existsByUsername("admin")).thenReturn(false);
    when(adminRepo.existsByUsername("urekmazino")).thenReturn(false);
    when(hasher.hashPassword("admin123")).thenReturn("hashedPassword");

    dataSeeder.seedAdminUsers();

    verify(adminRepo, times(2)).save(any(Admin.class));
  }

  @Test
  @DisplayName("Test run method when emergency services do not exist")
  void testSeedAdminUsersWhenExist() {
    when(adminRepo.existsByUsername("admin")).thenReturn(true);
    when(adminRepo.existsByUsername("urekmazino")).thenReturn(true);

    dataSeeder.seedAdminUsers();

    verify(adminRepo, never()).save(any(Admin.class));
  }

  @Test
  @DisplayName("Test createVerifiedUser method")
  void testCreateVerifiedUser() {
    String email = "test@example.com";
    String password = "test123";
    String hashedPassword = "hashedPassword";

    when(hasher.hashPassword(password)).thenReturn(hashedPassword);
    when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User user = dataSeeder.createVerifiedUser(email, password, "Test", "User", "12345678");

    assertEquals(email, user.getEmail());
    assertEquals(hashedPassword, user.getPassword());
    verify(loginService, times(1)).verifyEmail(user);
  }

  @Test
  @DisplayName("Test createUser method")
  void testCreateHousehold() {
    String name = "Test Household";
    double latitude = 59.9139;
    double longitude = 10.7522;

    when(householdRepo.save(any(Household.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Household household = dataSeeder.createHousehold(name, latitude, longitude);

    assertEquals(name, household.getName());
    assertEquals(latitude, household.getLatitude());
    assertEquals(longitude, household.getLongitude());
    assertNotNull(household.getMembers());
    assertNotNull(household.getInventory());
  }

  @Test
  @DisplayName("Test addHouseholdMember method")
  void testAddHouseholdMember() {
    User user = new User();
    Household household = new Household();

    when(householdMembersRepo.save(any(HouseholdMembers.class))).thenAnswer(invocation -> invocation.getArgument(0));

    dataSeeder.addHouseholdMember(household, user, true, false);

    assertEquals(1, household.getMembers().size());
    verify(householdMembersRepo, times(1)).save(any(HouseholdMembers.class));
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testSeedHouseholdOne() {
    when(hasher.hashPassword(anyString())).thenReturn("hashedPassword");
    when(householdRepo.save(any(Household.class))).thenReturn(new Household());
    when(itemRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

    when(categoryRepo.findByName(anyString())).thenReturn(Optional.of(new Category()));
    when(unitRepo.findByName(anyString())).thenReturn(Optional.of(new Unit()));

    dataSeeder.seedHouseholdOne();

    verify(userRepo, times(2)).save(any(User.class));
    verify(householdRepo, times(4)).save(any(Household.class));
    verify(householdMembersRepo, times(3)).save(any(HouseholdMembers.class));
    verify(itemRepo, times(2)).saveAll(anyList());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testSeedHouseholdTwo() {
    when(hasher.hashPassword(anyString())).thenReturn("hashedPassword");
    when(householdRepo.save(any(Household.class))).thenReturn(new Household());
    when(itemRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

    when(categoryRepo.findByName(anyString())).thenReturn(Optional.of(new Category()));
    when(unitRepo.findByName(anyString())).thenReturn(Optional.of(new Unit()));

    dataSeeder.seedHouseholdTwo();

    verify(userRepo, times(1)).save(any(User.class));
    verify(householdRepo, times(2)).save(any(Household.class));
    verify(householdMembersRepo, times(1)).save(any(HouseholdMembers.class));
    verify(itemRepo, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testSeedHouseholdThree() {
    when(hasher.hashPassword(anyString())).thenReturn("hashedPassword");
    when(householdRepo.save(any(Household.class))).thenReturn(new Household());
    when(itemRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

    when(categoryRepo.findByName(anyString())).thenReturn(Optional.of(new Category()));
    when(unitRepo.findByName(anyString())).thenReturn(Optional.of(new Unit()));

    dataSeeder.seedHouseholdThree();

    verify(userRepo, times(1)).save(any(User.class));
    verify(householdRepo, times(2)).save(any(Household.class));
    verify(householdMembersRepo, times(1)).save(any(HouseholdMembers.class));
    verify(itemRepo, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testSeedHouseholdFour() {
    when(hasher.hashPassword(anyString())).thenReturn("hashedPassword");
    when(householdRepo.save(any(Household.class))).thenReturn(new Household());
    when(itemRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

    when(categoryRepo.findByName(anyString())).thenReturn(Optional.of(new Category()));
    when(unitRepo.findByName(anyString())).thenReturn(Optional.of(new Unit()));

    dataSeeder.seedHouseholdFour();

    verify(userRepo, times(1)).save(any(User.class));
    verify(householdRepo, times(2)).save(any(Household.class));
    verify(householdMembersRepo, times(1)).save(any(HouseholdMembers.class));
    verify(itemRepo, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testGetFutureDate() {
    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.MONTH, 1);
    Date expected = cal.getTime();
    cal.add(Calendar.MONTH, -1);

    Date result = dataSeeder.getFutureDate(cal, Calendar.MONTH, 1);

    assertEquals(expected, result);
    assertEquals(now, cal.getTime());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testSeedEmergencyServicesWithTypes_WhenEmpty() {
    when(emergencyServiceRepo.count()).thenReturn(0L);
    when(typeRepo.findByName(anyString())).thenReturn(Optional.empty());
    when(typeRepo.save(any(Type.class))).thenAnswer(invocation -> invocation.getArgument(0));

    dataSeeder.seedEmergencyServicesWithTypes();

    verify(typeRepo, atLeastOnce()).save(any(Type.class));
    verify(emergencyServiceRepo, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with existing member")
  void testSeedEmergencyServicesWithTypes_WhenNotEmpty() {
    when(emergencyServiceRepo.count()).thenReturn(1L);

    dataSeeder.seedEmergencyServicesWithTypes();

    verify(emergencyServiceRepo, never()).saveAll(anyList());
  }

  @Test
  @DisplayName("Test addHouseholdMember method with member is null")
  void testAddHouseholdMember_WithMemberIsNull() {
    User user = null;
    Household household = new Household();


    assertThrows(IllegalArgumentException.class, () -> dataSeeder.addHouseholdMember(household, user, true, false));
    verify(householdMembersRepo, never()).save(any(HouseholdMembers.class));
  }


  @Test
  @DisplayName("Test addHouseholdMember method with household is null")
  void testAddHouseholdMember_WithHouseholdIsNull() {
    User user = new User();
    Household household = null;

    assertThrows(IllegalArgumentException.class, () -> dataSeeder.addHouseholdMember(household, user, true, false));
    verify(householdMembersRepo, never()).save(any(HouseholdMembers.class));
  }



}