package org.ntnu.idatt2106.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HouseholdCreate {
    private String name;
    private double latitude;
    private double longitude;

}
