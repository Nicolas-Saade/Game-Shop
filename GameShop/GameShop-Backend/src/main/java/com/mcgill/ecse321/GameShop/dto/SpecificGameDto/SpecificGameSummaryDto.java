package com.mcgill.ecse321.GameShop.dto.SpecificGameDto;

import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;

public class SpecificGameSummaryDto {
    
    private int specificGame_id;
    private ItemStatus itemStatus;
    private Game game;

    public SpecificGameSummaryDto(SpecificGame specificGame) {
        this.specificGame_id = specificGame.getSpecificGame_id();
        this.itemStatus = specificGame.getItemStatus();
        this.game = specificGame.getGames();
    }

    public int getSpecificGame_id() {
        return specificGame_id;
    }
    protected SpecificGameSummaryDto() {
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public Game getGame() {
        return game;
    }

}
