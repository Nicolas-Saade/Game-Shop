package com.mcgill.ecse321.GameShop.dto.SpecificGameDto;

import java.util.List;

public class SpecificGameListDto {

    private List<SpecificGameSummaryDto> games;

    public SpecificGameListDto(List<SpecificGameSummaryDto> games) {
        this.games = games;
    }
    protected SpecificGameListDto() {
    }

    public List<SpecificGameSummaryDto> getGames() {
        return games;
    }

    public void setGames(List<SpecificGameSummaryDto> games) {
        this.games = games;
    }
    
}
