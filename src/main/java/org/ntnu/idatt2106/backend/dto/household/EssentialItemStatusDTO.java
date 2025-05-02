package org.ntnu.idatt2106.backend.dto.household;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing an essential item and whether it is present in a household's inventory.
 */
@Getter
@Setter
@AllArgsConstructor
public class EssentialItemStatusDTO {
    private String name;
    private boolean present;
}

