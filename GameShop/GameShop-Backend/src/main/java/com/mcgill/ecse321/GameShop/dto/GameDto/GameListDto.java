package com.mcgill.ecse321.GameShop.dto.GameDto;

import java.util.List;

public class GameListDto {

    private List<GameSummaryDto> games;

    public GameListDto(List<GameSummaryDto> games) {
        this.games = games;
    }
    protected GameListDto() {
    }

    public List<GameSummaryDto> getGames() {
        return games;
    }

    public void setGames(List<GameSummaryDto> games) {
        this.games = games;
    }

    public Integer getNumberOfGames() {
        return games.size();
    }
    
}
