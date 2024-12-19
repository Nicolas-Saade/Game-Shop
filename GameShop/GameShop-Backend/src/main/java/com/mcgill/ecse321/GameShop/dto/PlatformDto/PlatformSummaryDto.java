package com.mcgill.ecse321.GameShop.dto.PlatformDto;

import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;

public class PlatformSummaryDto {
    private String platformName;
    private Manager manager;
    private int platformId;

    public PlatformSummaryDto(Platform platform) {
        this.platformName = platform.getPlatformName();
        this.platformId = platform.getPlatform_id();
        this.manager = platform.getManager();
    }
    public PlatformSummaryDto() {
    }
    public String getPlatformName() {
        return platformName;
    }
    public int getPlatformId() {
        return platformId;
    }
    public Manager getManager() {
        return manager;
    }
    
    
}
