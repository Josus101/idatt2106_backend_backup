package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.category.CategoryCreateRequest;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.repo.UnitRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

  @Autowired
  private JWT_token jwt;

  @Autowired
  private ItemRepo itemRepo;


  /**
  * Retrieves a category by its ID.
  *
  * @param id the ID of the category
  * @return the category with the given ID
  */
  public CategoryGetResponse getCategoryById(int id) {
    return categoryRepo.findById(id)
      .map(category -> new CategoryGetResponse(category.getId(), category.getName()))
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
            .map(category -> new CategoryGetResponse(category.getId(), category.getName()))
            .toList();
  }

  /**
   * Verifies that the authorized user is an admin.
   *
   * @param authorization the JWT token of the user
   * @return true if the user is an admin, false otherwise
   */
  public boolean isAdmin(String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return false;
    }
    String token = authorization.substring(7);
    if (token.isEmpty()) {
      return false;
    }
    Admin admin = jwt.getAdminUserByToken(token);
    if (admin == null) {
      return false;
    }
    return true;
  }

  /**
   * Creates a new category.
   *
   * @param category the category to create
   */
  public void createCategory(CategoryCreateRequest category,  String authorization) {
    if (!isAdmin(authorization)) {
      throw new IllegalArgumentException("Unauthorized: Only admins can create categories");
    }
    Category newCategory = new Category();
    if (category.getName().isEmpty()) {
      throw new IllegalArgumentException("Category name cannot be empty");
    }
    if (category.getKcalPerUnit() != null && category.getKcalPerUnit() < 0) {
      throw new IllegalArgumentException("Kcal per unit cannot be negative");
    }
    if (categoryRepo.findByName(category.getName()).isPresent()) {
      throw new IllegalArgumentException("Category with this name already exists");
    }
    newCategory.setName(category.getName());
    newCategory.setKcalPerUnit(category.getKcalPerUnit());
    newCategory.setIsEssential(category.getIsEssential());
    categoryRepo.save(newCategory);
  }

  /**
   * Updates an existing category.
   *
   * @param id the ID of the category to update
   * @param category the updated category data
   */
  public void updateCategory(int id, CategoryCreateRequest category, String authorization) {
    if (!isAdmin(authorization)) {
      throw new IllegalArgumentException("Unauthorized: Only admins can create categories");
    }
    Category existingCategory = categoryRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Category not found"));
    if (category.getName() != null && !category.getName().isEmpty()) {
      existingCategory.setName(category.getName());
    }
    if (category.getKcalPerUnit() != null) {
      existingCategory.setKcalPerUnit(category.getKcalPerUnit());
    }
    if (category.getIsEssential() != null) {
      existingCategory.setIsEssential(category.getIsEssential());
    }
    categoryRepo.save(existingCategory);
  }

  /**
   * Deletes a category by its ID.
   *
   * @param id the ID of the category to delete
   */
  public void deleteCategory(int id, String authorization) {
    if (!isAdmin(authorization)) {
      throw new IllegalArgumentException("Unauthorized: Only admins can create categories");
    }

    Category category = categoryRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Category not found"));
    if (category.getName().equals("Other")) {
      throw new IllegalArgumentException("Cannot delete Other category");
    }
    Category otherCategory = categoryRepo.findByName("Other")
        .orElseThrow(() -> new NoSuchElementException("Other category not found"));
    List<Item> items = itemRepo.findByCategoryId(id);
    for (Item item : items) {
      item.setCategory(otherCategory);
      itemRepo.save(item);
    }
    categoryRepo.delete(category);
  }
}
