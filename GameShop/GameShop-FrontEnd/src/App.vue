<template>
  <div>
    <v-app>
      <v-toolbar>
        <router-link
          to="/"
          class="title-link"
          style="
            text-decoration: none;
            color: black;
            margin-left: 15px;
            font-size: 20px;
          "
        >
          <span class="title">Game Shop</span>
        </router-link>
        
        <v-spacer></v-spacer>
        <v-btn
          v-if="isCustomer"
          @click="goToWishlist"
          color="secondary"
          variant="elevated"
        >
          Wishlist ({{ wishlistItemCount }})
        </v-btn>
        <v-btn
          v-if="isCustomer"
          @click="goToCart"
          color="primary"
          variant="elevated"
        >
          Cart ({{ cartItemCount }})
        </v-btn>
        <v-btn
          v-if="isCustomer"
          @click="router.push({ name: 'OrderHistory' })"
          color="primary"
          variant="elevated"
        >
          Order History
        </v-btn>
        <v-btn
          v-if="isManager || isEmployee || isCustomer"
          @click="goToUpdateAccount"
          color="primary"
          variant="elevated"
        >
          Update Account
        </v-btn>
        <v-btn
          v-if="isManager"
          @click="goToManagerDashboard"
          color="primary"
          variant="elevated"
        >
          Manager Dashboard
        </v-btn>
        <v-btn
          v-if="isEmployee"
          @click="goToEmployeeDashboard"
          color="primary"
          variant="elevated"
        >
          Employee Dashboard
        </v-btn>

        <v-btn
          v-if="auth.user"
          @click="router.push({ name: 'Logout' })"
          color="secondary"
          variant="elevated"
        >
          Logout
        </v-btn>
        <v-btn
          v-else
          @click="router.push({ name: 'Login' })"
          color="secondary"
          variant="elevated"
        >
          Login
        </v-btn>
      </v-toolbar>
      <router-view />
    </v-app>
  </div>
</template>

<script>
import { defineComponent, computed, onMounted } from "vue";
import { createWebHistory, useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import { productsStore } from "@/stores/products";
import { useAuthStore } from "@/stores/auth";
import { useWishlistStore } from "@/stores/wishlist";

export default defineComponent({
  name: "App",
  computed: {
    isManager() {
      const authStore = useAuthStore();
      return authStore.accountType === "MANAGER";
    },
    isEmployee() {
      const authStore = useAuthStore();
      return authStore.accountType === "EMPLOYEE";
    },
    isCustomer() {
      const authStore = useAuthStore();
      return authStore.accountType === "CUSTOMER";
    },
  },

  setup() {
    const router = useRouter();
    const store = productsStore();
    const auth = useAuthStore();
    const cartStore = useCartStore();
    const wishlistStore = useWishlistStore();

    const cartItemCount = computed(() =>
      cartStore.cartItems.reduce((total, item) => total + item.quantity, 0)
    );
    const wishlistItemCount = computed(
      () => wishlistStore.wishlistItems.length
    );
    const goToEmployeeDashboard = () => {
      router.push({ name: "EmployeeDashboard" });
    };
    const goToManagerDashboard = () => {
      router.push({ name: "ManagerDashboard" });
    };

    const goToCart = () => {
      router.push({ name: "CartView" });
    };

    const goToUpdateAccount = () => {
      router.push("/update-account");
    };

    onMounted(() => {
      // Ensure wishlist is fetched for customers
      if (auth.user && auth.accountType === "CUSTOMER") {
        wishlistStore.fetchWishlist();
      }

      // Ensure cart is fetched for customers
      if (auth.user) {
        cartStore.initializeCart();
        wishlistStore.initializeWishlist();
      }
    });

    const logout = () => {
      auth.logout();
      router.push({ name: "Catalog" });
    };

    const goToWishlist = () => {
      router.push({ name: "WishlistView" });
    };

    return {
      router,
      store,
      auth,
      cartItemCount,
      goToCart,
      logout,
      goToUpdateAccount,
      goToManagerDashboard,
      goToEmployeeDashboard,
      wishlistItemCount,
      goToWishlist,
    };
  },
});
</script>
