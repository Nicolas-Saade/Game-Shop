<template>
  <v-card
    class="product-item"
    @click="goToProductPage(props.productData.gameId)"
  >
    <v-img :src="props.productData.photoUrl" height="200px" cover />
    <v-card-title>{{ props.productData.title }}</v-card-title>

    <!-- <v-card-subtitle>$ {{ props.productData.price }}</v-card-subtitle> -->

    <!-- Display PRices with Promo Logic -->
    <v-card-subtitle>
      <!-- Show discounted price if it exists, otherwise show original price -->
      <span v-if="props.productData.originalPrice && props.productData.price !== props.productData.originalPrice">
        <span class="original-price">
          $ {{ props.productData.originalPrice }}
        </span>
        <span class="discounted-price">
          $ {{ props.productData.price }}
        </span>
      </span>
      <span v-else>
        $ {{ props.productData.price }}
      </span>
    </v-card-subtitle>

    <v-card-text>{{ props.productData.description }}</v-card-text>
    <v-card-actions>
      <v-btn v-if="isCustomer" @click.stop="addToCart(props.productData)">
        Add to cart
      </v-btn>
      <v-btn
        v-if="isCustomer"
        @click.stop="addToWishlist(props.productData)"
        color="secondary"
        variant="elevated"
      >
        Add to Wishlist
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import { productsStore } from "@/stores/products";
import { useCartStore } from "@/stores/cart";
import { useAuthStore } from "@/stores/auth";
import { useWishlistStore } from "@/stores/wishlist";
import { computed } from "vue";

const props = defineProps({
  productData: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["item-clicked"]);
const store = productsStore();
const cartStore = useCartStore();
const authStore = useAuthStore();
const wishlistStore = useWishlistStore();

const isCustomer = computed(() => authStore.accountType === "CUSTOMER");

const goToProductPage = (productId) => {
  emit("item-clicked", productId);
};

const addToCart = async (product) => {
  try {
    await cartStore.addGameToCart(product.gameId, 1);
  } catch (error) {
    console.error("Error adding to cart:", error);
  }
};

const addToWishlist = async (product) => {
  try {
    await wishlistStore.addGameToWishlist(product.gameId);
  } catch (error) {
    console.error("Error adding to wishlist:", error);
  }
};
</script>

<style scoped>
.product-item {
  cursor: pointer;
}

.original-price {
  text-decoration: line-through;
  color: red; /* Optional: Highlight the original price */
  margin-right: 8px; /* Add spacing between original and discounted price */
}

.discounted-price {
  color: green; /* Optional: Highlight the discounted price */
  font-weight: bold; /* Make it stand out */
}

</style>
