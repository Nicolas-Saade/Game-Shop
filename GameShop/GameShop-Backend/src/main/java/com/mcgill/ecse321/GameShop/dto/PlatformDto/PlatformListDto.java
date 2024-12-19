package com.mcgill.ecse321.GameShop.dto.PlatformDto;

import java.util.ArrayList;
import java.util.List;

import com.mcgill.ecse321.GameShop.model.Platform;

public class PlatformListDto {
    private List<PlatformSummaryDto> platforms;
    public PlatformListDto(List<PlatformSummaryDto> platforms) {
        this.platforms = platforms;
    }
    protected PlatformListDto() {
    }
    public List<PlatformSummaryDto> getPlatforms() {
        return platforms;
    }
    public void setPlatforms(List<PlatformSummaryDto> platforms) {
        this.platforms = platforms;
    }
    public static PlatformListDto convertToPlatformListDto(List<Platform> platforms) {
        List<PlatformSummaryDto> dtos = new ArrayList<PlatformSummaryDto>();
        for (Platform platform : platforms) {
            dtos.add(new PlatformSummaryDto(platform));
        }
        return new PlatformListDto(dtos);
    }
}
