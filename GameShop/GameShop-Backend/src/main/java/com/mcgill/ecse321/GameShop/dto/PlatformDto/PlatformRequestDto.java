package com.mcgill.ecse321.GameShop.dto.PlatformDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public class PlatformRequestDto {
   @NotBlank(message = "Platform name cannot be empty")
    private String platformName;
    @Email(message = "Email should be valid")
    private String managerEmail;
 

    public PlatformRequestDto() {
    }

    // public PlatformRequestDto(Platform platform) {
    //     this.platformName = platform.getPlatformName();
    //     this.managerEmail = platform.getManager().getEmail();
    //     this.manager = platform.getManager();
    // }
    public PlatformRequestDto(String platformName, String managerEmail) {
        this.platformName = platformName;
        this.managerEmail = managerEmail;
    }
    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
