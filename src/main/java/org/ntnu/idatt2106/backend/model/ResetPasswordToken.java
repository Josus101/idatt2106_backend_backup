package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ResetPasswordToken model for the database
 * @Author Konrad Seime
 * @since 0.1
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "password_reset_token")
public class ResetPasswordToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String token;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Date expirationDate;

  public ResetPasswordToken(String token, User user, Date expirationDate) {
    this.token = token;
    this.user = user;
    this.expirationDate = expirationDate;
  }
}
