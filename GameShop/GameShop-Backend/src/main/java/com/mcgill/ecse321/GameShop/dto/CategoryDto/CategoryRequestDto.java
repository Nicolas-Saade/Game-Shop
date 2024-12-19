package com.mcgill.ecse321.GameShop.dto.CategoryDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDto {
    @NotBlank(message = "Category name cannot be empty")
    private String categoryName;
    
    @Email(message = "Email should be valid")
    private String managerEmail;

    public CategoryRequestDto() {
    }

    // public CategoryRequestDto(Category category) {
    //     this.categoryName = category.getCategoryName();
    //     this.managerEmail = category.getManager().getEmail();
    //     this.manager = category.getManager();
    // }
    
    public CategoryRequestDto(String categoryName, String managerEmail) {
        this.categoryName = categoryName;
        this.managerEmail = managerEmail;
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
}
