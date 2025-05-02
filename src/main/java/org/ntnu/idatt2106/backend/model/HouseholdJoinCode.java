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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HouseholdJoinCode model for the database
 * This code allows users to join a household
 *
 * @Author Konrad Seime
 * @since 0.2
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "household_join_code")
public class HouseholdJoinCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true, length = 8)
  private String code;

  @ManyToOne()
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  @Column(nullable = false)
  private Date expirationDate;

  /**
   * Constructor for the HouseholdJoinCode model
   *
   * @param code the code to join the household
   * @param household the id of the household
   * @param expirationDate the expiration date of the code
   */
  public HouseholdJoinCode(String code, Household household, Date expirationDate) {
    this.code = code;
    this.household = household;
    this.expirationDate = expirationDate;
  }

}
