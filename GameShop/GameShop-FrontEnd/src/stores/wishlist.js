import { defineStore } from "pinia";
import { useAuthStore } from "@/stores/auth";
import { useCartStore } from "@/stores/cart";
import { usePromotionsStore } from "@/stores/promotions";
import { productsStore } from "@/stores/products";

export const useWishlistStore = defineStore("wishlist", {
  state: () => ({
    wishlistId: null,
    wishlistItems: [],
  }),

  actions: {
    async fetchWishlist(tgt_game = null) {
      const authStore = useAuthStore();
      if (!authStore.user || authStore.accountType !== "CUSTOMER") {
        console.warn("Wishlist can only be fetched for customers.");
        return;
      }

      try {
        const response = await fetch(
          `http://localhost:8080/wishlist/customer/${authStore.user.email}`
        );

        if (!response.ok) {
          throw new Error("Failed to fetch wishlist.");
        }

        const data = await response.json();

        // Extract wishlistId and games
        this.wishlistId = data.wishlistId;
        // Update existing wishlistItems or add new ones
        const updatedWishlistItems = data.games.games.map((game) => {
          const existingItem = this.wishlistItems.find(
            (item) => item.gameId === game.gameId
          );

          let discountedPrice = game.price;

          // If tgt_game matches the current game, apply its discounted price
          if (tgt_game && tgt_game.gameId === game.gameId) {
            discountedPrice = tgt_game.price;
          }

          if (existingItem && discountedPrice < game.price) {
            // Update existing item
            return {
              ...existingItem,
              price: discountedPrice,
              quantity: game.quantity,
            };
          } else if (existingItem) {
            // Update existing item
            return {
              ...existingItem,
              quantity: game.quantity,
            };
          }
          else {
            // Add new item
            return {
              gameId: game.gameId,
              title: game.title,
              price: discountedPrice || game.price,
              photoUrl: game.photoUrl,
            };
          }
        });

        // Replace wishlistItems with updated items
        this.wishlistItems = updatedWishlistItems;
      
        console.log("Wishlist fetched from backend:", this.wishlistItems);

      } catch (error) {
        console.error("Error fetching wishlist:", error);
      }
    },

    async addGameToWishlist(gameId) {
      const promos = await usePromotionsStore().fetchValidPromotions();
      const prod = productsStore();

      if (!this.wishlistId) {
        await this.fetchWishlist();
      }

      // check if the game has any promotions and apply them:
      console.log("Adding game from prodList:", prod.products);
      console.log("With GameId:", gameId);
      let tgt_game = prod.products.find((game) => {
        if (game.gameId === gameId) {
          console.log("Game found:", game);
          return game;
        }
      });

      if (this.wishlistId === null || tgt_game.gameId === null) {
        console.error("Invalid wishlistId or gameId");
      }

      console.log("Adding game to wishlist:", this.wishlistId);

      try {
        const response = await fetch(
          `http://localhost:8080/wishlist/${this.wishlistId}/${tgt_game.gameId}`,
          {
            method: "PUT",
          }
        );

        if (!response.ok) {
          throw new Error("Failed to add game to wishlist.");
        }

        await this.fetchWishlist(tgt_game); // Refresh the wishlist after adding
        alert("Game added to WishList!");
      } catch (error) {
        console.error("Error adding game to wishlist:", error);
      }
    },

    async removeGameFromWishlist(gameId) {
      if (!this.wishlistId) {
        await this.fetchWishlist();
      }

      try {
        const response = await fetch(
          `http://localhost:8080/wishlist/${this.wishlistId}/${gameId}`,
          {
            method: "DELETE",
          }
        );

        if (!response.ok) {
          throw new Error("Failed to remove game from wishlist.");
        }

        await this.fetchWishlist(); // Refresh the wishlist after removing
      } catch (error) {
        console.error("Error removing game from wishlist:", error);
      }
    },

    async clearWishlist() {
      if (!this.wishlistId) {
        await this.fetchWishlist();
      }

      try {
        const response = await fetch(
          `http://localhost:8080/wishlist/${this.wishlistId}`,
          {
            method: "PUT",
          }
        );

        if (!response.ok) {
          throw new Error("Failed to clear wishlist.");
        }

        await this.fetchWishlist(); // Refresh the wishlist after clearing
      } catch (error) {
        console.error("Error clearing wishlist:", error);
      }
    },

    async addWishlistToCart() {
      const cartStore = useCartStore();
      try {
        for (const item of this.wishlistItems) {
          await cartStore.addGameToCart(item.gameId, 1);
        }
        await this.clearWishlist();
        alert("WishList moved to Cart!");
      } catch (error) {
        console.error("Error moving wishlist to cart:", error);
      }
    },

    async initializeWishlist() {
      await this.fetchWishlist();

      for (const item of this.wishlistItems) {
        const refreshedGame = await productsStore().refreshPrices(item);
        await this.fetchWishlist(refreshedGame);
      }
    },
    
  },
});
