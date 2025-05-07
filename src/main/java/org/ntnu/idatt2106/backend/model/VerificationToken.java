package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * VerificationToken model for the database.
 *
 * @Author Konrad Seime
 * @since 0.2
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "verification_token")
public class VerificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  VerificationTokenType type;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private Date expirationDate;

  /**
   * Constructor for the VerificationToken model.
   *
   * @param token the verification token
   * @param email the email associated with the token
   * @param expirationDate the expiration date of the token
   * @param type the type of the token
   */
  public VerificationToken(String token, String email, Date expirationDate, VerificationTokenType type) {
    this.token = token;
    this.email = email;
    this.expirationDate = expirationDate;
    this.type = type;
  }
}
