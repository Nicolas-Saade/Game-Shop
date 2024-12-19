<template>
  <v-container>
    <h1>Order Confirmation</h1>
    <p>
      <strong>Tracking Number:</strong>
      {{ orderStore.currentOrder.trackingNumber }}
    </p>
    <p>
      <strong>Order Date:</strong>
      {{ formatDate(orderStore.currentOrder.orderDate) }}
    </p>
    <p>Thank you for your purchase!</p>

    <v-btn color="primary" @click="goToCatalog">Back to Catalog</v-btn>
  </v-container>
</template>

<script>
import { defineComponent } from "vue";
import { useOrderStore } from "@/stores/order";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "OrderConfirmation",
  setup() {
    const orderStore = useOrderStore();
    const router = useRouter();

    const goToCatalog = () => {
      router.push({ name: "Catalog" });
    };

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    };
    return {
      orderStore,
      goToCatalog,
      formatDate,
    };
  },
});
</script>

<style scoped>
p {
  font-size: 1.2em;
  margin: 10px 0;
}
</style>
