package com.mcgill.ecse321.GameShop.dto.PlatformDto;

import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;

public class PlatformResponseDto {

    private String platformName;
    private int platformId;
    private String managerEmail;
    private Manager manager;

    public PlatformResponseDto() {
    }
        public PlatformResponseDto(Platform platform) {
        this.platformName = platform.getPlatformName();
        this.managerEmail = platform.getManager().getEmail();
        this.manager = platform.getManager();
        this.platformId = platform.getPlatform_id();
    }
    public static PlatformResponseDto create(Platform platform) {
        return new PlatformResponseDto(platform);
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
    public Manager getManager() {
        return manager;
    }
    public int getPlatformId() {
        return platformId;
    }
}
