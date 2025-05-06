package org.ntnu.idatt2106.backend.service;

import jakarta.validation.constraints.Email;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.admin.AdminGetResponse;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

  @InjectMocks
  private AdminService adminService;

  @Mock
  private AdminRepo adminRepo;

  @Mock
  private JWT_token jwt;

  private Admin testAdmin;

  @Mock
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    BCryptHasher hasher = new BCryptHasher();
    testAdmin = new Admin("admin", hasher.hashPassword("password"), "test@mail.com",
        true);
  }

  @Test
  @DisplayName("Should validate non-empty password")
  void testValidatePassword() {
    assertTrue(adminService.validatePassword("adminuser123"));
    assertFalse(adminService.validatePassword(""));
  }

  @Test
  @DisplayName("Should validate correct name format")
  void testValidateName() {
    assertTrue(adminService.validateName("Admin"));
    assertTrue(adminService.validateName("O'Connor"));
    assertFalse(adminService.validateName("1234"));
  }

  @Test
  @DisplayName("Should return true if username is not in use")
  void testVerifyUsernameNotInUseTrue() {
    when(adminRepo.existsByUsername("admin")).thenReturn(false);
    assertTrue(adminService.verifyUsernameNotInUse("admin"));
  }

  @Test
  @DisplayName("Should return false if username is already in use")
  void testVerifyUsernameNotInUseFalse() {
    when(adminRepo.existsByUsername("admin")).thenReturn(true);
    assertFalse(adminService.verifyUsernameNotInUse("admin"));
  }

  @Test
  @DisplayName("Should validate admin user with correct data")
  void testValidateAdminUserTrue() {
    when(adminRepo.existsByUsername("admin")).thenReturn(false);
    assertTrue(adminService.validateAdminUser("admin", "password123"));
  }

  @Test
  @DisplayName("Should not validate admin user with invalid name")
  void testValidateAdminUserInvalidName() {
    assertFalse(adminService.validateAdminUser("1234", "password"));
  }

  @Test
  @DisplayName("Should not validate admin user if username is in use")
  void testValidateAdminUserUsernameInUse() {
    when(adminRepo.existsByUsername("admin")).thenReturn(true);
    assertFalse(adminService.validateAdminUser("admin", "password"));
  }

  @Test
  @DisplayName("Should throw if admin is not super user")
  void testVerifyAdminIsSuperUserThrows() {
    Admin nonSuperAdmin = new Admin("admin", "pass", "test@mail.com", false);
    when(jwt.getAdminUserByToken("token")).thenReturn(nonSuperAdmin);

    assertThrows(UnauthorizedException.class, () -> {
      adminService.verifyAdminIsSuperUser("token");
    });
  }

  @Test
  @DisplayName("Should authenticate admin with correct credentials")
  void testAuthenticateSuccess() {
    when(adminRepo.findByUsername("admin")).thenReturn(Optional.of(testAdmin));
    when(jwt.generateJwtToken(any(Admin.class)))
        .thenReturn(new UserTokenResponse("jwtToken", System.currentTimeMillis()));
    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    testAdmin.setActive(true);
    String token = adminService.authenticate("admin", "password");
    testAdmin.setActive(false);
    assertNotNull(token);
    assertEquals("jwtToken", token);
  }

  @Test
  @DisplayName("Should throw if admin username not found")
  void testAuthenticateUsernameNotFound() {
    when(adminRepo.findByUsername("admin")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      adminService.authenticate("admin", "password");
    });
  }

  @Test
  @DisplayName("Should throw if admin password is incorrect")
  void testAuthenticateWrongPassword() {
    Admin wrongPassAdmin = new Admin("admin", new BCryptHasher().hashPassword("otherpass"),"test@mail.com", true);
    when(adminRepo.findByUsername("admin")).thenReturn(Optional.of(wrongPassAdmin));
    wrongPassAdmin.setActive(true);
    assertThrows(IllegalArgumentException.class, () -> {
      adminService.authenticate("admin", "wrongpassword");
    });
  }

  @Test
  @DisplayName("Should register new admin if valid and super user")
  void testRegisterSuccess() {
    Admin newAdmin = new Admin("newadmin", "password","test@mail.com", false);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.existsByUsername("newadmin")).thenReturn(false);

    adminService.register(newAdmin, "token");

    verify(adminRepo).save(any(Admin.class));
  }

  @Test
  @DisplayName("Test register should throw if username is invalid")
  void testRegisterThrowsIfUsernameInvalid() {
    Admin newAdmin = new Admin("", "password","test@mail.com", false);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.existsByUsername("newadmin")).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.register(newAdmin, "token");
    });
  }
  @Test
  @DisplayName("Should not register if username is taken")
  void testRegisterUsernameInUse() {
    Admin newAdmin = new Admin("admin", "password","test@mail.com", false);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.existsByUsername("admin")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.register(newAdmin, "token");
    });
  }

  @Test
  @DisplayName("Should throw UnauthorizedException if not a super user")
  void testRegisterNotSuperUser() {
    AdminService spyService = Mockito.spy(adminService);

    doThrow(new UnauthorizedException("Not authorized"))
        .when(spyService).verifyAdminIsSuperUser("token");

    Admin newAdmin = new Admin("new", "pass","test@mail.com", false);

    assertThrows(UnauthorizedException.class, () -> {
      spyService.register(newAdmin, "token");
    });
  }

  @Test
  @DisplayName("Should register admin using string overload method")
  void testRegisterOverloadedSuccess() {
    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.existsByUsername("newadmin")).thenReturn(false);

    adminService.register("newadmin", "password@pass.com", "token");

    verify(adminRepo).save(any(Admin.class));
  }

  @Test
  @DisplayName("Should throw if elevating non-existing admin")
  void testElevateAdminNotFound() {
    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.elevateAdmin("1", "token");
    });
  }

  @Test
  @DisplayName("Should throw if elevating already super user")
  void testElevateAdminAlreadySuperUser() {
    Admin superAdmin = new Admin("super", "pass","test@mail.com", true);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.findById(1)).thenReturn(Optional.of(superAdmin));

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.elevateAdmin("1", "token");
    });
  }

  @Test
  @DisplayName("Should elevate a non-super admin to super user")
  void testElevateAdminSuccess() {
    Admin nonSuper = new Admin("user", "pass","test@mail.com", false);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.findById(1)).thenReturn(Optional.of(nonSuper));

    adminService.elevateAdmin("1", "token");

    assertTrue(nonSuper.isSuperUser());
    verify(adminRepo).save(nonSuper);
  }
  @Test
  @DisplayName("Test elevateAdmin throws if not super user")
  void testElevateAdminThrowsWhenUnauthorized() {
    Admin nonSuper = new Admin("user", "pass","test@mail.com", false);
    Admin notVerySuper2 = new Admin("weakling", "pass","test@mail.com", false);
    when(jwt.getAdminUserByToken("token")).thenReturn(notVerySuper2);
    when(adminRepo.findById(1)).thenReturn(Optional.of(nonSuper));

    assertThrows(UnauthorizedException.class, () -> {
      adminService.elevateAdmin("1", "token");
    });
    assertFalse(nonSuper.isSuperUser());
  }

  @Test
  @DisplayName("Should throw if exterminate target not found")
  void testExterminateAdminNotFound() {
    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.exterminateAdmin("1", "token");
    });
  }

  @Test
  @DisplayName("Should throw if trying to delete super user")
  void testExterminateSuperUserThrows() {
    Admin superAdmin = new Admin("super", "pass","test@mail.com", true);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.findById(1)).thenReturn(Optional.of(superAdmin));

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.exterminateAdmin("1", "token");
    });
  }

  @Test
  @DisplayName("Should delete a non-super admin successfully")
  void testExterminateAdminSuccess() {
    Admin nonSuper = new Admin("normal", "pass","test@mail.com", false);

    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);
    when(adminRepo.findById(1)).thenReturn(Optional.of(nonSuper));

    adminService.exterminateAdmin("1", "token");

    verify(adminRepo).delete(nonSuper);
  }

  @Test
  @DisplayName("Test exterminateAdmin throws if not super user")
  void testExterminateAdminThrowsWhenUnauthorized() {
    Admin nonSuper = new Admin("user", "pass","test@mail.com", false);
    Admin notVerySuper2 = new Admin("weakling", "pass","test@mail.com", false);
    when(jwt.getAdminUserByToken("token")).thenReturn(notVerySuper2);
    when(adminRepo.findById(1)).thenReturn(Optional.of(nonSuper));

    assertThrows(UnauthorizedException.class, () -> {
      adminService.exterminateAdmin("1", "token");
    });
  }

  @Test
  @DisplayName("Test Autheticate throws when logging in to unactivated admin")
  void testAuthenticateThrowsWhenUnactivated() {
    when(adminRepo.findByUsername("admin")).thenReturn(Optional.of(testAdmin));
    testAdmin.setActive(false);
    assertThrows(UserNotVerifiedException.class, () -> {
      adminService.authenticate("admin", "password");
    });
  }

  @Test
  @DisplayName("Activate admin user should set active to true")
  void testActivateAdminUser() {
    Admin admin = new Admin("test","","test",false);
    when(adminRepo.findById(1)).thenReturn(Optional.of(testAdmin));
    assertFalse(admin.isActive());

    adminService.activateAdmin(admin, "damnthispasswordisCOol");

    assertTrue(admin.isActive());
    verify(adminRepo).save(admin);
  }

  @Test
  @DisplayName("changePassword changes password to new password")
  void testResetPassword() {
    String newPassword = "newpassword123";
    Admin adminJr = new Admin("ape", "oldpass", "ape", false);

    adminService.changePassword(adminJr, newPassword);

    assertNotEquals("oldpass", testAdmin.getPassword());
    verify(adminRepo).save(adminJr);
  }

  @Test
  @DisplayName("changePassword throws if password is empty")
  void testResetPasswordEmpty() {
    String newPassword = "";

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.changePassword(testAdmin, newPassword);
    });
  }

  @Test
  @DisplayName("activateAdmin throws if admin is already active")
  void testActivateAdminUserAlreadyActive() {
    when(adminRepo.findById(1)).thenReturn(Optional.of(testAdmin));
    testAdmin.setActive(true);

    assertThrows(IllegalArgumentException.class, () -> {
      adminService.activateAdmin(testAdmin, "token");
    });
  }

  @Test
  @DisplayName("getAllAdmins returns all admins")
  void testGetAllAdmins() {
    when(adminRepo.findAll()).thenReturn(List.of(testAdmin));
    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);

    List<AdminGetResponse> admins = adminService.getAllAdmins("token");
    assertEquals(1, admins.size());
    assertEquals(testAdmin.getUsername(), admins.get(0).getUsername());
    assertEquals(testAdmin.isSuperUser(), admins.get(0).isSuperUser());
    verify(adminRepo).findAll();
  }

  @Test
  @DisplayName("getAllAdmins throws if no admins")
  void testGetAllAdminsThrowsNoAdmin() {
    when(adminRepo.findAll()).thenReturn(List.of());
    when(jwt.getAdminUserByToken("token")).thenReturn(testAdmin);

    assertThrows(UserNotFoundException.class, () -> {
      adminService.getAllAdmins("token");
    });
  }

  @Test
  @DisplayName("getAllAdmins throws if not super user")
  void testGetAllAdminsThrowsNotSuperUser() {
    Admin notVerySuper = new Admin("smallDog", "strongpass", "test@est.t", false);
    when(jwt.getAdminUserByToken("token")).thenReturn(notVerySuper);
    when(adminRepo.findAll()).thenReturn(List.of(testAdmin));

    assertThrows(UnauthorizedException.class, () -> {
      adminService.getAllAdmins("token");
    });
  }


  @Test
  @DisplayName("sendActivateEmail sends email to activate admin")
  void testSendActivateEmail() {
    when(adminRepo.findById(1)).thenReturn(Optional.of(testAdmin));
    try {
      doNothing().when(emailService).sendAdminActivationEmail(any(Admin.class));
    } catch (Exception e) {
      fail();
    }

    adminService.sendActivateEmail(testAdmin);
  }

  @Test
  @DisplayName("sendActivateEmail throws if admin is already active")
  void testSendActivateEmailAlreadyActive() {
    when(adminRepo.findById(1)).thenReturn(Optional.of(testAdmin));
    testAdmin.setActive(true);

    assertThrows(MailSendingFailedException.class, () -> {
      adminService.sendActivateEmail(testAdmin);
    });
  }

  @Test
  @DisplayName("sendActivateEmail throws if mail sending failed")
  void testSendActivateEmailMailSendingFailed() {
    when(adminRepo.findById(1)).thenReturn(Optional.of(testAdmin));
    try {
      doThrow(new RuntimeException("Mail sending failed")).when(emailService)
          .sendAdminActivationEmail(any(Admin.class));
    } catch (Exception e) {
      fail();
    }

    assertThrows(RuntimeException.class, () -> {
      adminService.sendActivateEmail(testAdmin);
    });
  }


}
