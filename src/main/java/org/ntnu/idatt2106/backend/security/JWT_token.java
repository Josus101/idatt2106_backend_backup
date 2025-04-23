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
import org.ntnu.idatt2106.backend.dto.UserTokenDTO;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for generating and validating JWT tokens.
 * This includes token generation, validation, and user extraction from tokens.
 */
@Service
public class JWT_token {

  @Autowired
  private UserRepo userRepo;
  private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private static final long EXPIRATION_TIME = 120 * 60 * 1000; // 2 hours

  /**
   * Generates a JWT token for the given user.
   *
   * @param user the user for whom to generate the token
   * @return a UserTokenDTO containing the generated token and its expiration time
   */
  public UserTokenDTO generateJwtToken(User user) {
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    String token = Jwts.builder()
            .setSubject(user.getStringID())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(key)
            .compact();
    return new UserTokenDTO(token, expirationDate.getTime());
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
   * Extracts the user ID from the JWT token.
   *
   * @param token the JWT token
   * @return the user ID as a string, or null if the token is invalid
   */
  public String extractIdFromJwt(String token) {
    try {
      Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token)
          .getBody();
      return claims.getSubject();
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
    String id = extractIdFromJwt(token);
    if (id == null) {
      return null;
    }
    return userRepo.findById(Integer.parseInt(id)).orElse(null);
  }
}
