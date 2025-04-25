package org.ntnu.idatt2106.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import org.ntnu.idatt2106.backend.model.EmailVerifyToken;
import org.ntnu.idatt2106.backend.model.ResetPasswordToken;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.EmailVerificationTokenRepo;
import org.ntnu.idatt2106.backend.repo.ResetPasswordTokenRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
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
  private EmailVerificationTokenRepo emailVerificationTokenRepo;

  @Autowired
  private ResetPasswordTokenRepo resetPasswordTokenRepo;

  @Value("${mail.from}")
  private String fromEmail;

  //TODO change this to front end url when front end supports this
  private static final String BASE_URL = "http://localhost:8080/";

  /**
   * Sends an email verification link to the user.
   *
   * @param user The user to whom the email is sent.
   * @throws MessagingException If there is an error while sending the email.
   * @throws IllegalStateException If the user is already verified.
   */
  public void sendVerificationEmail(User user) throws MessagingException {
    if (user.isVerified()) {
      throw new IllegalStateException("User is already verified");
    }
    String token = generateEmailVerifyToken(user);
    String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
    String verificationUrl = BASE_URL + "api/users/verify/" + encodedToken;

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
   */
  public void sendResetPasswordEmail(User user) throws MessagingException {
    String token = generateResetPasswordToken(user);
    String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
    String resetUrl = BASE_URL + "api/users/reset-password/" + encodedToken;

    String htmlContent = buildEmailTemplate(
        "Reset Your Password",
        "You've requested to reset your password. Click the button below to proceed. But since the url won't work yes, here is the token: " + encodedToken,
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
   */
  private void sendHtmlEmail(String to, String subject, String htmlContent)
      throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setFrom("Krisefikser <" + fromEmail + ">");
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlContent, true);

    message.addHeader("X-Mailer", "KrisefikserMailer");
    message.addHeader("X-Priority", "1");
    message.addHeader("List-Unsubscribe", "<mailto:unsubscribe@yourdomain.com>");

    mailSender.send(message);
  }

  /**
   * Generates a token for email verification for the given user.
   *
   * @param user The user for whom the token is generated.
   * @return The generated token.
   */
  private String generateEmailVerifyToken(User user) {
    return generateToken(user, true);
  }

  /**
   * Generates a token for resetting the password for the given user.
   *
   * @param user The user for whom the token is generated.
   * @return The generated token.
   */
  private String generateResetPasswordToken(User user) {
    return generateToken(user, false);
  }

  /**
   * Generates a token for the given user and saves it to the database.
   *
   * @param user The user for whom the token is generated.
   * @param isVerificationToken Indicates whether the token is for email verification or password reset.
   * @return The generated token.
   */
  private String generateToken(User user, boolean isVerificationToken) {
    String token = jwtTokenService.generateJwtToken(user).getToken();
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

    if (isVerificationToken) {
      emailVerificationTokenRepo.save(new EmailVerifyToken(token, user, expirationDate));
    } else {
      resetPasswordTokenRepo.save(new ResetPasswordToken(token, user, expirationDate));
    }
    return token;
  }

  /**
   * For sending test emails.
   * @param to The recipient's email address.
   * @param subject The subject of the email.
   * @param text The text content of the email.
   */
  public void sendTestEmail(String to, String subject, String text) {
    String htmlContent = buildEmailTemplate(
        "Test Email",
        text,
        " ",
        " ",
        "This is a test email."
    );
    try {
      sendHtmlEmail(to, subject, htmlContent);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send test email", e);
    }
  }
}