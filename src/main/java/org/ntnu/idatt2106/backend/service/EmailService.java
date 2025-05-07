package org.ntnu.idatt2106.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.model.VerificationToken;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service class for sending emails to users.
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class EmailService {

  private final int EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private JWT_token jwtTokenService;

  @Autowired
  private VerificationTokenRepo verificationTokenRepo;

  @Value("${mail.from}")
  private String fromEmail;

  private static final String BASE_URL = "http://localhost:5173/";

  /**
   * Sends an email verification link to the user.
   *
   * @param user The user to whom the email is sent.
   * @throws MessagingException If there is an error while sending the email.
   * @throws IllegalStateException If the user is already verified, or if the email sender is not configured.
   */
  public void sendVerificationEmail(User user) throws MessagingException {
    if (user.isVerified()) {
      throw new IllegalStateException("User is already verified");
    }
    String token = generateEmailVerifyToken(user);
    String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
    String verificationUrl = BASE_URL + "email-verification/" + encodedToken;

    String htmlContent = buildEmailTemplate(
        "Verify Your Email Address",
        "Please confirm your email address to complete your registration",
        verificationUrl,
        "Verify Email",
        "This link will expire in 1 hour."
    );

    sendHtmlEmail(user.getEmail(), "Verify Your Email", htmlContent);
  }
  /**
   * Sends a password reset email to the user.
   *
   * @param user The user to whom the email is sent.
   * @throws MessagingException If there is an error while sending the email.
   * @throws IllegalStateException If the email sender is not configured.
   */
  public void sendResetPasswordEmail(User user) throws MessagingException {
    String token = generateResetPasswordToken(user);
    String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
    String resetUrl = BASE_URL + "password-reset/" + encodedToken;

    String htmlContent = buildEmailTemplate(
        "Reset Your Password",
        "You've requested to reset your password. Click the button below to proceed",
        resetUrl,
        "Reset Password",
        "This link is valid for 1 hour."
    );

    sendHtmlEmail(user.getEmail(), "Password Reset Request", htmlContent);
  }
  /**
   * Builds the HTML email template.
   * @param header The header of the email.
   * @param message The message body of the email.
   * @param actionUrl The URL for the action button.
   * @param actionText The text for the action button.
   * @param footer The footer of the email.
   * @return The complete HTML email template as a string.
   */
  private String buildEmailTemplate(String header, String message,
      String actionUrl, String actionText,
      String footer) {
    return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 20px auto; padding: 20px; }
                    .header { color: #2c3e50; font-size: 24px; margin-bottom: 20px; }
                    .content { margin: 20px 0; }
                    .button {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #3498db;
                        color: white !important;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #7f8c8d;
                        border-top: 1px solid #eee;
                        padding-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">%s</div>
                    <div class="content">
                        <p>%s</p>
                        <a href="%s" class="button">%s</a>
                        <p>Or copy this link: <br>%s</p>
                    </div>
                    <div class="footer">
                        <p>%s</p>
                        <p>Time: %s</p>
                    </div>
                </div>
            </body>
            </html>
            """,
        header, message, actionUrl, actionText, actionUrl,
        footer, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
  }

  /**
   * Sends an HTML email.
   *
   * @param to The recipient's email address.
   * @param subject The subject of the email.
   * @param htmlContent The HTML content of the email.
   * @throws MessagingException If there is an error while sending the email.
   * @throws IllegalStateException If the email sender is not configured.
   */
  private void sendHtmlEmail(String to, String subject, String htmlContent)
      throws MessagingException {
    if (fromEmail.equals("I_AM_INVALID")) {
      throw new IllegalStateException("Email sender is not configured. Make sure you have the current .env file");
    }
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setFrom("Krisefikser <" + fromEmail + ">");
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlContent, true);

    message.addHeader("X-Mailer", "KrisefikserMailer");
    message.addHeader("X-Priority", "1");

    mailSender.send(message);
  }

  /**
   * Generates a token for email verification for the given user.
   *
   * @param user The user for whom the token is generated.
   * @return The generated token.
   */
  private String generateEmailVerifyToken(User user) {
    return generateToken(user, VerificationTokenType.EMAIL_VERIFICATION);
  }

  /**
   * Generates a token for resetting the password for the given user.
   *
   * @param user The user for whom the token is generated.
   * @return The generated token.
   */
  private String generateResetPasswordToken(User user) {
    return generateToken(user, VerificationTokenType.PASSWORD_RESET);
  }

  /**
   * Generates a token for the given user and saves it to the database.
   *
   * @param user The user for whom the token is generated.
   * @param type Indicates what type of token is being generated.
   * @return The generated token.
   */
  private String generateToken(User user, VerificationTokenType type) {
    String token = jwtTokenService.generateJwtToken(user).getToken();
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

    verificationTokenRepo.save(new VerificationToken(token, user.getEmail(), expirationDate,
        type));
    return token;
  }

  /**
   * Generates a verification token for the given admin and saves it to the database.
   *
   * @param admin The admin for whom the token is generated.
   * @return The generated token.
   */
  private String generateAdminToken(Admin admin) {
    String token = jwtTokenService.generateJwtToken(admin).getToken();
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    verificationTokenRepo.save(new VerificationToken(token, admin.getEmail(), expirationDate,
        VerificationTokenType.ADMIN_VERIFICATION));
    return token;
  }

  /**
   * For sending test emails.
   * @param to The recipient's email address.
   * @param subject The subject of the email.
   * @param text The text content of the email.
   * @throws MessagingException If there is an error while sending the email.
   * @throws IllegalStateException If the email sender is not configured.
   */
  public void sendTestEmail(String to, String subject, String text) throws MessagingException, IllegalStateException {
    String htmlContent = buildEmailTemplate(
        "Test Email",
        text,
        " ",
        " ",
        "This is a test email."
    );
    sendHtmlEmail(to, subject, htmlContent);
  }

  /**
   * Checks if the user has a valid verification email.
   *
   * @param user The user to check.
   * @return true if the user has a valid verification email, false otherwise.
   */
  public boolean hasValidVerificationEmail(User user) {
    Optional<VerificationToken> token = verificationTokenRepo.findByEmail(user.getEmail());
    return token.filter(
        emailVerifyToken -> !emailVerifyToken.getExpirationDate().before(new Date())).isPresent();
  }

  /**
   * Sends a mail to activate an admin user
   *
   * @param admin The admin to whom the email is sent.
   * @throws MessagingException If there is an error while sending the email.
   */
  public void sendAdminActivationEmail(Admin admin) throws MessagingException {
    if (admin.isActive()) {
      throw new IllegalStateException("Admin is already active");
    }
    String token = generateAdminToken(admin);
    String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
    String verificationUrl = BASE_URL + "admin-activation/" + encodedToken;

    String htmlContent = buildEmailTemplate(
        "Activate Your Admin Account",
        "An admin of Krisefikser has created an admin account for you, with the username "
            + admin.getUsername() + ". "
            + "Please click the button below to activate your account.",
        verificationUrl,
        "Activate Admin Account",
        "This link will expire in 24 hour."
    );

    sendHtmlEmail(admin.getEmail(), "Activate Your Admin Account", htmlContent);
  }

  /**
   * Sends a 2FA token to the user.
   *
   * @param to The recipient's email address.
   * @param token The 2FA token to be sent.
   * @throws MessagingException If there is an error while sending the email.
   */
  public void send2FA(String to, String token) throws MessagingException {
    String htmlContent = buildEmailTemplate(
        "2FA Token",
        "Your 2FA token is: " + token,
        " ",
        token,
        "Please return to login page."
    );
    sendHtmlEmail(to, "2FA Token", htmlContent);
  }
}