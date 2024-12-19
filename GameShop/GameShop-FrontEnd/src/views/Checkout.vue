<template>
  <v-container>
    <h1>Checkout</h1>
    <div
      v-for="item in cartStore.cartItems"
      :key="item.gameId"
      class="checkout-item"
    >
      <p>{{ item.title }} (x{{ item.quantity }}) - ${{ item.price }}</p>
    </div>
    <h2>Total: ${{ (total || 0).toFixed(2) }}</h2>

    <v-form ref="checkoutForm" v-model="formValid">
      <v-textarea
        v-model="note"
        label="Note (Optional)"
        rows="3"
        maxlength="255"
      ></v-textarea>
      <v-text-field
        v-model="paymentCard"
        label="Payment Card Number"
        :rules="[cardValidation]"
        maxlength="16"
        required
      ></v-text-field>
    </v-form>

    <v-btn color="success" @click="completePurchase" :disabled="!formValid">
      Complete Purchase
    </v-btn>
  </v-container>
</template>

<script>
import { defineComponent, ref, computed } from "vue";
import { useCartStore } from "@/stores/cart";
import { useOrderStore } from "@/stores/order";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "Checkout",
  setup() {
    const cartStore = useCartStore();
    const orderStore = useOrderStore();
    const authStore = useAuthStore();
    const router = useRouter();

    const note = ref("");
    const paymentCard = ref("");
    const formValid = ref(false);
    const cardValidation = (value) =>
      value.length === 16 || "Card must be 16 digits.";

    const total = computed(() => {
      return cartStore.cartItems.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );
    });

    const completePurchase = async () => {
      const orderData = {
        orderDate: new Date(),
        note: note.value,
        paymentCard: paymentCard.value,
        customerEmail: authStore.user.email,
      };

      try {
        await orderStore.createOrder(orderData);
        cartStore.clearCart();
        router.push({ name: "OrderConfirmation" });
      } catch (error) {
        alert("Failed to complete the purchase.");
      }
    };

    return {
      cartStore,
      note,
      paymentCard,
      formValid,
      cardValidation,
      total,
      completePurchase,
    };
  },
});
</script>

<style scoped>
.checkout-item {
  margin-bottom: 16px;
}
</style>
