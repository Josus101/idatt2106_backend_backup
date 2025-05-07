package org.ntnu.idatt2106.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.model.*;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

  @InjectMocks
  private EmailService emailService;

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private JWT_token jwtTokenService;

  @Mock
  private VerificationTokenRepo verificationTokenRepo;

  @Mock
  private MimeMessage mimeMessage;

  private User testUser;
  private Admin testAdmin;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1);
    testUser.setEmail("test@example.com");
    testUser.setVerified(false);
    testUser.setFirstname("John");
    testUser.setLastname("Pork");
    testUser.setPhoneNumber("12345678");

    testAdmin = new Admin();
    testAdmin.setId(1);
    testAdmin.setEmail("test@krisefikser.no");
    testAdmin.setActive(false);
    testAdmin.setSuperUser(false);
    testAdmin.setUsername("testAdmin");


    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    String token = "mockedTokenMadeByMonkeys";
    long expirationTime = 60 * 60 * 1000L; // 1 hour
    UserTokenResponse userTokenResponse = new UserTokenResponse(token, expirationTime);
    when(jwtTokenService.generateJwtToken(testUser)).thenReturn(userTokenResponse);

    TestUtils.setField(emailService, "fromEmail", "no-reply@example.com");

  }

  @Test
  @DisplayName("Test sendVerificationEmail method sends email and saves token")
  void testSendVerificationEmailSendsEmailAndSaves() throws Exception {
    emailService.sendVerificationEmail(testUser);

    verify(verificationTokenRepo).save(any(VerificationToken.class));
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Test sendVerificationEmail method throws exception if user is already verified")
  void testSendVerificationEmailFailsIfUserVerified() {
    testUser.setVerified(true);

    Exception exception = assertThrows(IllegalStateException.class, () -> {
      emailService.sendVerificationEmail(testUser);
    });

    assertEquals("User is already verified", exception.getMessage());
    verify(mailSender, never()).send((MimeMessage) any());
  }

  @Test
  @DisplayName("Test sendVerificationEmail method sends a valid email")
  void testSendResetPasswordEmailSendsValidEmail() throws Exception {
    emailService.sendResetPasswordEmail(testUser);

    verify(verificationTokenRepo).save(any(VerificationToken.class));
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Test sendResetPasswordEmail method throws exception if user is already verified")
  void testSendTestEmailSendsValidTestEmail() {
    assertDoesNotThrow(() -> {
      emailService.sendTestEmail("recipient@example.com", "Subject", "This is a test.");
    });
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Test sendTestEmail method throws exception if email is invalid")
  void testBuildEmailTemplateIsNotNullAndIncludesContent() throws Exception {
    String html = TestUtils.callPrivateMethod(emailService, "buildEmailTemplate",
        new Class[]{String.class, String.class, String.class, String.class, String.class},
        new Object[]{"Header", "Body", "http://link", "Click Here", "Footer"});

    assertNotNull(html);
    assertTrue(html.contains("Header"));
    assertTrue(html.contains("Click Here"));
    assertTrue(html.contains("http://link"));
  }

  @Test
  @DisplayName("Test sendAdminActivationEmail method sends email and saves token")
  void testSendAdminMailSendsMailAndSaves() {
    when(jwtTokenService.generateJwtToken(testAdmin)).thenReturn(new UserTokenResponse("mockedToken", 3600L));

    try {
      emailService.sendAdminActivationEmail(testAdmin);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    verify(verificationTokenRepo).save(any(VerificationToken.class));
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Test sendAdminActivationEmail method throws exception if admin is already active")
  void testSendAdminMailFailsIfAdminActive() {
    testAdmin.setActive(true);
    when(jwtTokenService.generateJwtToken(testAdmin)).thenReturn(new UserTokenResponse("mockedToken", 3600L));
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      emailService.sendAdminActivationEmail(testAdmin);
    });

    assertEquals("Admin is already active", exception.getMessage());
    verify(mailSender, never()).send((MimeMessage) any());
  }


}
