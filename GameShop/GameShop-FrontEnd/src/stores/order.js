import { defineStore } from "pinia";
import { useAuthStore } from "@/stores/auth";

export const useOrderStore = defineStore("order", {
  state: () => ({
    orders: [],
    currentOrder: null,
  }),

  actions: {
    async fetchOrders() {
      const auth = useAuthStore();

      if (!auth.user || auth.accountType !== "CUSTOMER") {
        console.warn("Orders can only be fetched for customers.");
        return;
      }

      try {
        const response = await fetch(
          `http://localhost:8080/orders/customer/${auth.user.email}`
        );

        if (!response.ok) {
          throw new Error("Failed to fetch orders.");
        }

        const data = await response.json();

        // Fetch specific games for each order
        const ordersWithGameDetails = await Promise.all(
          data.orders.map(async (order) => {
            try {
              const specificGamesResponse = await fetch(
                `http://localhost:8080/orders/${order.trackingNumber}/specificGames`
              );

              if (!specificGamesResponse.ok) {
                console.error(
                  `Failed to fetch specific games for order: ${order.trackingNumber}`
                );
                return { ...order, games: [], totalPrice: 0 };
              }

              const specificGamesData = await specificGamesResponse.json();

              const gamesWithDetails = await Promise.all(
                specificGamesData.games.map(async (specificGame) => {
                  try {
                    const gameId =
                      specificGame.game?.aGame_id || specificGame.game?.game_id;

                    if (!gameId) {
                      console.error(
                        "Game ID not found in specificGame:",
                        specificGame
                      );
                      return { title: "Unknown Game", quantity: 1, price: 0 };
                    }

                    const gameResponse = await fetch(
                      `http://localhost:8080/games/${gameId}`
                    );

                    if (!gameResponse.ok) {
                      return { title: "Unknown Game", quantity: 1, price: 0 };
                    }

                    const gameDetails = await gameResponse.json();
                    const gamePrice =
                      gameDetails.aPrice || gameDetails.price || 0;

                    return {
                      title: gameDetails.aTitle,
                      quantity: 1, // Each SpecificGame represents one unit
                      price: gamePrice,
                    };
                  } catch (err) {
                    console.error(
                      `Error fetching game details for specificGame:`,
                      specificGame,
                      err
                    );
                    return { title: "Unknown Game", quantity: 1, price: 0 };
                  }
                })
              );

              // Group games by title and sum quantities and total prices
              const groupedGames = gamesWithDetails.reduce((acc, game) => {
                const existingGame = acc.find((g) => g.title === game.title);
                const gameTotalPrice = game.price * game.quantity;
                if (existingGame) {
                  existingGame.quantity += game.quantity;
                  existingGame.totalPrice += isFinite(gameTotalPrice)
                    ? gameTotalPrice
                    : 0;
                } else {
                  acc.push({
                    ...game,
                    totalPrice: isFinite(gameTotalPrice) ? gameTotalPrice : 0,
                  });
                }
                return acc;
              }, []);

              // Calculate total price for the order
              const totalPrice = groupedGames.reduce(
                (sum, game) =>
                  sum + (isFinite(game.totalPrice) ? game.totalPrice : 0),
                0
              );

              return { ...order, games: groupedGames, totalPrice };
            } catch (err) {
              console.error(
                `Error processing order: ${order.trackingNumber}`,
                err
              );
              return { ...order, games: [], totalPrice: 0 };
            }
          })
        );

        this.orders = ordersWithGameDetails || [];
      } catch (error) {
        console.error("Error fetching orders:", error);
        this.orders = [];
      }
    },

    async createOrder(orderData) {
      try {
        // Step 1: Create the order
        const response = await fetch(`http://localhost:8080/orders`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(orderData),
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || "Error creating order");
        }

        const newOrder = await response.json();
        this.currentOrder = newOrder;

        alert("Order successfully created!");
      } catch (error) {
        console.error("Error creating order:", error);
        alert(`Failed to create order: ${error.message}`);
        throw error;
      }
    },

    async returnGame(trackingNumber, specificGameId) {
      console.log(
        "Attempting to return game with trackingNumber:",
        trackingNumber,
        "and specificGameId:",
        specificGameId
      );
      try {
        const response = await fetch(
          `http://localhost:8080/orders/${trackingNumber}/return/${specificGameId}`,
          {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ note: "" }), // Send an empty note or provide a value
          }
        );

        if (!response.ok) {
          const errorData = await response.text();
          console.error("Server responded with:", response.status, errorData);
          throw new Error("Failed to return game");
        }

        // Update the currentOrder
        await this.getOrderByTrackingNumber(trackingNumber);

        // Update the specific order in the orders array
        const orderIndex = this.orders.findIndex(
          (o) => o.trackingNumber === trackingNumber
        );
        if (orderIndex !== -1) {
          this.orders[orderIndex] = { ...this.currentOrder };
        }
      } catch (error) {
        console.error("Error returning game:", error);
        throw error;
      }
    },

    async getOrderByTrackingNumber(trackingNumber) {
      try {
        // Fetch the order details
        const response = await fetch(
          `http://localhost:8080/orders/${trackingNumber}`
        );

        if (!response.ok) {
          throw new Error("Error fetching order details");
        }

        const orderDetails = await response.json();

        // Fetch specific games associated with the order
        const specificGamesResponse = await fetch(
          `http://localhost:8080/orders/${trackingNumber}/specificGames`
        );

        if (!specificGamesResponse.ok) {
          console.error(
            `Failed to fetch specific games for order: ${trackingNumber}`
          );
          this.currentOrder = { ...orderDetails, games: [], totalPrice: 0 };
          return;
        }

        // Parse the specific games data
        const specificGamesData = await specificGamesResponse.json();
        const specificGamesArray = specificGamesData.games || specificGamesData;

        console.log("Specific games data:", specificGamesArray);

        // Fetch details for each specific game
        const gamesWithDetails = await Promise.all(
          specificGamesArray.map(async (specificGame) => {
            try {
              const gameId =
                specificGame.game?.aGame_id ||
                specificGame.game?.game_id ||
                specificGame.game?.gameId;

              if (!gameId) {
                console.error(
                  "Game ID not found in specificGame:",
                  specificGame
                );
                return { title: "Unknown Game", quantity: 1, price: 0 };
              }

              const gameResponse = await fetch(
                `http://localhost:8080/games/${gameId}`
              );

              if (!gameResponse.ok) {
                return { title: "Unknown Game", quantity: 1, price: 0 };
              }

              const gameDetails = await gameResponse.json();
              const gamePrice = gameDetails.aPrice || gameDetails.price || 0;

              return {
                title: gameDetails.aTitle || gameDetails.title,
                quantity: 1, // Each SpecificGame represents one unit
                price: gamePrice,
                gameId: gameId,
                specificGameId:
                  specificGame.specificGameId ||
                  specificGame.aSpecificGame_id ||
                  specificGame.specificGame_id,
                itemStatus:
                  specificGame.itemStatus ||
                  specificGame.aItemStatus ||
                  specificGame.item_status ||
                  "Confirmed",
              };
            } catch (err) {
              console.error(
                `Error fetching game details for specificGame:`,
                specificGame,
                err
              );
              return { title: "Unknown Game", quantity: 1, price: 0 };
            }
          })
        );

        console.log("Games with details:", gamesWithDetails);

        // Group games by title and sum quantities and total prices
        const groupedGames = gamesWithDetails.reduce((acc, game) => {
          const existingGame = acc.find(
            (g) => g.title === game.title && g.itemStatus === game.itemStatus
          );
          const gameTotalPrice = game.price * game.quantity;
          if (existingGame) {
            existingGame.quantity += game.quantity;
            existingGame.totalPrice += isFinite(gameTotalPrice)
              ? gameTotalPrice
              : 0;
          } else {
            acc.push({
              ...game,
              totalPrice: isFinite(gameTotalPrice) ? gameTotalPrice : 0,
            });
          }
          return acc;
        }, []);

        // Calculate total price for the order, excluding returned games
        const totalPrice = groupedGames.reduce(
          (sum, game) =>
            game.itemStatus !== "Returned"
              ? sum + (isFinite(game.totalPrice) ? game.totalPrice : 0)
              : sum,
          0
        );

        // Set the currentOrder with all the details
        this.currentOrder = {
          ...orderDetails,
          games: groupedGames,
          totalPrice,
        };
      } catch (error) {
        console.error("Error fetching order by tracking number:", error);
        this.currentOrder = null;
      }
    },
  },
});
