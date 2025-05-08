package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Model class for the unit table in the database
 * This class represents a unit of measurement for a food item.
 *
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
    private String englishName;

    @Column(nullable = false)
    private String norwegianName;

    /**
     * Blank constructor for the Unit model
     */
    public Unit() {};

    /**
     * Constructor for the unit model
     * @param englishName of the unit
     * @param norwegianName of the unit
     */
    public Unit(String englishName, String norwegianName) {
        this.englishName = englishName;
        this.norwegianName = norwegianName;
    }
}
