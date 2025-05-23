package org.ntnu.idatt2106.backend.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.security.JWT_token;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginServiceTest {

  @InjectMocks
  private LoginService loginService;

  @Mock
  private UserRepo userRepo;

  @Mock
  private VerificationTokenRepo verificationTokenRepo;

  @Mock
  private EmailService emailService;
  @Mock
  private JWT_token jwt;

  @Mock
  private UserSettingsService userSettingsService;

  @Spy
  private BCryptHasher hasher = new BCryptHasher();

  private User testUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUser = new User("test@example.com", hasher.hashPassword("securePassword"), "John", "Doe", "12345678");
  }

  @Test
  @DisplayName("test should validate correct email")
  void testValidateEmail() {
    assertTrue(loginService.validateEmail("valid@email.com"));
    assertFalse(loginService.validateEmail("invalid-email"));
  }

  @Test
  @DisplayName("test should validate correct password")
  void testValidatePassword() {
    assertTrue(loginService.validatePassword("password123"));
    assertFalse(loginService.validatePassword(""));
  }

  @Test
  @DisplayName("test should validate correct phone number")
  void testValidatePhoneNumber() {
    assertTrue(loginService.validatePhoneNumber("12345678"));
    assertFalse(loginService.validatePhoneNumber("123"));
  }

  @Test
  @DisplayName("test should validate correct name")
  void testValidateName() {
    assertTrue(loginService.validateName("John"));
    assertTrue(loginService.validateName("O'Connor"));
    assertFalse(loginService.validateName("1234"));
  }

  @Test
  @DisplayName("test should return true if email is not in use")
  void testVerifyEmailNotInUse() {
    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
    assertTrue(loginService.verifyEmailNotInUse("test@example.com"));
  }

  @Test
  @DisplayName("test should return false if email is already in use")
  void testVerifyEmailAlreadyInUse() {
    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    assertFalse(loginService.verifyEmailNotInUse("test@example.com"));
  }

  @Test
  @DisplayName("test should return true if phone number is not in use")
  void testVerifyPhoneNotInUse() {
    when(userRepo.findByPhoneNumber("12345678")).thenReturn(Optional.empty());
    assertTrue(loginService.verifyPhoneNumberNotInUse("12345678"));
  }

  @Test
  @DisplayName("test should return false if phone number is already in use")
  void testVerifyPhoneAlreadyInUse() {
    when(userRepo.findByPhoneNumber("12345678")).thenReturn(Optional.of(testUser));
    assertFalse(loginService.verifyPhoneNumberNotInUse("12345678"));
  }

  @Test
  @DisplayName("Should authenticate user with correct credentials")
  void testAuthenticateSuccess() {
    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(jwt.generateJwtToken((User) any())).thenReturn(new UserTokenResponse("token", System.currentTimeMillis()));
    testUser.setVerified(true);
    when(hasher.checkPassword("securePassword", testUser.getPassword())).thenReturn(true);
    UserTokenResponse token = loginService.authenticate("test@example.com", "securePassword");

    assertNotNull(token);
    verify(jwt).generateJwtToken(testUser);
  }

  @Test
  @DisplayName("Should validate user correctly with all valid and invalid field combinations")
  void testValidateUser() {
    // All fields valid
    assertTrue(loginService.validateUser(testUser));

    // Invalid email
    testUser.setEmail("invalid-email");
    assertFalse(loginService.validateUser(testUser));

    // Reset and test invalid password
    testUser.setEmail("valid@example.com");
    testUser.setPassword("");
    assertFalse(loginService.validateUser(testUser));

    // Reset and test invalid phone number
    testUser.setPassword("securePassword");
    testUser.setPhoneNumber("123");
    assertFalse(loginService.validateUser(testUser));

    // Reset and test invalid first name
    testUser.setPhoneNumber("12345678");
    testUser.setFirstname("1234");
    assertFalse(loginService.validateUser(testUser));

    // Reset and test invalid last name
    testUser.setFirstname("WhiteMonster"); // We love white monster
    testUser.setLastname("1234");
    assertFalse(loginService.validateUser(testUser));
  }


  @Test
  @DisplayName("Should throw IllegalArgumentException when user data is invalid")
  void testRegisterInvalidUserData() {
    UserRegisterRequest invalidUser = new UserRegisterRequest(
            "valid@example.com",  // valid email
            "securePassword",     // valid password
            "1234",               // invalid first name (numbers)
            "Monkeyface",         // valid last name (funnier now)
            "12345678",           // valid phone
            "123456789"           // valid reCaptcha token
    );

    when(userRepo.findByEmail(invalidUser.getEmail())).thenReturn(Optional.empty());
    when(userRepo.findByPhoneNumber(invalidUser.getPhoneNumber())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> loginService.register(invalidUser));
  }


  @Test
  @DisplayName("Should fail authentication with wrong email")
  void testAuthenticateWrongEmail() {
    when(userRepo.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      loginService.authenticate("wrong@example.com", "securePassword");
    });
  }

  @Test
  @DisplayName("Should fail authentication with wrong password")
  void testAuthenticateWrongPassword() {
    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    testUser.setVerified(true);
    assertThrows(IllegalArgumentException.class, () -> {
      loginService.authenticate("test@example.com", "wrongPassword");
    });
  }

  @Test
  @DisplayName("Should register new valid user if parameters are valid and not in use")
  void testRegisterSuccess() throws MessagingException {
    UserRegisterRequest dto = new UserRegisterRequest("new@example.com", "newpass", "Jane", "Doe", "87654321", "123456789");

    when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
    when(userRepo.findByPhoneNumber(dto.getPhoneNumber())).thenReturn(Optional.empty());
    doNothing().when(emailService).sendVerificationEmail(any(User.class));
    doNothing().when(userSettingsService).saveUserSettings(eq(1), any(UserStoreSettingsRequest.class));

    loginService.register(dto);

    verify(emailService, times(1)).sendVerificationEmail(any(User.class));
    verify(userRepo).save(any(User.class));
  }

  @Test
  @DisplayName("Should not register if email is in use")
  void testRegisterEmailInUse() {
    UserRegisterRequest dto = new UserRegisterRequest("test@example.com", "newpass", "Jane", "Doe", "87654321", "123456789");

    when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(testUser));

    assertThrows(AlreadyInUseException.class, () -> loginService.register(dto));
  }

  @Test
  @DisplayName("Should not register if phone number is in use")
  void testRegisterPhoneInUse() {
    UserRegisterRequest dto = new UserRegisterRequest("new@example.com", "newpass", "Jane", "Doe", "12345678", "123456789");

    when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
    when(userRepo.findByPhoneNumber(dto.getPhoneNumber())).thenReturn(Optional.of(testUser));

    assertThrows(AlreadyInUseException.class, () -> loginService.register(dto));
  }

  @Test
  @DisplayName("Should validate token and return user")
  void testValidateTokenSuccess() {
    when(jwt.getUserByToken("validToken")).thenReturn(testUser);
    doNothing().when(jwt).validateJwtToken("validToken");

    User result = loginService.validateTokenAndGetUser("validToken");
    assertEquals(testUser.getEmail(), result.getEmail());
  }

  @Test
  @DisplayName("Should throw TokenExpiredException when token is expired")
  void testValidateTokenExpired() {
    doThrow(new TokenExpiredException("Token expired")).when(jwt).validateJwtToken("expiredToken", false);

    assertThrows(TokenExpiredException.class, () -> loginService.validateTokenAndGetUser("expiredToken"));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when token is invalid")
  void testValidateTokenInvalid() {
    doThrow(new IllegalArgumentException("Invalid")).when(jwt).validateJwtToken("invalid", false);

    assertThrows(IllegalArgumentException.class, () -> loginService.validateTokenAndGetUser("invalid"));
  }

  @Test
  @DisplayName("Test validateTokenAndGetUser throws IllegalArgumentException on generic error")
  void testValidateTokenError() {
    doThrow(new RuntimeException("Error"))
            .when(jwt).validateJwtToken("errorToken", false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            loginService.validateTokenAndGetUser("errorToken")
    );

    assertEquals("Error validating token", exception.getMessage());
  }

  @Test
  @DisplayName("Test resetPassword changes password")
  void testResetPasswordChangesPassword() {
    testUser.setPassword(hasher.hashPassword("oldPassword"));
    String oldPassword = testUser.getPassword();
    String newPassword = "newSecurePassword";
    loginService.resetPassword(testUser, newPassword);
    assertNotEquals(oldPassword, testUser.getPassword());
  }

  @Test
  @DisplayName("Test resetPassword sets the password correctly")
  void testResetPasswordSetsCorrectPassword() {
    String newPassword = "newSecurePassword";
    loginService.resetPassword(testUser, newPassword);
    assertTrue(hasher.checkPassword(newPassword, testUser.getPassword()));
  }

  @Test
  @DisplayName("Test verifyEmail sets user as verified")
  void testVerifyEmailSetsUserAsVerified() {
    testUser.setVerified(false);
    loginService.verifyEmail(testUser);
    assertTrue(testUser.isVerified());
  }

  @Test
  @DisplayName("Test register throws Exception if sending email fails")
  void testRegisterThrowsIfSendingEmailFails() throws MessagingException {
    UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
        "Whitemonster@gmail.com",
        "password",
        "White",
        "Monster",
        "12345678",
        "123456789"
    );
    when(userRepo.findByEmail(userRegisterRequest.getEmail())).thenReturn(Optional.empty());
    when(userRepo.findByPhoneNumber(userRegisterRequest.getPhoneNumber())).thenReturn(Optional.empty());
    doThrow(new MessagingException("Failed to send email")).when(emailService).sendVerificationEmail(any(User.class));
    assertThrows(RuntimeException.class, () -> {
      loginService.register(userRegisterRequest);
    });
  }

  @Test
  @DisplayName("Test authenticate throws UserNotVerifiedException if user is not verified")
  void testThrowsNotVerifiedIfNotVerified() {
    String mail = "apeman";
    String password = "password";
    when(userRepo.findByEmail(mail)).thenReturn(Optional.of(testUser));
    when(
        hasher.checkPassword(password, testUser.getPassword())).thenReturn(
        true);
    testUser.setVerified(false);
    when(emailService.hasValidVerificationEmail(any())).thenReturn(false);
    assertThrows(UserNotVerifiedException.class, () -> {
      loginService.authenticate(mail, password);
    });
    try {
      verify(emailService, times(1)).sendVerificationEmail(testUser);
    } catch (MessagingException e) {
      fail("Should not throw exception");
    }
  }

  @Test
  @DisplayName("Test does not send new verification email if user is already has active token")
  void testDoesNotSendNewVerificationEmailIfUserAlreadyHasActiveToken() {
    String mail = "apeman";
    String password = "password";
    when(userRepo.findByEmail(mail)).thenReturn(Optional.of(testUser));
    when(
        hasher.checkPassword(password, testUser.getPassword())).thenReturn(
        true);
    testUser.setVerified(false);
    when(emailService.hasValidVerificationEmail(any())).thenReturn(true);
    assertThrows(UserNotVerifiedException.class, () -> {
      loginService.authenticate(mail, password);
    });
    try {
      verify(emailService, times(0)).sendVerificationEmail(testUser);
    } catch (MessagingException e) {
      fail("Should not throw exception");
    }
  }

  @Test
  @DisplayName("Test throws MailsendException if mail sending fails")
  void testThrowsMailSendExceptionIfMailSendingFails() {
    String mail = "apeman";
    String password = "password";
    when(userRepo.findByEmail(mail)).thenReturn(Optional.of(testUser));
    when(
        hasher.checkPassword(password, testUser.getPassword())).thenReturn(
        true);
    testUser.setVerified(false);
    when(emailService.hasValidVerificationEmail(testUser)).thenReturn(false);
    try {
      doThrow(new MessagingException("Failed to send email")).when(emailService).sendVerificationEmail(testUser);
      assertThrows(MailSendingFailedException.class, () -> {
        loginService.authenticate(mail, password);
      });
      verify(emailService, times(1)).sendVerificationEmail(testUser);
    } catch (MessagingException e) {
      assertEquals(e.getMessage(), "failed to send verification email");
    }
  }



}
