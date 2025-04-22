package org.ntnu.idatt2106.backend.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "household")
public class Household {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column
  private double latitude;

  @Column
  private double longitude;

  @ManyToMany
  @JoinTable(
    name = "household_members",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "household_id")
  )
  private List<User> members = new ArrayList<>();

  @ManyToOne
  @JoinTable(name = "user")
  private User creator;

}
