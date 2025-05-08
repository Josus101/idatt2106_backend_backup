package org.ntnu.idatt2106.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling category-related operations.
 * This class is responsible for the business logic related to categories.
 * @author Jonas Reiher
 * @version 0.1
 * @since 0.1
 */
@Service
public class CategoryService {
  // Repositories
  @Autowired
  private CategoryRepo categoryRepo;


  /**
  * Retrieves a category by its ID.
  *
  * @param id the ID of the category
  * @return the category with the given ID
  */
  public CategoryGetResponse getCategoryById(int id) {
    return categoryRepo.findById(id)
        .map(category -> new CategoryGetResponse(category.getId(), category.getEnglishName(),
            category.getNorwegianName()))
      .orElseThrow(() -> new NoSuchElementException("Category not found"));
  }

  /**
   * Retrieves all categories.
   *
   * @return a list of all categories
   */
  public List<CategoryGetResponse> getAllCategories() {
    return categoryRepo.findAll()
            .stream()
            .map(category -> new CategoryGetResponse(category.getId(), category.getEnglishName(), category.getNorwegianName()))
            .toList();
  }

}
