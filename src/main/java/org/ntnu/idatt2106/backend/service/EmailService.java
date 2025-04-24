package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.models.EmailVerifyToken;
import org.ntnu.idatt2106.backend.models.ResetPasswordToken;
import org.ntnu.idatt2106.backend.models.User;
import org.ntnu.idatt2106.backend.repo.EmailVerificationTokenRepo;
import org.ntnu.idatt2106.backend.repo.ResetPasswordTokenRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service class for sending emails to users.
 */
@Service
public class EmailService {
  private final int EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private JWT_token jwtTokenService;

  @Autowired
  private EmailVerificationTokenRepo EmailVerificationTokenRepo;

  @Autowired
  private ResetPasswordTokenRepo ResetPasswordTokenRepo;

  @Value("${mail.from}")
  private String fromEmail;

  private static final String BASE_URL = "http://localhost:8080/";

  /**
   * Sends an email verification link to the user.
   *
   * @param user The user to whom the email is sent.
   */
  public void sendVerificationEmail(User user) {
    String token = generateEmailVerifyToken(user);
    String verificationUrl = BASE_URL + "verify?token=" + token;
    sendEmail(user.getEmail(), "Verify your email", "Click the following link to verify your email: " + verificationUrl);
    EmailVerifyToken emailVerifyToken = new EmailVerifyToken(token, user, new Date(System.currentTimeMillis() + EXPIRATION_TIME));
    EmailVerificationTokenRepo.save(emailVerifyToken);
  }

  /**
   * Sends a password reset email to the user.
   *
   * @param user The user to whom the email is sent.
   */
  public void sendResetPasswordEmail(User user) {
    String token = generateResetPasswordToken(user);
    String resetPasswordUrl = BASE_URL + "reset-password?token=" + token;
    sendEmail(user.getEmail(), "Reset your password", "Click the following link to reset your password: " + resetPasswordUrl);
    ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, user, new Date(System.currentTimeMillis() + EXPIRATION_TIME));
    ResetPasswordTokenRepo.save(resetPasswordToken);
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
    Date expirationDate = new Date(System.currentTimeMillis() + 60 * 60 * 1000); // 1 hour expiration
    if (isVerificationToken) {
      EmailVerifyToken EmailVerifyToken = new EmailVerifyToken(token, user, expirationDate);
      EmailVerificationTokenRepo.save(EmailVerifyToken);
    } else {
      ResetPasswordToken ResetPasswordToken = new ResetPasswordToken(token, user, expirationDate);
      ResetPasswordTokenRepo.save(ResetPasswordToken);
    }
    return token;
  }

  /**
   * Sends an email to the specified recipient with the given subject and body.
   *
   * @param to      The recipient's email address.
   * @param subject The subject of the email.
   * @param body    The body of the email.
   */
  public void sendEmail(String to, String subject, String body) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      System.out.println("Sending email to: " + to);
      System.out.println("Subject: " + subject);
      message.setFrom("noreply.krisefikser@gmail.com");
      message.setFrom(fromEmail);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
    } catch (Exception e) {
      System.out.println(
          "Error sending message, make sure EMAIL_USERNAME and EMAIL_PASSWORD are set in the .env file. \nThe error message is " + e);
    }
  }
}
