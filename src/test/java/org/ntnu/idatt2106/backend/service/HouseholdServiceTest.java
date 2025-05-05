package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.HouseholdCreate;
import org.ntnu.idatt2106.backend.dto.household.HouseholdRequest;
import org.ntnu.idatt2106.backend.dto.user.UserPositionResponse;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
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
  private HouseholdJoinCodeRepo joinCodeRepo;

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
    testUser = new User(1,"test@example.com", "password123", "Ape", "Apemann", "69420420",
        10.0, 20.0, true);
    testUser.setPositionUpdateTime(new Date(1850000000000L));
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
    when(joinCodeRepo.existsByCode(anyString())).thenReturn(false);
    when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(true);
    String code = householdService.generateJoinCode(testHousehold, testUser);

    assertNotNull(code);
    assertEquals(8, code.length());
    verify(joinCodeRepo).save(any(HouseholdJoinCode.class));
  }

  @Test
  @DisplayName("Should not generate a join code if user is not a member")
  void testGenerateJoinCodeForNonMember() {
    when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(false);

    assertThrows(UnauthorizedException.class, () -> {
      householdService.generateJoinCode(testHousehold, testUser);
    });
    verify(joinCodeRepo, never()).save(any(HouseholdJoinCode.class));
  }

  @Test
  @DisplayName("Should allow joining with valid and unexpired code")
  void testJoinHouseholdWithValidCode() {
    HouseholdJoinCode joinCode = new HouseholdJoinCode("ABC123", testHousehold,
        new Date(System.currentTimeMillis() + 10000));

    when(joinCodeRepo.findByCode("ABC123")).thenReturn(Optional.of(joinCode));
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

    when(joinCodeRepo.findByCode("XYZ999")).thenReturn(Optional.of(expiredCode));

    Household result = householdService.joinHousehold("XYZ999", testUser);

    assertNull(result);
  }

  @Test
  @DisplayName("Should not join with empty join code")
  void testJoinHouseholdWithEmptyCode() {
    when(joinCodeRepo.findByCode("")).thenReturn(Optional.empty());
    when(joinCodeRepo.findByCode(null)).thenReturn(Optional.empty());
    when(joinCodeRepo.findByCode(" ")).thenReturn(Optional.empty());
    when(joinCodeRepo.findByCode("  ")).thenReturn(Optional.empty());

    Household result = householdService.joinHousehold("", testUser);

    assertNull(result);
  }

  @Test
  @DisplayName("Should not join with invalid join code")
  void testJoinHouseholdWithInvalidCode() {
    when(joinCodeRepo.findByCode("INVALID")).thenReturn(Optional.empty());

    Household result = householdService.joinHousehold("INVALID", testUser);

    assertNull(result);
  }

  @Test
  @DisplayName("Should add user to household and persist membership")
  void testAddUserToHousehold() {
    HouseholdJoinCode joinCode = new HouseholdJoinCode("ABC123", testHousehold,
        new Date(System.currentTimeMillis() + 10000));

    when(joinCodeRepo.findByCode("ABC123")).thenReturn(Optional.of(joinCode));

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
    when(joinCodeRepo.existsByCode(anyString())).thenReturn(true);
    when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(true);

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        householdService.generateJoinCode(testHousehold, testUser)
    );

    assertTrue(exception.getMessage().contains("Could not generate a unique join code"));
    verify(joinCodeRepo, never()).save(any());
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

  @Test
  @DisplayName("Private verifyTokenNotTaken should return true for unused code")
  void testPrivateVerifyTokenNotTaken() {
    when(joinCodeRepo.existsByCode("UNUSED")).thenReturn(false);

    boolean result = TestUtils.callPrivateMethod(householdService,
        new Class[]{String.class},
        new Object[]{"UNUSED"},
        "verifyTokenNotTaken");

    assertTrue(result);
  }

  @Test
  @DisplayName("Private verifyTokenNotTaken should return false for used code")
  void testPrivateVerifyTokenIsTaken() {
    when(joinCodeRepo.existsByCode("USED")).thenReturn(true);

    boolean result = TestUtils.callPrivateMethod(householdService,
        new Class[]{String.class},
        new Object[]{"USED"},
        "verifyTokenNotTaken");

    assertFalse(result);
  }

  @Test
  @DisplayName("Private verifyTokenNotExpired should return true for valid token")
  void testPrivateVerifyTokenNotExpired() {
    HouseholdJoinCode validCode = new HouseholdJoinCode("VALID", testHousehold,
        new Date(System.currentTimeMillis() + 10000));

    boolean result = TestUtils.callPrivateMethod(householdService,
        new Class[]{HouseholdJoinCode.class},
        new Object[]{validCode},
        "verifyTokenNotExpired");

    assertTrue(result);
  }

  @Test
  @DisplayName("Private verifyTokenNotExpired should return false for expired token")
  void testPrivateVerifyTokenExpired() {
    HouseholdJoinCode expiredCode = new HouseholdJoinCode("EXPIRED", testHousehold,
        new Date(System.currentTimeMillis() - 10000));

    boolean result = TestUtils.callPrivateMethod(householdService,
        new Class[]{HouseholdJoinCode.class},
        new Object[]{expiredCode},
        "verifyTokenNotExpired");

    assertFalse(result);
  }

  @Test
  @DisplayName("Private verifyTokenNotExpired should return false for null token")
  void testPrivateVerifyTokenNull() {
    boolean result = TestUtils.callPrivateMethod(householdService,
        new Class[]{String.class},
        new Object[]{null},
        "verifyTokenNotExpired");

    assertFalse(result);
  }

  @Test
  @DisplayName("Private getHouseholdByCode should return household for valid code")
  void testPrivateGetHouseholdByValidCode() {
    HouseholdJoinCode joinCode = new HouseholdJoinCode("VALID", testHousehold,
        new Date(System.currentTimeMillis() + 10000));
    when(joinCodeRepo.findByCode("VALID")).thenReturn(Optional.of(joinCode));

    Household result = TestUtils.callPrivateMethod(householdService,
        new Class[]{String.class},
        new Object[]{"VALID"},
        "getHouseholdByCode");

    assertEquals(testHousehold, result);
  }

  @Test
  @DisplayName("Private getHouseholdByCode should return null for invalid code")
  void testPrivateGetHouseholdByInvalidCode() {
    when(joinCodeRepo.findByCode("INVALID")).thenReturn(Optional.empty());

    Household result = TestUtils.callPrivateMethod(householdService,
        new Class[]{String.class},
        new Object[]{"INVALID"},
        "getHouseholdByCode");

    assertNull(result);
  }

  @Test
  @DisplayName("Private removeUserFromHousehold should remove existing member")
  void testPrivateRemoveUserFromHousehold() {
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
  @DisplayName("Private removeUserFromHousehold should handle non-existent member")
  void testPrivateRemoveNonExistentMember() {
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.empty());

    TestUtils.callPrivateMethod(householdService,
        new Class[]{Household.class, User.class},
        new Object[]{testHousehold, testUser},
        "removeUserFromHousehold");

    verify(householdMembersRepo, never()).delete(any());
  }

  @Test
  @DisplayName("Private verifyUserInHousehold should pass for existing member")
  void testPrivateVerifyUserInHousehold() {
    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold))
        .thenReturn(true);

    assertDoesNotThrow(() -> {
      TestUtils.callPrivateMethod(householdService,
          new Class[]{Household.class, User.class},
          new Object[]{testHousehold, testUser},
          "verifyUserInHousehold");
    });
  }

  @Test
  @DisplayName("Private generateRandomCode should generate code of correct length")
  void testPrivateGenerateRandomCodeLength() {
    String code = TestUtils.callPrivateMethod(householdService,
        new Class[]{int.class},
        new Object[]{8},
        "generateRandomCode");

    assertNotNull(code);
    assertEquals(8, code.length());
  }

  @Test
  @DisplayName("Private generateRandomCode should generate alphanumeric code")
  void testPrivateGenerateRandomCodeCharacters() {
    String code = TestUtils.callPrivateMethod(householdService,
        new Class[]{int.class},
        new Object[]{8},
        "generateRandomCode");

    assertTrue(code.matches("[A-Z0-9]+"));
  }

  @Test
  @DisplayName("Should allow regular user to leave household")
  void testLeaveHouseholdRegularUser() {
    HouseholdMembers member = new HouseholdMembers(testUser, testHousehold, false);
    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold))
        .thenReturn(true);
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.of(member));

    householdService.leaveHousehold(testHousehold, testUser);

    verify(householdMembersRepo).delete(member);
    verify(householdRepo).save(testHousehold);
  }

  @Test
  @DisplayName("Should prevent admin from leaving household")
  void testLeaveHouseholdAdminUser() {
    HouseholdMembers adminMember = new HouseholdMembers(testUser, testHousehold, true);
    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold))
        .thenReturn(true);
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.of(adminMember));

    assertThrows(IllegalArgumentException.class, () -> {
      householdService.leaveHousehold(testHousehold, testUser);
    });

    verify(householdMembersRepo, never()).delete(any());
  }

  @Test
  @DisplayName("Should throw when user not in household tries to leave")
  void testLeaveHouseholdNonMember() {
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> {
      householdService.leaveHousehold(testHousehold, testUser);
    });
  }

  @Test
  @DisplayName("Should allow admin to kick user by IDs")
  void testKickUserFromHouseholdByIds() {
    User adminUser = new User(2, "admin@test.com", "password", "Admin", "User", "12345678");
    User targetUser = new User(3, "target@test.com", "password", "Target", "User", "87654321");

    when(householdRepo.findById(1)).thenReturn(Optional.of(testHousehold));
    when(userRepo.findById(3)).thenReturn(Optional.of(targetUser));
    when(userRepo.findById(2)).thenReturn(Optional.of(adminUser));
    when(householdMembersRepo.existsByUserAndHousehold(adminUser, testHousehold)).thenReturn(true);
    when(householdMembersRepo.existsByUserAndHousehold(targetUser, testHousehold)).thenReturn(true);
    when(householdMembersRepo.findByUserAndHousehold(adminUser, testHousehold))
        .thenReturn(Optional.of(new HouseholdMembers(adminUser, testHousehold, true)));
    when(householdMembersRepo.findByUserAndHousehold(targetUser, testHousehold))
        .thenReturn(Optional.of(new HouseholdMembers(targetUser, testHousehold, false)));

    householdService.kickUserFromHousehold(1, 3, adminUser);

    verify(householdMembersRepo).delete(any(HouseholdMembers.class));
  }

  @Test
  @DisplayName("Should throw when household not found by ID")
  void testKickUserFromNonExistentHousehold() {
    when(householdRepo.findById(9)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> {
      householdService.kickUserFromHousehold(9, 1, testUser);
    });
  }

  @Test
  @DisplayName("Should throw when target user not found by ID")
  void testKickNonExistentUser() {
    when(householdRepo.findById(1)).thenReturn(Optional.of(testHousehold));
    when(userRepo.findById(9)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> {
      householdService.kickUserFromHousehold(1, 9, testUser);
    });
  }

  @Test
  @DisplayName("Should verify admin can kick user")
  void testVerifyCanKickUserAdmin() {
    User adminUser = new User("admin@test.com", "password", "Admin", "User", "12345678");
    HouseholdMembers adminMember = new HouseholdMembers(adminUser, testHousehold, true);
    HouseholdMembers targetMember = new HouseholdMembers(testUser, testHousehold, false);

    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold)).thenReturn(true);
    when(householdMembersRepo.findByUserAndHousehold(adminUser, testHousehold))
        .thenReturn(Optional.of(adminMember));

    assertDoesNotThrow(() -> {
      TestUtils.callPrivateMethod(householdService,
          new Class[]{Household.class, User.class, User.class},
          new Object[]{testHousehold, testUser, adminUser},
          "verifyCanKickUserFromHousehold");
    });
  }

  @Test
  @DisplayName("Should allow admin to kick user")
  void testKickUserFromHousehold() {
    User adminUser = new User("admin@test.com", "password", "Admin", "User", "12345678");
    HouseholdMembers adminMember = new HouseholdMembers(adminUser, testHousehold, true);
    HouseholdMembers targetMember = new HouseholdMembers(testUser, testHousehold, false);

    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold)).thenReturn(true);
    when(householdMembersRepo.existsByUserAndHousehold(adminUser, testHousehold)).thenReturn(true);

    when(householdMembersRepo.findByUserAndHousehold(adminUser, testHousehold))
        .thenReturn(Optional.of(adminMember));
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold))
        .thenReturn(Optional.of(targetMember));

    householdService.kickUserFromHousehold(testHousehold, testUser, adminUser);

    verify(householdMembersRepo).delete(targetMember);
    verify(householdRepo).save(testHousehold);
  }

  @Test
  @DisplayName("getHouseholdsByUser should return households for user")
  void testGetHouseholdsByUser() {
    testUser.getHouseholdMemberships().add(new HouseholdMembers(testUser, testHousehold, false));
    List<HouseholdRequest> households = householdService.getHouseholdsByUser(testUser);
    assertEquals(1, households.size());
    assertEquals(testHousehold.getId(), households.get(0).getId());
    assertEquals(testHousehold.getName(), households.get(0).getName());
  }

  @Test
  @DisplayName("getHouseholdsByUser should return empty list for user with no households")
  void testGetHouseholdsByUserNoHouseholds() {
    when(householdMembersRepo.findByUser(testUser)).thenReturn(new ArrayList<>());

    List<HouseholdRequest> households = householdService.getHouseholdsByUser(testUser);
    assertTrue(households.isEmpty());
  }

  @Test
  @DisplayName("getHouseholdsByUser should throw illegalargument for null user")
  void testGetHouseholdsByUserNullUser() {
    assertThrows(IllegalArgumentException.class, () -> {
      householdService.getHouseholdsByUser(null);
    });
  }

  @Test
  @DisplayName("getUserPositions should return positions for users in the household")
  void testGetUserPositionShouldReturnCorrectPosition() {
    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold)).thenReturn(true);
    HouseholdMembers member = new HouseholdMembers(testUser, testHousehold, false);
    when(householdMembersRepo.findByUser(testUser)).thenReturn(List.of(member));
    when(householdMembersRepo.findByUserAndHousehold(testUser, testHousehold)).thenReturn(Optional.of(member));
    when(householdMembersRepo.findAllByHousehold(testHousehold)).thenReturn(List.of(member));

    List<UserPositionResponse> result = householdService.getUserPositions(testHousehold, testUser);

    assertEquals(1, result.size());
    UserPositionResponse response = result.get(0);
    assertEquals(10.0, response.getLatitude());
    assertEquals(20.0, response.getLongitude());
    assertEquals("2028-08-16T02:53", response.getPositionUpdateTime());
    assertEquals(1, response.getId());
    assertEquals("Ape Apemann", response.getName());
  }

  @Test
  @DisplayName("getUserPositions should throw exception for non-member user")
  void testGetUserPositionsThrowsExceptionWhenNonMember() {
    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold)).thenReturn(false);

    assertThrows(UnauthorizedException.class, () ->
        householdService.getUserPositions(testHousehold, testUser));
  }

  @Test
  @DisplayName("getUserPositions should return correct positions for household ID and user")
  void testGetUserPositionsShouldReturnCorrectWithId() {
    when(householdRepo.findById(42)).thenReturn(Optional.of(testHousehold));
    when(householdMembersRepo.existsByUserAndHousehold(testUser, testHousehold)).thenReturn(true);
    HouseholdMembers member = new HouseholdMembers(testUser, testHousehold, false);
    when(householdMembersRepo.findAllByHousehold(testHousehold)).thenReturn(List.of(member));

    List<UserPositionResponse> result = householdService.getUserPositions(42, testUser);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("getUserPositions should throw exception for non-existent household ID")
  void testGetUserPositionsHouseholdIdAndUserThrowsIdNotFound() {
    when(householdRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () ->
        householdService.getUserPositions(99, testUser));
  }

  @Test
  @DisplayName("getUserPositions should return correct positions for user only")
  void testGetUserPositionsShouldReturnCorrectForUserOnly() {
    HouseholdMembers memberInHousehold = mock(HouseholdMembers.class);
    Household anotherHousehold = mock(Household.class);

    HouseholdMembers member = new HouseholdMembers(testUser, anotherHousehold, false);
    testUser.setHouseholdMemberships(List.of(memberInHousehold, member));
    when(householdMembersRepo.existsByUserAndHousehold(testUser, anotherHousehold)).thenReturn(true);
    when(householdMembersRepo.findByUser(testUser)).thenReturn(List.of(memberInHousehold, member));
    when(householdMembersRepo.findAllByHousehold(anotherHousehold)).thenReturn(List.of(member));
    List<UserPositionResponse> result = householdService.getUserPositions(testUser);
    assertEquals(1, result.size());
    UserPositionResponse response = result.get(0);
    assertEquals(10.0, response.getLatitude());
    assertEquals(20.0, response.getLongitude());
    assertEquals("2028-08-16T02:53", response.getPositionUpdateTime());
    assertEquals(1, response.getId());
    assertEquals("Ape Apemann", response.getName());
  }

  @Test
  @DisplayName("getUserPositions should throw exception for non-existent user")
  void testGetUserPositionsThrowsExceptionWhenNullUser() {
    assertThrows(IllegalArgumentException.class, () ->
        householdService.getUserPositions((User) null));
  }

  @Test
  @DisplayName("getUserPositions should throw exception for null household or user")
  void testGetUserPositionsThrowsExceptionWhenNullHouseholdOrUser() {
    assertThrows(IllegalArgumentException.class, () ->
        householdService.getUserPositions(null, testUser));

    assertThrows(IllegalArgumentException.class, () ->
        householdService.getUserPositions(testHousehold, null));
  }

}
