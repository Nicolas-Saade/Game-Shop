package com.mcgill.ecse321.GameShop.dto.PromotionDto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PromotionRequestDto {

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @Min(value = 0, message = "Discount rate must be between 0 and 100")
    @Max(value = 100, message = "Discount rate must be between 0 and 100")
    private int discountRate;

    @NotNull(message = "Start LocalDate  cannot be null")
    private LocalDate  startLocalDate ;

    @NotNull(message = "End LocalDate  cannot be null")
    private LocalDate  endLocalDate ;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Manager email cannot be empty")
    private String managerEmail;

    @NotNull(message = "Game IDs cannot be null")
    private List<Integer> gameIds;

    public PromotionRequestDto() {
    }

    public PromotionRequestDto(String description, int discountRate, LocalDate  startLocalDate , LocalDate  endLocalDate , String managerEmail, List<Integer> gameIds) {
        this.description = description;
        this.discountRate = discountRate;
        this.startLocalDate  = startLocalDate ;
        this.endLocalDate  = endLocalDate ;
        this.managerEmail = managerEmail;
        this.gameIds = gameIds;
    }

    // Getters and Setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public LocalDate  getStartLocalDate () {
        return startLocalDate ;
    }

    public void setStartLocalDate (LocalDate  startLocalDate ) {
        this.startLocalDate  = startLocalDate ;
    }

    public LocalDate  getEndLocalDate () {
        return endLocalDate ;
    }

    public void setEndLocalDate (LocalDate  endLocalDate ) {
        this.endLocalDate  = endLocalDate ;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public List<Integer> getGameIds() {
        return gameIds;
    }

    public void setGameIds(List<Integer> gameIds) {
        this.gameIds = gameIds;
    }
}
