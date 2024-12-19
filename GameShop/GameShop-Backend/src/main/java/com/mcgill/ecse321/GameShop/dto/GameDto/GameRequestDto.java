package com.mcgill.ecse321.GameShop.dto.GameDto;

import java.util.List;

import com.mcgill.ecse321.GameShop.model.Game.GameStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GameRequestDto {

    @NotBlank(message = "Game title cannot be empty")
    private String aTitle;

    @NotBlank(message = "Game description cannot be empty")
    private String aDescription;

    @Positive(message = "Game price cannot be negative")
    private double aPrice;

    @NotNull(message = "Game status cannot be null")
    private GameStatus aGameStatus;

    @Positive(message = "Game stock quantity cannot be negative")
    private int aStockQuantity;

    @NotBlank(message = "Game photo URL cannot be empty")
    private String aPhotoUrl;

    // private List<Integer> aPlatform;
    private List<Integer> Categories;
    private List<Integer> Platforms;

    protected GameRequestDto() {
    }

    public GameRequestDto(String aTitle, String aDescription, double aPrice, GameStatus aGameStatus, int aStockQuantity,
            String aPhotoUrl) {
        this.aTitle = aTitle;
        this.aDescription = aDescription;
        this.aPrice = aPrice;
        this.aGameStatus = aGameStatus;
        this.aStockQuantity = aStockQuantity;
        this.aPhotoUrl = aPhotoUrl;
    }

    public String getaTitle() {
        return aTitle;
    }

    public void setaTitle(String aTitle) {
        this.aTitle = aTitle;
    }

    public String getaDescription() {
        return aDescription;
    }

    public void setaDescription(String aDescription) {
        this.aDescription = aDescription;
    }

    public double getaPrice() {
        return aPrice;
    }

    public void setaPrice(double aPrice) {
        this.aPrice = aPrice;
    }

    public GameStatus getaGameStatus() {
        return aGameStatus;
    }

    public void setaGameStatus(GameStatus aGameStatus) {
        this.aGameStatus = aGameStatus;
    }

    public int getaStockQuantity() {
        return aStockQuantity;
    }

    public void setaStockQuantity(int aStockQuantity) {
        this.aStockQuantity = aStockQuantity;
    }

    public String getaPhotoUrl() {
        return aPhotoUrl;
    }

    public void setaPhotoUrl(String aPhotoUrl) {
        this.aPhotoUrl = aPhotoUrl;
    }

    public void setCategories(List<Integer> Categories) {
        this.Categories = Categories;
    }

    public List<Integer> getCategories() {
        return Categories;
    }

    public void setPlatforms(List<Integer> Platforms) {
        this.Platforms = Platforms;
    }

    public List<Integer> getPlatforms() {
        return Platforms;
    }
}
