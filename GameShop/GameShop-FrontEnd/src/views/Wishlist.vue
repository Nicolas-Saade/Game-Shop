<template>
  <v-btn @click="goToCatalog" color="primary" variant="elevated">
    Back to Catalog
  </v-btn>

  <div
    v-if="!wishlistStore.wishlistItems.length"
    style="text-align: center; margin-top: 50px"
  >
    <h1>Your Wishlist is Empty</h1>
  </div>
  <div class="wishlist-items" v-else>
    <div
      class="wishlist-item"
      v-for="item in wishlistStore.wishlistItems"
      :key="item.gameId"
    >
      <div class="item-details">
        <img :src="item.photoUrl" alt="Product Image" />
        <div class="item-text">
          <p><strong>Title:</strong> {{ item.title }}</p>
          <p><strong>Price:</strong> ${{ item.price.toFixed(2) }}</p>
        </div>
      </div>
      <div class="buttons">
        <v-btn @click="removeFromWishlist(item.gameId)" color="error"
          >Remove</v-btn
        >
        <v-btn @click="addToCart(item.gameId)" color="primary"
          >Add to Cart</v-btn
        >
      </div>
    </div>

    <!-- Move All to Cart Button -->
    <div class="move-all-button">
      <v-btn color="success" @click="moveAllToCart"> Move All to Cart </v-btn>
    </div>
  </div>
</template>

<script>
import { defineComponent, onMounted } from "vue";
import { useWishlistStore } from "@/stores/wishlist";
import { useCartStore } from "@/stores/cart";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "WishlistView",
  setup() {
    const wishlistStore = useWishlistStore();
    const cartStore = useCartStore();
    const router = useRouter();

    const goToCatalog = () => {
      router.push({ name: "Catalog" });
    };

    const removeFromWishlist = async (gameId) => {
      await wishlistStore.removeGameFromWishlist(gameId);
    };

    const addToCart = async (gameId) => {
      await cartStore.addGameToCart(gameId, 1);
      await wishlistStore.removeGameFromWishlist(gameId);
    };

    const moveAllToCart = async () => {
      for (const item of wishlistStore.wishlistItems) {
        await cartStore.addGameToCart(item.gameId, 1);
      }
      await wishlistStore.clearWishlist();
    };

    onMounted(() => {
      wishlistStore.wishlistItems;
    });

    return {
      wishlistStore,
      goToCatalog,
      removeFromWishlist,
      addToCart,
      moveAllToCart,
    };
  },
});
</script>

<style scoped>
.wishlist-items {
  margin-top: 30px;
  padding: 16px;
}

.wishlist-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background-color: #ffffff;
}

.item-details {
  display: flex;
  flex: 1;
  align-items: center;
}

.item-details img {
  width: 120px;
  height: auto;
  border-radius: 8px;
  margin-right: 16px;
}

.item-text {
  display: flex;
  flex-direction: column;
}

.buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.move-all-button {
  text-align: right;
  margin-top: 20px;
}
</style>
