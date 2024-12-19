<template>
  <v-btn @click="goToCatalog" color="primary" variant="elevated">
    Back to catalog
  </v-btn>
  <div
    v-if="!cartStore.cartItems.length"
    style="text-align: center; margin-top: 50px"
  >
    <h1>Your Cart is Empty</h1>
  </div>
  <div class="cart-items" v-else>
    <div
      class="cart-item"
      v-for="item in cartStore.cartItems"
      :key="item.gameId"
    >
      <div class="item-details">
        <img :src="item.photoUrl" alt="Product Image" />
        <div class="item-text">
          <p><strong>Title:</strong> {{ item.title }}</p>
          <p><strong>Price:</strong> ${{ item.price.toFixed(2) }}</p>
          <p><strong>Quantity:</strong> {{ item.quantity }}</p>
        </div>
      </div>
      <div class="buttons">
        <v-btn @click="decreaseQuantity(item.gameId)" color="primary">-</v-btn>
        <v-btn @click="increaseQuantity(item.gameId)" color="primary">+</v-btn>
        <v-btn @click="removeAllFromCart(item.gameId)" color="error"
          >Remove</v-btn
        >
      </div>
    </div>

    <!-- Total Price Section -->
    <div class="total-price">
      <h2>Total: ${{ cartStore.totalPrice.toFixed(2) }}</h2>
    </div>

    <!-- Proceed to Checkout Button -->
    <div class="action-buttons">
      <v-btn color="error" @click="clearCart">Clear Cart</v-btn>
      <v-btn color="success" @click="proceedToCheckout">
        Proceed to Checkout
      </v-btn>
    </div>
  </div>
</template>

<script>
import { defineComponent, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import { useAuthStore } from "@/stores/auth";

export default defineComponent({
  name: "CartView",
  setup() {
    const cartStore = useCartStore();
    const router = useRouter();
    const auth = useAuthStore();

    onMounted(async () => {
      if (auth.user) {
        // await cartStore.fetchCart();
        cartStore.cartItems;
      } else {
        router.push({ name: "Login" });
      }
    });

    const goToCatalog = () => {
      router.push({ name: "Catalog" });
    };

    const increaseQuantity = async (gameId) => {
      const item = cartStore.cartItems.find((item) => item.gameId === gameId);
      if (item) {
        await cartStore.updateGameQuantity(gameId, item.quantity + 1);
      }
    };

    const decreaseQuantity = async (gameId) => {
      const item = cartStore.cartItems.find((item) => item.gameId === gameId);
      if (item) {
        if (item.quantity > 1) {
          await cartStore.updateGameQuantity(gameId, item.quantity - 1);
        } else {
          await cartStore.removeGameFromCart(gameId, item.quantity);
        }
      }
    };

    const removeAllFromCart = async (gameId) => {
      const item = cartStore.cartItems.find((item) => item.gameId === gameId);
      if (item) {
        await cartStore.removeGameFromCart(gameId, item.quantity);
      }
    };

    const clearCart = async () => {
      try {
        await cartStore.clearCart();
      } catch (error) {
        console.error("Error clearing cart:", error);
      }
    };

    const proceedToCheckout = () => {
      if (auth.user) {
        router.push({ name: "Checkout" });
      } else {
        router.push({ name: "Login" });
      }
    };

    return {
      cartStore,
      goToCatalog,
      increaseQuantity,
      decreaseQuantity,
      removeAllFromCart,
      clearCart,
      proceedToCheckout,
    };
  },
});
</script>

<style scoped>
.cart-items {
  margin-top: 30px;
  padding: 16px;
}

.cart-item {
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

.total-price {
  text-align: right;
  font-size: 1.5em;
  font-weight: bold;
  margin-top: 20px;
}

.action-buttons {
  text-align: right;
  margin-top: 20px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
