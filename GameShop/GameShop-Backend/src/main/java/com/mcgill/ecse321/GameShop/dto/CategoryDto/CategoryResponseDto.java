package com.mcgill.ecse321.GameShop.dto.CategoryDto;

import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Category;

public class CategoryResponseDto {

    private String categoryName;
    private int categoryId;
    private String managerEmail;
    private Manager manager;

    public CategoryResponseDto() {
    }

    public CategoryResponseDto(Category category) {
        this.categoryName = category.getCategoryName();
        this.managerEmail = category.getManager().getEmail();
        this.manager = category.getManager();
        this.categoryId = category.getCategory_id();
    }
    public static CategoryResponseDto create(Category category) {
        return new CategoryResponseDto(category);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public Manager getManager() {
        return manager;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
