package org.ntnu.idatt2106.backend.controller;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.*;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.HouseholdMembersRepo;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.HouseholdService;

import org.ntnu.idatt2106.backend.service.EssentialItemService;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HouseholdControllerTest {

    @InjectMocks
    private HouseholdController householdController;

    @Mock
    private PreparednessService preparednessService;

    @Mock
    private EssentialItemService essentialItemService;

    @Mock
    private HouseholdRepo householdRepo;

    @Mock
    private HouseholdService householdService;

    @Mock
    private JWT_token jwtTokenService;

    @Mock
    private HouseholdMembersRepo householdMembersRepo;

    private final String validToken = "Bearer valid.jwt.token";
    private final User testUser = new User("user@test.com", "pass", "Test", "User", "12345678");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    void assertUnauthorizedCases(TokenRequestHandler handler, Object... extraArgs) {
        // Case 1: Missing token
        Object[] argsMissing = prepend(null, extraArgs);
        ResponseEntity<?> responseMissing = handler.apply(argsMissing);
        assertEquals(HttpStatus.UNAUTHORIZED, responseMissing.getStatusCode());
        assertEquals("Error: Unauthorized", responseMissing.getBody());

        // Case 2: Malformed token
        Object[] argsInvalid = prepend("InvalidToken", extraArgs);
        ResponseEntity<?> responseInvalid = handler.apply(argsInvalid);
        assertEquals(HttpStatus.UNAUTHORIZED, responseInvalid.getStatusCode());
        assertEquals("Error: Unauthorized", responseInvalid.getBody());

        // Case 3: Valid token, but user is null
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(null);
        Object[] argsNullUser = prepend("Bearer valid.jwt.token", extraArgs);
        ResponseEntity<?> responseNullUser = handler.apply(argsNullUser);
        assertEquals(HttpStatus.UNAUTHORIZED, responseNullUser.getStatusCode());
        assertEquals("Error: Unauthorized", responseNullUser.getBody());
    }

    private Object[] prepend(Object token, Object... args) {
        Object[] result = new Object[args.length + 1];
        result[0] = token;
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }




    @Test
    @DisplayName("Should return preparedness status from token")
    void testGetPreparednessStatusSuccess() {
        String token = "Bearer dummy.jwt.token";
        int userId = 42;
        MyHouseholdStatusGetResponse mockStatus = new MyHouseholdStatusGetResponse(1, "MyHouse", new PreparednessStatus(6.0, 4.0));

        when(jwtTokenService.extractIdFromJwt("dummy.jwt.token")).thenReturn(String.valueOf(userId));
        when(preparednessService.getPreparednessStatusByUserId(userId)).thenReturn(List.of(mockStatus));

        ResponseEntity<?> response = householdController.getPreparednessStatus(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(mockStatus), response.getBody());
    }

    @Test
    @DisplayName("Should return 404 if user has no households")
    void testGetPreparednessStatusUserNotFound() {
        String token = "Bearer abc.def.ghi";

        when(jwtTokenService.extractIdFromJwt("abc.def.ghi")).thenReturn("99");
        when(preparednessService.getPreparednessStatusByUserId(99)).thenThrow(new NoSuchElementException("User not found"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: User not found", response.getBody());
    }

    @Test
    @DisplayName("Should return 500 on unknown error in preparedness")
    void testGetPreparednessStatusInternalError() {
        String token = "Bearer xyz.token";
        when(jwtTokenService.extractIdFromJwt("xyz.token")).thenReturn("123");
        when(preparednessService.getPreparednessStatusByUserId(123)).thenThrow(new RuntimeException("Boom"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: Boom", response.getBody());
    }

    @Test
    @DisplayName("Should return essential items status from token")
    void testGetEssentialItemsStatusSuccess() {
        String token = "Bearer real.jwt.token";
        int householdId = 1;

        List<EssentialItemStatusDTO> mockItems = List.of(
            new EssentialItemStatusDTO("grill", true),
            new EssentialItemStatusDTO("jodtabletter", false)
        );

        when(jwtTokenService.getUserByToken("real.jwt.token")).thenReturn(new User());
        when(essentialItemService.getEssentialItemStatus(householdId)).thenReturn(mockItems);

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(token, householdId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockItems, response.getBody());
    }

    @Test
    @DisplayName("getEssentialItemStatus unauthorized cases")
    void testGetEssentialItemStatusUnauthorized() {
        assertUnauthorizedCases(
                args -> householdController.getEssentialItemsStatus((String) args[0], (int) args[1]), 1
        );
    }

    @Test
    @DisplayName("Should return 401 when token is invalid or user not found")
    void testCreateHouseholdUnauthorized() {
        String token = "Bearer invalid";
        HouseholdCreate dto = new HouseholdCreate("CakeHouse", 10.0, 20.0);

        when(jwtTokenService.getUserByToken("invalid")).thenReturn(null);

        ResponseEntity<?> response = householdController.createHousehold(token, dto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: Unauthorized", response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when invalid household is provided")
    void testCreateHouseholdBadRequest() {
        String token = "Bearer valid.token";
        HouseholdCreate dto = new HouseholdCreate("HouseOfMazino", 10.0, 20.0);

        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());

        doThrow(new IllegalArgumentException("Invalid household details")).when(householdService).createHousehold(dto);

        ResponseEntity<?> response = householdController.createHousehold(token, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Invalid household details", response.getBody());
    }

    @Test
    @DisplayName("Should return join code if authorized and household exists")
    void testCreateInviteSuccess() {
        String token = "Bearer abc.token";
        User user = new User();
        Household household = new Household();
        when(jwtTokenService.getUserByToken("abc.token")).thenReturn(user);
        when(householdRepo.findById(1)).thenReturn(Optional.of(household));
        when(householdMembersRepo.existsByUserAndHousehold(user, household)).thenReturn(true);
        when(householdService.generateJoinCode(household,user)).thenReturn("JOIN123");

        ResponseEntity<?> response = householdController.createInvite(token, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("JOIN123", response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when household not found during invite")
    void testCreateInviteNotFound() {
        String token = "Bearer abc.token";
        when(jwtTokenService.getUserByToken("abc.token")).thenReturn(new User());
        when(householdRepo.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = householdController.createInvite(token, 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: No value present", response.getBody());
    }

    @Test
    @DisplayName("Should return 401 when unauthorized to create invite")
    void testCreateInviteUnauthorized() {
        String token = "Bearer bad.token";
        when(jwtTokenService.getUserByToken("bad.token")).thenReturn(null);

        ResponseEntity<?> response = householdController.createInvite(token, 1);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: Unauthorized", response.getBody());
    }

    @Test
    @DisplayName("Should call joinHousehold if authorized")
    void testJoinHouseholdSuccess() {
        String token = "Bearer join.token";
        User user = new User();

        when(jwtTokenService.getUserByToken("join.token")).thenReturn(user);

        assertDoesNotThrow(() -> householdController.joinHouseHold(token, "JOIN123"));
        verify(householdService).joinHousehold("JOIN123", user);
    }

    @Test
    @DisplayName("Should not call joinHousehold if unauthorized")
    void testJoinHouseholdUnauthorized() {
        String token = "Bearer fake.token";
        when(jwtTokenService.getUserByToken("fake.token")).thenReturn(null);

        householdController.joinHouseHold(token, "JOIN123");

        verify(householdService, never()).joinHousehold(anyString(), any());
    }

    @Test
    @DisplayName("Should handle NoSuchElementException when joining household")
    void testJoinHouseholdNotFound() {
        String token = "Bearer join.token";
        User user = new User();

        when(jwtTokenService.getUserByToken("join.token")).thenReturn(user);
        doThrow(new NoSuchElementException()).when(householdService).joinHousehold("JOIN123", user);

        householdController.joinHouseHold(token, "JOIN123");

        verify(householdService).joinHousehold("JOIN123", user);
    }

    @Test
    @DisplayName("Should return 400 when household not found during join")
    void testShouldReturn404WhenNotFound() {
        String token = "joketoken";
        User user = new User();

        when(jwtTokenService.getUserByToken(token)).thenReturn(user);
        when(householdService.joinHousehold("JOIN123", user)).thenThrow(new NoSuchElementException("Household not found"));

        ResponseEntity<?> response = householdController.joinHouseHold(token, "JOIN123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("User can leave household if they are a member")
    void testUserCanLeaveHousehold() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        when(householdRepo.findById(1)).thenReturn(Optional.of(new Household()));
        when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(true);
        when(householdMembersRepo.findByUserAndHousehold(any(), any())).thenReturn(Optional.of(new HouseholdMembers()));

        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully left the household", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave household if they are not a member")
    void testUserCannotLeaveHouseholdNotMember() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        when(householdRepo.findById(1)).thenReturn(Optional.of(new Household()));
        when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(false);
        doThrow(new NoSuchElementException("User not found in household")).when(householdService).leaveHousehold(eq(1), any());

        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: User not found in household", response.getBody());
    }
    @Test
    @DisplayName("User cannot leave household if household does not exist")
    void testUserCannotLeaveHouseholdNotFound() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());

        doThrow(new NoSuchElementException("Household not found")).when(householdService).leaveHousehold(eq(1), any());

        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("User can kick another user from household if they are the admin")
    void testUserCanKickFromHousehold() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        when(householdRepo.findById(1)).thenReturn(Optional.of(new Household()));
        when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(true);
        when(householdMembersRepo.findByUserAndHousehold(any(), any())).thenReturn(Optional.of(new HouseholdMembers()));

        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User successfully kicked from the household", response.getBody());
    }

    @Test
    @DisplayName("User cannot kick another user from household if they are not the admin")
    void testUserCannotKickFromHouseholdNotAdmin() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        when(householdRepo.findById(1)).thenReturn(Optional.of(new Household()));
        when(householdMembersRepo.existsByUserAndHousehold(any(), any())).thenReturn(false);

        doThrow(new UnauthorizedException("User not authorized to kick this user from the household"))
            .when(householdService).kickUserFromHousehold(eq(1), eq(1), any());
        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error: User not authorized to kick this user from the household", response.getBody());
    }

    @Test
    @DisplayName("User cannot kick from household if household does not exist")
    void testUserCannotKickFromHouseholdNotFound() {
        User admin = new User();
        when(jwtTokenService.getUserByToken(any())).thenReturn(admin);
        doThrow(new NoSuchElementException("Household not found"))
            .when(householdService).kickUserFromHousehold(eq(1), eq(1), any());

        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot kick from household if user does not exist")
    void testUserCannotKickFromHouseholdUserNotFound() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());

        doThrow(new NoSuchElementException("Household or user not found"))
            .when(householdService).kickUserFromHousehold(eq(1), eq(1), any());
        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household or user not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot kick from household if the user is not a member")
    void testUserCannotKickFromHouseholdNotMember() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        doThrow(new IllegalArgumentException("User not found in household"))
            .when(householdService).kickUserFromHousehold(eq(1), eq(1), any());
        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User not found in household", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave non existent household")
    void testUserCannotLeaveNonExistentHousehold() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        doThrow(new NoSuchElementException("Household not found"))
            .when(householdService).leaveHousehold(eq(1), any());
        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave household with invalid token")
    void testUserCannotLeaveInvalidToken() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(null);
        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: User not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave household with no token")
    void testUserCannotLeaveNoToken() {
        when(jwtTokenService.getUserByToken("")).thenReturn(null);
        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("", 1);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: User not found", response.getBody());
    }

    @Test
    @DisplayName("Should return households when token is valid and user has households")
    void testGetMyHouseholdsSuccess() {
        User user = new User();
        List<HouseholdRequest> households = List.of(new HouseholdRequest());

        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(user);
        when(householdService.getHouseholdsByUser(user)).thenReturn(households);

        ResponseEntity<?> response = householdController.getMyHouses("Bearer valid.token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(households, response.getBody());
    }

    @Test
    @DisplayName("getMyHouses unauthorized cases")
    void testGetMyHousesUnauthorized() {
        assertUnauthorizedCases(
                args -> householdController.getMyHouses((String) args[0])
        );
    }


    @Test
    @DisplayName("Should return 404 when no households are found for user")
    void testGetMyHouseholdsNotFound() {
        User user = new User();

        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(user);
        when(householdService.getHouseholdsByUser(user)).thenThrow(new NoSuchElementException("No households found for this user"));

        ResponseEntity<?> response = householdController.getMyHouses("Bearer valid.token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: No households found for this user", response.getBody());
    }

    @Test
    @DisplayName("Should return 500 if unexpected exception occurs")
    void testGetMyHouseholdsInternalError() {
        when(jwtTokenService.getUserByToken("valid.token")).thenThrow(new RuntimeException("Could not retrieve households"));

        ResponseEntity<?> response = householdController.getMyHouses("Bearer valid.token");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: Could not retrieve households", response.getBody());
    }

    @Test
    @DisplayName("getPrimaryHousehold should return 200 OK with primary household")
    void testGetPrimaryHouseholdSuccess() {
        HouseholdMinimalGetResponse mockHousehold = new HouseholdMinimalGetResponse(1, "house");
        mockHousehold.setId(1);

        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);
        when(householdService.getPrimary(testUser)).thenReturn(mockHousehold);

        ResponseEntity<?> response = householdController.getPrimaryHousehold(validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockHousehold, response.getBody());
    }

    @Test
    @DisplayName("getMyHouses unauthorized cases")
    void testGetPrimaryUnauthorized() {
        assertUnauthorizedCases(
                args -> householdController.getMyHouses((String) args[0])
        );
    }


    @Test
    @DisplayName("getPrimaryHousehold should return 404 if user not found")
    void testGetPrimaryHouseholdUserNotFound() {
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);
        when(householdService.getPrimary(testUser)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<?> response = householdController.getPrimaryHousehold(validToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: User not found", response.getBody());
    }

    @Test
    @DisplayName("getPrimaryHousehold should return 500 if unexpected exception occurs")
    void testGetPrimaryHouseholdUnexpectedError() {
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);
        when(householdService.getPrimary(testUser)).thenThrow(new RuntimeException("Unexpected fail"));

        ResponseEntity<?> response = householdController.getPrimaryHousehold(validToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: Unexpected fail", response.getBody());
    }

    @Test
    @DisplayName("setPrimaryHousehold should return OK when token is valid and user is found")
    void testSetPrimaryHouseholdSuccess() {
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);

        ResponseEntity<?> response = householdController.setPrimaryHousehold(validToken, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        verify(householdService).setPrimary(1, testUser);
    }

    @Test
    @DisplayName("setPrimaryHousehold unauthorized cases")
    void testSetPrimaryHouseholdUnauthorized() {
        assertUnauthorizedCases(
                args -> householdController.setPrimaryHousehold((String) args[0], (int) args[1]),
                1 // householdId
        );
    }


    @Test
    @DisplayName("setPrimaryHousehold should return NOT_FOUND when user is not in household")
    void testSetPrimaryHouseholdNotFound() {
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);
        doThrow(new NoSuchElementException("Household not found")).when(householdService).setPrimary(1, testUser);

        ResponseEntity<?> response = householdController.setPrimaryHousehold(validToken, 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("setPrimaryHousehold should return BAD_REQUEST when input is invalid")
    void testSetPrimaryHouseholdBadRequest() {
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);
        doThrow(new IllegalArgumentException("Missing householdId")).when(householdService).setPrimary(1, testUser);

        ResponseEntity<?> response = householdController.setPrimaryHousehold(validToken, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Missing householdId", response.getBody());
    }

    @Test
    @DisplayName("setPrimaryHousehold should return INTERNAL_SERVER_ERROR on unexpected exception")
    void testSetPrimaryHouseholdUnexpectedError() {
        when(jwtTokenService.getUserByToken("valid.jwt.token")).thenReturn(testUser);
        doThrow(new RuntimeException("Database unreachable")).when(householdService).setPrimary(1, testUser);

        ResponseEntity<?> response = householdController.setPrimaryHousehold(validToken, 1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: Database unreachable", response.getBody());
    }

}
