package org.ntnu.idatt2106.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for generating and validating JWT tokens.
 * This includes token generation, validation, and user extraction from tokens.
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class JWT_token {

  @Autowired
  private UserRepo userRepo;
  @Autowired
  private AdminRepo adminRepo;
  private static final Key userKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private static final Key adminKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private static final long EXPIRATION_TIME = 120 * 60 * 1000; // 2 hours

  /**
   * Constructor for JWT_token service.
   *
   * @param userRepo the User repository for database access
   */
  public JWT_token(UserRepo userRepo, AdminRepo adminRepo) {
    this.userRepo = userRepo;
    this.adminRepo = adminRepo;
  }

  /**
   * Default constructor for JWT_token service.
   */
  public JWT_token() {
  }

  /**
   * Generates a JWT token for the given user.
   *
   * @param user the user for whom to generate the token
   * @return a UserTokenResponse containing the generated token and its expiration time
   */
  public UserTokenResponse generateJwtToken(User user) {
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    String token = Jwts.builder()
            .setSubject(user.getStringID())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(userKey)
            .compact();
    return new UserTokenResponse(token, expirationDate.getTime());
  }
  /**
   * Generates a JWT token for the given user.
   *
   * @param admin the user for whom to generate the token
   * @return a UserTokenDTO containing the generated token and its expiration time
   */
  public UserTokenResponse generateJwtToken(Admin admin) {
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    String token = Jwts.builder()
        .setSubject(admin.getStringId())
        .setIssuedAt(new Date())
        .setExpiration(expirationDate)
        .signWith(adminKey)
        .compact();
    return new UserTokenResponse(token, expirationDate.getTime());
  }

  /**
   * Generates a JWT token for the given user with a given expiration time.
   *
   * @param user the user for whom to generate the token
   * @param expirationTime the expiration time in milliseconds
   * @return a UserTokenResponse containing the generated token and its expiration time
   */
  public UserTokenResponse generateJwtTokenWithExpirationTime(User user, long expirationTime) {
    Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
    String token = Jwts.builder()
            .setSubject(user.getStringID())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(userKey)
            .compact();
    return new UserTokenResponse(token, expirationDate.getTime());
  }

  /**
   * Validates the JWT token and checks if it has expired.
   *
   * @param token the JWT token to validate
   * @param admin boolean indicating if the token is for an admin
   * @throws TokenExpiredException if the token has expired
   * @throws IllegalArgumentException if the token is invalid or empty
   */
  public void validateJwtToken(String token, boolean admin) {
    try {
      Key key = admin ? adminKey : userKey;
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    } catch (ExpiredJwtException e) {
      throw new TokenExpiredException("Token has expired");
    } catch (UnsupportedJwtException | MalformedJwtException e) {
      throw new IllegalArgumentException("Token is invalid");
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Token is empty");
    }
  }

  /**
   * Validates the JWT token and checks if it has expired.
   *
   * @param token the JWT token to validate
   * @throws TokenExpiredException if the token has expired
   * @throws IllegalArgumentException if the token is invalid or empty
   */
  public void validateJwtToken(String token) {
    try {
      validateJwtToken(token, false);
    } catch (ExpiredJwtException e) {
      throw new TokenExpiredException("Token has expired");
    } catch (UnsupportedJwtException | MalformedJwtException e) {
      throw new IllegalArgumentException("Token is invalid");
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Token is empty");
    }
  }

  /**
   * Extracts the user ID from the JWT token.
   *
   * @param token the JWT token
   * @param admin boolean indicating if the token is for an admin
   * @return the user ID as a string, or null if the token is invalid
   */
  public String extractIdFromJwt(String token, boolean admin) {
    try {
      Key key = admin ? adminKey : userKey;
      Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token)
          .getBody();
      return claims.getSubject();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Extracts the user ID from the JWT token.
   *
   * @param token the JWT token
   * @return the user ID as a string, or null if the token is invalid
   */
  public String extractIdFromJwt(String token) {
    try {
      return extractIdFromJwt(token, false);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Retrieves the user associated with the given JWT token.
   *
   * @param token the JWT token
   * @return the User object, or null if the token is invalid or user not found
   */
  public User getUserByToken(String token) {
    String id = extractIdFromJwt(token, false);
    if (id == null) {
      return null;
    }
    return userRepo.findById(Integer.parseInt(id)).orElse(null);
  }

  /**
   * Retrieves the admin associated with the given JWT token.
   *
   * @param token the JWT token
   * @return the Admin user object, or null if the token is invalid or user not found
   */
  public Admin getAdminUserByToken(String token) {
    System.out.println("Token: " + token);
    String id = extractIdFromJwt(token, true);
    System.out.println("ID: " + id);
    if (id == null) {
      return null;
    }
    return adminRepo.findById(Integer.parseInt(id)).orElse(null);
  }
}
