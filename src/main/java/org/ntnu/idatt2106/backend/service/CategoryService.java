package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.UnitRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling category-related operations.
 * This class is responsible for the business logic related to categories.
 */
@Service
public class CategoryService {
  // Repositories
  @Autowired
  private CategoryRepo categoryRepo;

  @Autowired
  private UnitRepo unitRepo;

  /**
  * Retrieves a category by its ID.
  *
  * @param id the ID of the category
  * @return the category with the given ID
  */
  public CategoryGetResponse getCategoryById(int id) {
    return categoryRepo.findById(id)
      .map(category -> new CategoryGetResponse(category.getId(), category.getName()))
      .orElseThrow(() -> new IllegalArgumentException("Category not found"));
  }
}
