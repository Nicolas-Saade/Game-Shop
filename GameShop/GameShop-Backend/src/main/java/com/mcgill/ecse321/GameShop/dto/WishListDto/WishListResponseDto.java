package com.mcgill.ecse321.GameShop.dto.WishListDto;

import java.util.ArrayList;
import java.util.List;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.WishList;

public class WishListResponseDto {
   
    private int wishlistId;
    private String title;
    private String customerEmail;
    private GameListDto games;

    public WishListResponseDto(WishList wishList) {
        this.wishlistId = wishList.getWishList_id();
        this.title = wishList.getTitle();
        List<GameSummaryDto> gameSummaryList = new ArrayList<GameSummaryDto>();
        for(Game game : wishList.getGames()) {
            gameSummaryList.add(new GameSummaryDto(game));
        }
        this.games = new GameListDto(gameSummaryList);
        this.customerEmail = wishList.getCustomer().getEmail();
    }
    public WishListResponseDto() {
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public GameListDto getGames() {
        return games;
    }

    public static WishListResponseDto create(WishList wishList) {
        return new WishListResponseDto(wishList);
    }
}