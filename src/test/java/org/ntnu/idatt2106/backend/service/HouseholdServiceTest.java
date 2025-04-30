package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.HouseholdCreate;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("HouseholdService Unit Tests")
class HouseholdServiceTest {

  @InjectMocks
  private HouseholdService householdService;

  @Mock
  private HouseholdJoinCodeRepo householdJoinCodeRepo;

  @Mock
  private HouseholdRepo householdRepo;

  @Mock
  private HouseholdMembersRepo householdMembersRepo;

  @Mock
  private UserRepo userRepo;

  private Household testHousehold;
  private User testUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testHousehold = new Household(1, "TestHouse", 10.0, 20.0);
    testUser = new User("test@example.com", "password123", "Ape", "Apemann", "69420420");
  }

  @Test
  @DisplayName("Should create a new household")
  void testCreateHousehold() {
    when(householdRepo.save(any(Household.class))).thenReturn(testHousehold);

    Household result = householdService.createHousehold("TestHouse", 10.0, 20.0);

    assertNotNull(result);
    assertEquals("TestHouse", result.getName());
    verify(householdRepo).save(any(Household.class));
  }

  @Test
  @DisplayName("Should create a new household with dto constructor")
  void testCreateHouseholdWithDTO() {
    when(householdRepo.save(any(Household.class))).thenReturn(testHousehold);

    HouseholdCreate dto = new HouseholdCreate("TestHouse", 10.0, 20.0);
    Household result = householdService.createHousehold(dto);

    assertNotNull(result);
    assertEquals("TestHouse", result.getName());
    verify(householdRepo).save(any(Household.class));
  }

  @Test
  @DisplayName("Should generate a valid join code")
  void testGenerateJoinCode() {
    when(householdJoinCodeRepo.existsByCode(anyString())).thenReturn(false);
    when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(true);
    String code = householdService.generateJoinCode(testHousehold, testUser);

    assertNotNull(code);
    assertEquals(8, code.length());
    verify(householdJoinCodeRepo).save(any(HouseholdJoinCode.class));
  }

  @Test
  @DisplayName("Should not generate a join code if user is not a member")
  void testGenerateJoinCodeForNonMember() {
    when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> {
      householdService.generateJoinCode(testHousehold, testUser);
    });
    verify(householdJoinCodeRepo, never()).save(any(HouseholdJoinCode.class));
  }

  @Test
  @DisplayName("Should allow joining with valid and unexpired code")
  void testJoinHouseholdWithValidCode() {
    HouseholdJoinCode joinCode = new HouseholdJoinCode("ABC123", testHousehold,
        new Date(System.currentTimeMillis() + 10000));

    when(householdJoinCodeRepo.findByCode("ABC123")).thenReturn(Optional.of(joinCode));
    when(householdMembersRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Household result = householdService.joinHousehold("ABC123", testUser);

    assertNotNull(result);
    assertEquals("TestHouse", result.getName());
    verify(householdMembersRepo).save(any(HouseholdMembers.class));
  }

  @Test
  @DisplayName("Should not join with expired join code")
  void testJoinHouseholdWithExpiredCode() {
    HouseholdJoinCode expiredCode = new HouseholdJoinCode("XYZ999", testHousehold,
        new Date(System.currentTimeMillis() - 1000));

    when(householdJoinCodeRepo.findByCode("XYZ999")).thenReturn(Optional.of(expiredCode));

    Household result = householdService.joinHousehold("XYZ999", testUser);

    assertNull(result);
  }

  @Test
  @DisplayName("Should not join with empty join code")
  void testJoinHouseholdWithEmptyCode() {
    when(householdJoinCodeRepo.findByCode("")).thenReturn(Optional.empty());
    when(householdJoinCodeRepo.findByCode(null)).thenReturn(Optional.empty());
    when(householdJoinCodeRepo.findByCode(" ")).thenReturn(Optional.empty());
    when(householdJoinCodeRepo.findByCode("  ")).thenReturn(Optional.empty());

    Household result = householdService.joinHousehold("", testUser);

    assertNull(result);
  }

  @Test
  @DisplayName("Should not join with invalid join code")
  void testJoinHouseholdWithInvalidCode() {
    when(householdJoinCodeRepo.findByCode("INVALID")).thenReturn(Optional.empty());

    Household result = householdService.joinHousehold("INVALID", testUser);

    assertNull(result);
  }

  @Test
  @DisplayName("Should add user to household and persist membership")
  void testAddUserToHousehold() {
    HouseholdJoinCode joinCode = new HouseholdJoinCode("ABC123", testHousehold,
        new Date(System.currentTimeMillis() + 10000));

    when(householdJoinCodeRepo.findByCode("ABC123")).thenReturn(Optional.of(joinCode));

    householdService.joinHousehold("ABC123", testUser);

    verify(householdMembersRepo).save(any(HouseholdMembers.class));
  }

  @Test
  @DisplayName("Should remove user from household if member exists")
  void testRemoveUserFromHouseholdSuccess() {
    HouseholdMembers member = new HouseholdMembers(testUser, testHousehold, false);

    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.of(member));

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class},
        new Object[]{testHousehold, testUser},
        "removeUserFromHousehold");

    verify(householdMembersRepo).delete(member);
  }

  @Test
  @DisplayName("Should do nothing when removing non-member user from household")
  void testRemoveUserFromHouseholdNotPresent() {
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.empty());

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class},
        new Object[]{testHousehold, testUser},
        "removeUserFromHousehold");

    verify(householdMembersRepo, never()).delete(any());
  }

  @Test
  @DisplayName("Should fail generating join code after max tries")
  void testGenerateJoinCodeFailsAfterMaxTries() {
    when(householdJoinCodeRepo.existsByCode(anyString())).thenReturn(true);
    when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(true);

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        householdService.generateJoinCode(testHousehold, testUser)
    );

    assertTrue(exception.getMessage().contains("Could not generate a unique join code"));
    verify(householdJoinCodeRepo, never()).save(any());
  }

  @Test
  @DisplayName("Should update the users memberships when adding admin to household")
  void testUpdateUserMembershipsWhenAdmin() {
    testUser.setHouseholdMemberships(new ArrayList<>());
    testHousehold.setMembers(new ArrayList<>());

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class, boolean.class},
        new Object[]{testHousehold, testUser, true},
        "addUserToHousehold");

    assertEquals(1, testUser.getHouseholdMemberships().size());
    assertEquals(testUser.getHouseholdMemberships().get(0).getHousehold(), testHousehold);
  }

  @Test
  @DisplayName("Should update the users memberships when adding to household")
  void testUpdateUserMemberships() {
    testUser.setHouseholdMemberships(new ArrayList<>());
    testHousehold.setMembers(new ArrayList<>());

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class},
        new Object[]{testHousehold, testUser},
        "addUserToHousehold");

    assertEquals(1, testUser.getHouseholdMemberships().size());
    assertEquals(testUser.getHouseholdMemberships().get(0).getHousehold(), testHousehold);
  }

  @Test
  @DisplayName("Should update the household members when adding a user")
  void testUpdateMembersWhenAddingUser() {
    testUser.setHouseholdMemberships(new ArrayList<>());
    testHousehold.setMembers(new ArrayList<>());

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class},
        new Object[]{testHousehold, testUser},
        "addUserToHousehold");

    assertEquals(1, testHousehold.getMembers().size());
    assertTrue(testHousehold.getMembers().get(0).getUser().equals(testUser));
  }

  @Test
  @DisplayName("Should update the household members when adding an admin user")
  void testUpdateMembersWhenAddingAdminUser() {
    testUser.setHouseholdMemberships(new ArrayList<>());
    testHousehold.setMembers(new ArrayList<>());

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class, boolean.class},
        new Object[]{testHousehold, testUser, true},
        "addUserToHousehold");

    assertEquals(1, testHousehold.getMembers().size());
    assertTrue(testHousehold.getMembers().get(0).getUser().equals(testUser));
  }

  @Test
  @DisplayName("Should not update the household members when adding a user that already exists")
  void testUpdateMembersWhenAddingExistingUser() {
    testUser.setHouseholdMemberships(new ArrayList<>());
    testHousehold.setMembers(new ArrayList<>());

    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold))
        .thenReturn(true);
    HouseholdMembers existingMember = new HouseholdMembers(testUser, testHousehold, false);
    testHousehold.getMembers().add(existingMember);

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class},
        new Object[]{testHousehold, testUser},
        "addUserToHousehold");

    verify(userRepo, never()).save(testUser);
    assertEquals(1, testHousehold.getMembers().size());
  }

  @Test
  @DisplayName("Should not update the household members when adding an admin user that already exists")
  void testUpdateMembersWhenAddingExistingAdminUser() {
    testUser.setHouseholdMemberships(new ArrayList<>());
    testHousehold.setMembers(new ArrayList<>());

    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold))
        .thenReturn(true);
    HouseholdMembers existingMember = new HouseholdMembers(testUser, testHousehold, true);
    testHousehold.getMembers().add(existingMember);

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class, boolean.class},
        new Object[]{testHousehold, testUser, true},
        "addUserToHousehold");

    verify(userRepo, never()).save(testUser);
    assertEquals(1, testHousehold.getMembers().size());
  }

  @Test
  @DisplayName("Added admin user should have isAdmin set to true")
  void testAdminUserIsAdmin(){
    HouseholdMembers member = new HouseholdMembers(testUser, testHousehold, true);
    when(householdMembersRepo.save(any(HouseholdMembers.class))).thenReturn(member);
    householdService.addUserToHousehold(testHousehold, testUser, true);
    assertTrue(member.isAdmin());
    assertTrue(testHousehold.getMembers().get(0).isAdmin());
  }

}
