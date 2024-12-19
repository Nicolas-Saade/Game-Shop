<template>
  <div class="catalog-container">
    <div class="sidebar">
      <h3>Search</h3>

      <!-- Search Bar -->
      <v-text-field
        v-model="searchStore.searchQuery"
        label="Search by name"
        outlined
        dense
        clearable
        style="margin-left: 20px; max-width: 300px"
        @input="toggleSearchFilter"
      ></v-text-field>

      <h3>Filters</h3>

      <!-- Promotions Filter -->
      <div class="filter-section">
        <h4>Promotions</h4>
        <div class="custom-checkbox">
          <input
            type="checkbox"
            id="onSaleCheckbox"
            :checked="filters.onSale"
            @change="togglePromotionsFilter"
          />
          <label for="onSale">On Sale</label>
        </div>
      </div>

      <!-- Category Filter -->
      <div class="filter-section">
        <h4>Category</h4>
        <div
          class="custom-checkbox"
          v-for="category in categories"
          :key="category.categoryId"
        >
          <input
            type="checkbox"
            :id="'category-' + category.categoryId"
            :checked="filters.categories.includes(category.categoryId)"
            @change="toggleCategoryFilter(category.categoryId)"
          />
          <label :for="'category-' + category.categoryId">
            {{ category.categoryName }}
          </label>
        </div>
      </div>

      <!-- Platform Filter -->
      <div class="filter-section">
        <h4>Platform</h4>
        <div
          class="custom-checkbox"
          v-for="platform in platforms"
          :key="platform.platformId"
        >
          <input
            type="checkbox"
            :id="'platform-' + platform.platformId"
            :checked="filters.platforms.includes(platform.platformId)"
            @change="togglePlatformFilter(platform.platformId)"
          />
          <label :for="'platform-' + platform.platformId">
            {{ platform.platformName }}
          </label>
        </div>
      </div>
    </div>

    <!-- Products List -->
    <div class="products-list">
      <v-row no-gutters>
        <v-col
          v-for="product in filteredProducts"
          :key="product.gameId"
          cols="12"
          sm="4"
        >
          <product-item
            :product-data="product"
            @item-clicked="goToProductPage"
          />
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from "vue";
import assert from "assert";
import ProductItem from "@/components/ProductItem.vue";
import { productsStore } from "@/stores/products";
import { usePromotionsStore } from "@/stores/promotions";
import { useRouter } from "vue-router";
import { searchQueryStore } from "@/stores/searchQuery";

export default defineComponent({
  name: "CatalogView",
  components: {
    ProductItem,
  },
  setup() {
    const store = productsStore();
    const router = useRouter();
    const promos = usePromotionsStore();
    const searchStore = searchQueryStore();

    const filters = ref({
      search: "", // Search query holder
      onSale: false,
      categories: [],
      platforms: [],
    });

    const categories = ref([]);
    const platforms = ref([]);
    const promotions = ref([]);

    const filteredProducts = computed(() => {
      if (!store.products.length) {
        return [];
      }

      return store.products.filter((product) => {
        let matches = true;

        if (
          filters.value.search !== null &&
          filters.value.search !== "" &&
          filters.value.search !== undefined &&
          filters.value.search !== " "
        ) {
          matches =
            matches &&
            product.title
              .toLowerCase()
              .includes(filters.value.search.toLowerCase());
        }

        return matches;
      });
    });

    const applyFilters = async () => {
      if (
        filters.value.categories.length > 0 &&
        filters.value.platforms.length > 0
      ) {
        await fetchGamesByCategoriesAndPlatforms(
          filters.value.categories,
          filters.value.platforms
        );
      } else if (filters.value.categories.length > 0) {
        await fetchGamesByCategories(filters.value.categories);
      } else if (filters.value.platforms.length > 0) {
        await fetchGamesByPlatforms(filters.value.platforms);
      } else if (filters.value.onSale) {
        await fetchGamesOnSale();
      } else {
        await store.fetchProductsFromDB();
      }
    };

    const fetchGamesOnSale = async () => {
      // Fetch all Promotions from the backend
      let allPromotions = await promos.fetchValidPromotions();
      const promotionsArray = Array.isArray(allPromotions) ? allPromotions : [];

      // Update the store.products to display
      try {
        // Create a map to store games by gameId
        let gamesMap = new Map();

        for (const promotion of allPromotions) {
          const response = await fetch(
            `http://localhost:8080/promotions/${promotion.promotionId}/games`
          );
          const data = await response.json();

          data.forEach((game) => {
            // Calcualte the discounted price
            const discountRate = promotion.discountRate;
            const discountedPrice =
              game.price - (game.price * discountRate) / 100;
            gamesMap.set(game.gameId, {
              ...game,
              price:
                discountedPrice !== game.price ? Math.floor(discountedPrice * 100) / 100 : game.price,
              discountedPrice:
                discountedPrice !== game.price ? Math.floor(discountedPrice * 100) / 100 : game.price,
              originalPrice: game.price,
            });
          });
        }
        store.products = Array.from(gamesMap.values());
      } catch (error) {
        console.error("Error fetching games on sale:", error);
      }
    };

    const fetchGamesByCategories = async (categoryIds) => {
      try {
        let gamesMap = new Map();
        for (const id of categoryIds) {
          const response = await fetch(
            `http://localhost:8080/categories/${id}/games`
          );
          const data = await response.json();

          for (let game of data.games) {
            game = await store.refreshPrices(game);
            gamesMap.set(game.gameId, {
              ...game,
              categories: game.categories?.categories || [],
              platforms: game.platforms?.platforms || [],
              promotions: game.promotions || [],
            });
          }

          // data.games.forEach((game) => {
          //   gamesMap.set(game.gameId, {
          //     ...game,
          //     categories: game.categories?.categories || [],
          //     platforms: game.platforms?.platforms || [],
          //     promotions: game.promotions || [],
          //   });
          // });
        }

        store.products = Array.from(gamesMap.values());
      } catch (error) {
        console.error("Error fetching games by categories:", error);
      }
    };

    const fetchGamesByPlatforms = async (platformIds) => {
      try {
        let gamesMap = new Map();
        for (const id of platformIds) {
          const response = await fetch(
            `http://localhost:8080/platforms/${id}/games`
          );
          const data = await response.json();

          for (let game of data.games) {
            game = await store.refreshPrices(game);
            gamesMap.set(game.gameId, {
              ...game,
              categories: game.categories?.categories || [],
              platforms: game.platforms?.platforms || [],
              promotions: game.promotions || [],
            });
          }
        }

        store.products = Array.from(gamesMap.values());
      } catch (error) {
        console.error("Error fetching games by platforms:", error);
      }
    };

    const fetchGamesByCategoriesAndPlatforms = async (
      categoryIds,
      platformIds
    ) => {
      try {
        let categoryGamesMap = new Map();
        let platformGamesMap = new Map();

        // Fetch games by categories
        for (const id of categoryIds) {
          const response = await fetch(
            `http://localhost:8080/categories/${id}/games`
          );
          const data = await response.json();

          data.games.forEach((game) => {
            categoryGamesMap.set(game.gameId, {
              ...game,
              categories: game.categories?.categories || [],
              platforms: game.platforms?.platforms || [],
              promotions: game.promotions || [],
            });
          });
        }

        // Fetch games by platforms
        for (const id of platformIds) {
          const response = await fetch(
            `http://localhost:8080/platforms/${id}/games`
          );
          const data = await response.json();

          data.games.forEach((game) => {
            platformGamesMap.set(game.gameId, {
              ...game,
              categories: game.categories?.categories || [],
              platforms: game.platforms?.platforms || [],
              promotions: game.promotions || [],
            });
          });
        }

        // Find intersection of games in both maps
        const intersectionGames = Array.from(categoryGamesMap.values()).filter(
          (game) => platformGamesMap.has(game.gameId)
        );

        // Call the refreshPrices method for each game
        for (let i = 0; i < intersectionGames.length; i++) {
          const updatedGame = await store.refreshPrices(intersectionGames[i]);
          intersectionGames[i] = updatedGame;
        }

        // Assign updated array to store.products
        store.products = intersectionGames;
      } catch (error) {
        console.error(
          "Error fetching games by categories and platforms:",
          error
        );
      }
    };

    const toggleCategoryFilter = async (categoryId) => {
      if (filters.value.categories.includes(categoryId)) {
        filters.value.categories = filters.value.categories.filter(
          (id) => id !== categoryId
        );
      } else {
        filters.value.categories.push(categoryId);
      }
      await applyFilters();
    };

    const togglePlatformFilter = async (platformId) => {
      if (filters.value.platforms.includes(platformId)) {
        filters.value.platforms = filters.value.platforms.filter(
          (id) => id !== platformId
        );
      } else {
        filters.value.platforms.push(platformId);
      }
      await applyFilters();
    };

    const togglePromotionsFilter = async () => {
      filters.value.onSale = !filters.value.onSale;
      await applyFilters();
    };

    const toggleSearchFilter = async () => {
      filters.value.search = searchStore.searchQuery;
      await applyFilters();
    };

    const goToProductPage = (id) => {
      router.push({ name: "ProductView", params: { id } });
    };

    onMounted(async () => {
      await store.fetchProductsFromDB();

      // Fetch categories dynamically from the backend
      try {
        const response = await fetch("http://localhost:8080/categories");
        const data = await response.json();
        categories.value = data.categories.map((cat) => ({
          categoryName: cat.categoryName,
          categoryId: cat.categoryId,
        }));
      } catch (error) {
        console.error("Error fetching categories:", error);
      }

      // Fetch platforms dynamically from the backend
      try {
        const response = await fetch("http://localhost:8080/platforms");
        const data = await response.json();
        platforms.value = data.platforms.map((plat) => ({
          platformName: plat.platformName,
          platformId: plat.platformId,
        }));
      } catch (error) {
        console.error("Error fetching platforms:", error);
      }

      // Fetch games on sale dynamically from the backend
      try {
        const response = await fetch("http://localhost:8080/promotions");
        const data = await response.json();
        promotions.value = data.promotions.map((promo) => ({
          promotionId: promo.promotionId,
          description: promo.description,
          discountRate: promo.discountRate,
          startLocalDate: promo.startLocalDate,
          endLocalDate: promo.endLocalDate,
          gameIds: promo.gameIds,
        }));
      } catch (error) {
        console.error("Error fetching games on sale:", error);
      }

      // Apply filters on initial load
    });

    return {
      filters,
      categories,
      promotions,
      platforms,
      filteredProducts,
      searchStore,
      goToProductPage,
      applyFilters,
      toggleCategoryFilter,
      togglePlatformFilter,
      togglePromotionsFilter,
      toggleSearchFilter,
    };
  },
});
</script>

<style scoped>
.catalog-container {
  display: flex;
}

/* Sidebar Styles */
.sidebar {
  width: 250px;
  padding: 16px;
  border-right: 1px solid #ccc;
}

.filter-section {
  margin-bottom: 24px;
}

/* Products List Styles */
.products-list {
  flex: 1;
  padding: 16px;
}

/* Custom Checkbox Styles */
.custom-checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.custom-checkbox input[type="checkbox"] {
  appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid black;
  border-radius: 2px;
  outline: none;
  cursor: pointer;
  margin-right: 8px;
  position: relative;
}

.custom-checkbox input[type="checkbox"]:checked {
  background-color: black;
}

.custom-checkbox input[type="checkbox"]:checked::before {
  content: "✔";
  color: white;
  font-size: 16px;
  position: absolute;
  left: 2px;
  top: -2px;
}

.custom-checkbox label {
  cursor: pointer;
  font-size: 14px;
}
</style>
