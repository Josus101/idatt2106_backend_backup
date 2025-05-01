package org.ntnu.idatt2106.backend.controller;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.HouseholdCreate;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.HouseholdMembersRepo;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.HouseholdService;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HouseholdControllerTest {

    @InjectMocks
    private HouseholdController householdController;

    @Mock
    private PreparednessService preparednessService;

    @Mock
    private HouseholdRepo householdRepo;

    @Mock
    private HouseholdService householdService;

    @Mock
    private JWT_token jwtTokenService;

    @Mock
    private HouseholdMembersRepo householdMembersRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns status for valid household ID")
    void testGetPreparednessStatusSuccess() {
        int householdId = 1;
        PreparednessStatus status = new PreparednessStatus(80, false, "Good");

        when(preparednessService.getPreparednessStatusByHouseholdId(householdId)).thenReturn(status);

        ResponseEntity<?> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns 404 if household not found")
    void testGetPreparednessStatusNotFound() {
        int householdId = 999;

        when(preparednessService.getPreparednessStatusByHouseholdId(householdId))
                .thenThrow(new NoSuchElementException("Household not found"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns 500 on general exception")
    void testGetPreparednessStatusInternalError() {
        int householdId = 123;

        when(preparednessService.getPreparednessStatusByHouseholdId(householdId))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error: Could not fetch preparedness status", response.getBody());
    }


    @Test
    @DisplayName("Should create household with valid token")
    void testCreateHouseholdSuccess() {
        String token = "Bearer abc.def.ghi";
        User user = new User();
        HouseholdCreate dto = new HouseholdCreate("ApeHouse", 10.0, 20.0);
        Household created = new Household();

        when(jwtTokenService.getUserByToken("abc.def.ghi")).thenReturn(user);
        when(householdService.createHousehold(dto)).thenReturn(created);

        ResponseEntity<?> response = householdController.createHousehold(token, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Household successfully created", response.getBody());
    }

    @Test
    @DisplayName("Should return 401 when token is invalid or user not found")
    void testCreateHouseholdUnauthorized() {
        String token = "Bearer invalid";
        HouseholdCreate dto = new HouseholdCreate("CakeHouse", 10.0, 20.0);

        when(jwtTokenService.getUserByToken("invalid")).thenReturn(null);

        ResponseEntity<?> response = householdController.createHousehold(token, dto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when invalid household is provided")
    void testCreateHouseholdBadRequest() {
        String token = "Bearer valid.token";
        HouseholdCreate dto = new HouseholdCreate("HouseOfMazino", 10.0, 20.0);

        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());

        doThrow(new IllegalArgumentException()).when(householdService).createHousehold(dto);

        ResponseEntity<?> response = householdController.createHousehold(token, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid household details", response.getBody());
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
        assertEquals("Household not found", response.getBody());
    }

    @Test
    @DisplayName("Should return 401 when unauthorized to create invite")
    void testCreateInviteUnauthorized() {
        String token = "Bearer bad.token";
        when(jwtTokenService.getUserByToken("bad.token")).thenReturn(null);

        ResponseEntity<?> response = householdController.createInvite(token, 1);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
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
        assertEquals("Matching user and household not found", response.getBody());
    }
    @Test
    @DisplayName("User cannot leave household if household does not exist")
    void testUserCannotLeaveHouseholdNotFound() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());

        doThrow(new NoSuchElementException("Household not found")).when(householdService).leaveHousehold(eq(1), any());

        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Matching user and household not found", response.getBody());
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
        assertEquals("User not authorized to kick this user from the household", response.getBody());
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
        assertEquals("Household or user not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot kick from household if user does not exist")
    void testUserCannotKickFromHouseholdUserNotFound() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());

        doThrow(new NoSuchElementException("User not found"))
            .when(householdService).kickUserFromHousehold(eq(1), eq(1), any());
        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Household or user not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot kick from household if the user is not a member")
    void testUserCannotKickFromHouseholdNotMember() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        doThrow(new IllegalArgumentException("User not found in household"))
            .when(householdService).kickUserFromHousehold(eq(1), eq(1), any());
        ResponseEntity<?> response = householdController.getOutOfMyHouse("Bearer valid.token", 1, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid household ID or user ID", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave non existent household")
    void testUserCannotLeaveNonExistentHousehold() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(new User());
        doThrow(new NoSuchElementException("Household not found"))
            .when(householdService).leaveHousehold(eq(1), any());
        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Matching user and household not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave household with invalid token")
    void testUserCannotLeaveInvalidToken() {
        when(jwtTokenService.getUserByToken("valid.token")).thenReturn(null);
        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("Bearer valid.token", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    @DisplayName("User cannot leave household with no token")
    void testUserCannotLeaveNoToken() {
        when(jwtTokenService.getUserByToken("")).thenReturn(null);
        ResponseEntity<?> response = householdController.getMeOutOfThisHousehold("", 1);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Could not leave household", response.getBody());
    }



}
