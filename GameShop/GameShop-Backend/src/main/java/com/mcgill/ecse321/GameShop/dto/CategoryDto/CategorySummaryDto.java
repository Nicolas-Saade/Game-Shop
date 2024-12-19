package com.mcgill.ecse321.GameShop.dto.CategoryDto;

import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Category;

public class CategorySummaryDto {
    private String categoryName;
    private Manager manager;
    private int categoryId;

    public CategorySummaryDto(Category category) {
        this.categoryName = category.getCategoryName();
        this.categoryId = category.getCategory_id();
        this.manager = category.getManager();
    }
    public CategorySummaryDto() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public Manager getManager() {
        return manager;
    }
}
