<template>
  <v-container>
    <v-btn text @click="goBack">
      <v-icon left>mdi-arrow-left</v-icon> Back to Orders
    </v-btn>
    <div v-if="loading">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>
    <div v-else-if="error">
      <v-alert type="error">{{ error }}</v-alert>
    </div>
    <div v-else-if="!order">
      <v-alert type="info">No order details available.</v-alert>
    </div>
    <div v-else>
      <v-card class="mt-4">
        <v-card-title>
          <v-icon class="mr-2">mdi-truck</v-icon>
          Order Details - #{{ order.trackingNumber }}
        </v-card-title>
        <v-card-subtitle>
          <v-icon class="mr-2">mdi-calendar</v-icon>
          {{ formattedOrderDate }}
        </v-card-subtitle>
        <v-divider></v-divider>
        <v-card-text>
          <v-list dense>
            <v-list-item
              v-for="(game, index) in order.games"
              :key="index"
              :class="{ returned: game.itemStatus === 'Returned' }"
            >
              <v-list-item-content>
                <v-list-item-title>{{ game.title }}</v-list-item-title>
                <v-list-item-subtitle>
                  Quantity: {{ game.quantity }} - Status:
                  {{ game.itemStatus }} - ${{ game.price.toFixed(2) }} each
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-action>
                <v-btn
                  color="error"
                  text
                  @click="returnGame(game)"
                  :disabled="game.itemStatus === 'Returned'"
                >
                  Return
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions class="justify-end">
          <v-chip color="green" text-color="white">
            Total: ${{ order.totalPrice.toFixed(2) }}
          </v-chip>
        </v-card-actions>
      </v-card>
    </div>
  </v-container>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useOrderStore } from "@/stores/order";

export default defineComponent({
  name: "OrderDetails",
  setup() {
    const route = useRoute();
    const router = useRouter();
    const orderStore = useOrderStore();
    const loading = ref(false);
    const error = ref("");
    const order = ref(null);

    const fetchOrderDetails = async () => {
      const trackingNumber = route.params.trackingNumber;
      try {
        loading.value = true;
        await orderStore.getOrderByTrackingNumber(trackingNumber);
        order.value = orderStore.currentOrder;

        if (!order.value) {
          throw new Error("Order not found");
        }
      } catch (err) {
        error.value = "Failed to fetch order details. Please try again.";
        console.error("Error in fetchOrderDetails:", err);
      } finally {
        loading.value = false;
      }
    };

    const formattedOrderDate = computed(() => {
      if (!order.value || !order.value.orderDate) return "";
      const date = new Date(order.value.orderDate);
      const options = { year: "numeric", month: "long", day: "numeric" };
      return date.toLocaleDateString(undefined, options);
    });

    const goBack = () => {
      router.push({ name: "OrderHistory" });
    };

    const returnGame = async (game) => {
      console.log("Game object:", game);
      const specificGameId =
        game.specificGameId || game.specificGame_id || game.aSpecificGame_id;
      console.log("Using specificGameId:", specificGameId);

      if (!specificGameId) {
        alert("Cannot return this game due to missing specificGameId.");
        return;
      }

      const confirmed = confirm(
        `Are you sure you want to return ${game.title}?`
      );
      if (!confirmed) {
        return;
      }

      try {
        loading.value = true;

        // Call the return method in the order store
        await orderStore.returnGame(order.value.trackingNumber, specificGameId);

        // Update the order details after return
        await fetchOrderDetails();

        // Show success message
        alert(`Successfully returned ${game.title}.`);
      } catch (err) {
        error.value = "Failed to return game. Please try again.";
        console.error("Error returning game:", err);
      } finally {
        loading.value = false;
      }
    };

    onMounted(fetchOrderDetails);

    return {
      loading,
      error,
      order,
      formattedOrderDate,
      goBack,
      returnGame,
    };
  },
});
</script>

<style scoped>
.v-card-title,
.v-card-subtitle {
  display: flex;
  align-items: center;
}

.returned {
  opacity: 0.5;
  filter: grayscale(100%);
}
</style>
