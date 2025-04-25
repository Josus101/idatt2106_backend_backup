package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * Unit model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "unit")
@Getter
@Setter
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    /**
     * Blank constructor for the Unit model
     */
    public Unit() {};

    /**
     * Constructor for the unit model
     * @param name of the unit
     */
    public Unit(String name) {
        this.name = name;
    }
}
