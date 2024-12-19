import { defineStore } from "pinia";
import { usePromotionsStore } from "./promotions.js";

export const productsStore = defineStore("products", {
  state: () => ({
    products: [],
    cart: [],
  }),

  actions: {
    async fetchProductsFromDB() {
      try {
        // Fetch Games from the backend
        const response = await fetch("http://localhost:8080/games");
        const data = await response.json();

        // Fetch Valid Promotions
        const validPromos = await usePromotionsStore().fetchValidPromotions();
        const promoMap = new Map();

        // Create the promotion Map
        if (validPromos.length > 0) {
          validPromos.forEach((promo) => {
            promo.games.forEach((game) => {
              const gameId = game.gameId;
              if (gameId === null || gameId < 0) {
                console.error("Invalid gameId in promotion:", promo);
              }
              if (promoMap.has(gameId)) {
                promoMap.get(gameId).push(promo.discountRate);
              } else {
                promoMap.set(gameId, [promo.discountRate]);
              }
              if (!promoMap.has(gameId)) {
                console.error("Error in PromoMap Initi for game", gameId);
              }
            });
          });
        } else {
          console.log("No Valid Promos Found");
        }

        // GGo Through games and apply promos
        this.products = data.games.map((game) => {
          let discountedPrice = game.price; // Start with the base price
          let originalPrice = null;
          // Check if the game has any promotions
          if (promoMap.has(game.gameId)) {
            promoMap.get(game.gameId).forEach((promo) => {
              const newPrice = game.price - (game.price * promo) / 100;

              // Update the discounted price if the new price is lower
              if (newPrice < discountedPrice) {
                originalPrice = discountedPrice;
                discountedPrice = Math.floor(newPrice*100) / 100;
              }
            });
          }

          // Return the updated game object with prices and additional fields
          return {
            ...game,
            price: discountedPrice,
            originalPrice: originalPrice || null,
            categories: game.categories?.categories || [], // Flatten categories
            platforms: game.platforms?.platforms || [], // Flatten platforms
          };
        });
      } catch (error) {
        console.error("Error fetching products:", error);
      }
    },

    // Apply promotions to a single game
    async applyPromotionToGame(game) {
      try {
        const validPromos = await usePromotionsStore().fetchValidPromotions();
        const promo_list = []; // Array to hold the promotions to the specific game inputted

        // check if any promotions on that game
        // If any append to the promo_list
        if (validPromos.length > 0) {
          validPromos.forEach((promo) => {
            promo.games.forEach((promoted_game) => {
              if (promoted_game.gameId === game.gameId) {
                promo_list.push(promo.discountRate);
              }
            });
          });
        }

        if (promo_list.length === 0) {
          console.log("No Promotions for game:", game);
          return game;
        }

        console.log("Promotions for game:", game, promo_list);

        // Apply all the discounts to the game
        let discountedPrice = game.price;
        let originalPrice = null;

        promo_list.forEach((promo) => {
          const newPrice = game.price - (game.price * promo) / 100;
          if (newPrice < discountedPrice) {
            originalPrice = discountedPrice;
            discountedPrice = Math.floor(newPrice * 100) / 100;
          }
        });

        return {
          ...game,
          price: discountedPrice,
          originalPrice: originalPrice || null,
          categories: game.categories?.categories || [],
          platforms: game.platforms?.platforms || [],
        };
      } catch (error) {
        console.error("Error applying promotions to game:", error);
      }
    },

    async addGame(gameData) {
      let newGame = undefined;
      try {
        const response = await fetch("http://localhost:8080/games", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(gameData),
        });
        newGame = await response.json();
        newGame = await response.json();
        this.products.push(newGame);
        console.log("Game added:", newGame);
      } catch (error) {
        console.error("Error adding game:", error);
      }

      if (newGame === undefined) {
        console.error("Error adding game:", error);
      }

      try {
        for (let i = 0; i < newGame.aStockQuantity; i++) {
          const specificGameData = {
            itemStatus: "Returned", // Adjust this status as required
            trackingNumber: [], // Add tracking numbers if needed
            game_id: newGame.aGame_id,
          };

          // Create the specific game
          const specificGameResponse = await fetch(
            "http://localhost:8080/specificGames",
            {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(specificGameData),
            }
          );
          const newSpecificGame = await specificGameResponse.json();
          if (newSpecificGame !== null) {
            console.log("Specific Game added:", newSpecificGame);
          }
        }
      } catch (error) {
        console.error("Error adding game:", error);
      }

      if (newGame === undefined) {
        console.error("Error adding game:", error);
      }

      try {
        for (let i = 0; i < newGame.aStockQuantity; i++) {
          const specificGameData = {
            itemStatus: "Returned", // Adjust this status as required
            trackingNumber: [], // Add tracking numbers if needed
            game_id: newGame.aGame_id,
          };

          // Create the specific game
          const specificGameResponse = await fetch(
            "http://localhost:8080/specificGames",
            {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(specificGameData),
            }
          );
          const newSpecificGame = await specificGameResponse.json();
          if (newSpecificGame !== null) {
            console.log("Specific Game added:", newSpecificGame);
          }
        }
      } catch (error) {
        console.error("Error adding game:", error);
      }
    },

    async fetchCategories() {
      try {
        const response = await fetch("http://localhost:8080/categories");
        const data = await response.json();
        this.categories = data.categories || []; // Ensure categories are updated in the store
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    },

    async fetchPlatforms() {
      try {
        const response = await fetch("http://localhost:8080/platforms");
        const data = await response.json();
        return data.platforms;
      } catch (error) {
        console.error("Error fetching platforms:", error);
        return [];
      }
    },

    async refreshPrices(All) {
      // REfresh products inside input All
      let game = await this.applyPromotionToGame(All);
      return game;
    },
  },
});
