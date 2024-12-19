package com.mcgill.ecse321.GameShop.dto.SpecificGameDto;

import java.util.List;

import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SpecificGameRequestDto {
        @NotNull(message = "Item status cannot be null")
        private ItemStatus itemStatus;

        private List<String> trackingNumbers;
        @Positive(message = "Associated Game ID cannot be negative")
        private int game_id;

        public SpecificGameRequestDto(ItemStatus itemStatus, List<String> trackingNumber, int game_id) {
            this.itemStatus = itemStatus;
            this.trackingNumbers = trackingNumber;
            this.game_id = game_id;
        }
        protected SpecificGameRequestDto() {
        }

        public ItemStatus getItemStatus() {
            return itemStatus;
        }

        public void setItemStatus(ItemStatus itemStatus) {
            this.itemStatus = itemStatus;
        }

        public List<String> getTrackingNumbers() {
            return trackingNumbers;
        }

        public void setTrackingNumbers(List<String> trackingNumber) {
            this.trackingNumbers = trackingNumber;
        }

        public int getGame_id() {
            return game_id;
        }

        public void setGame_id(int game_id) {
            this.game_id = game_id;
        }

}
