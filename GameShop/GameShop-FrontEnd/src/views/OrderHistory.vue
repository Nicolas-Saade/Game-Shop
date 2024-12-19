<template>
  <v-container>
    <h1>Your Orders</h1>
    <div v-if="loading">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>
    <div v-else-if="error">
      <v-alert type="error">{{ error }}</v-alert>
    </div>
    <div v-else-if="!orderStore.orders.length">
      <v-alert type="info">No orders found for your account.</v-alert>
      <v-btn color="primary" @click="goToCatalog">Start Shopping</v-btn>
    </div>
    <div v-else>
      <v-row>
        <v-col
          cols="12"
          md="6"
          v-for="order in orderStore.orders"
          :key="order.trackingNumber"
        >
          <v-card class="mb-4">
            <v-card-title>
              <v-icon class="mr-2">mdi-truck</v-icon>
              Order #{{ order.trackingNumber }}
            </v-card-title>
            <v-card-subtitle>
              <v-icon class="mr-2">mdi-calendar</v-icon>
              {{ formatDate(order.orderDate) }}
            </v-card-subtitle>
            <v-divider></v-divider>
            <v-card-text>
              <v-list dense>
                <v-list-item v-for="(game, index) in order.games" :key="index">
                  <v-list-item-content>
                    <v-list-item-title>{{ game.title }}</v-list-item-title>
                    <v-list-item-subtitle>
                      Quantity: {{ game.quantity }} - ${{
                        game.price.toFixed(2)
                      }}
                      each
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card-text>
            <v-card-actions class="justify-space-between">
              <v-btn
                color="primary"
                text
                @click="viewOrder(order.trackingNumber)"
              >
                View Details
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import { defineComponent, onMounted, ref } from "vue";
import { useOrderStore } from "@/stores/order";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "OrderHistory",
  setup() {
    const orderStore = useOrderStore();
    const router = useRouter();
    const loading = ref(false);
    const error = ref("");

    const fetchOrders = async () => {
      try {
        loading.value = true;
        await orderStore.fetchOrders();
        if (!orderStore.orders.length) {
          error.value = "No orders found for your account.";
        }
      } catch (err) {
        error.value = "Failed to fetch orders. Please try again.";
      } finally {
        loading.value = false;
      }
    };

    const goToCatalog = () => {
      router.push({ name: "Catalog" });
    };

    const viewOrder = (trackingNumber) => {
      router.push({ name: "OrderDetails", params: { trackingNumber } });
    };

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      const options = { year: "numeric", month: "long", day: "numeric" };
      return date.toLocaleDateString(undefined, options);
    };

    onMounted(() => {
      fetchOrders();
    });

    return {
      orderStore,
      loading,
      error,
      goToCatalog,
      viewOrder,
      formatDate,
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

.v-card-title span {
  cursor: pointer;
}
</style>
