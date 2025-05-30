// auth.js
import { defineStore } from "pinia";
import { useRouter } from "vue-router";
// file we are currently testing
import { useCartStore } from "@/stores/cart";
import { useWishlistStore } from "@/stores/wishlist";
import api from "./../utils/axiosConfig";
import apiFetch from "./../utils/apiFetch";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null,
    accountType: null,
  }),

  actions: {
    loadFromLocalStorage() {
      const user = localStorage.getItem("user");
      const accountType = localStorage.getItem("accountType");

      if (user) {
        this.user = JSON.parse(user);
      }

      if (accountType) {
        this.accountType = JSON.parse(accountType);
      }
    },
    async login(email, password) {
      try {
        console.log("Starting login:", email, password);

        const response = await api.post("/account/login", { email, password });

        if (response.status !== 200) {
          console.log("Login failed:", response);
          console.log("Response status:", response.status);
          console.log("Response text:", response.statusText);
          throw new Error("Invalid credentials or account not found.", response);
        }

        // const data = await response.json();
        console.log("Login successful:", response.data);

        this.user = { email: response.data.email };
        this.accountType = response.data.type;

        localStorage.setItem("user", JSON.stringify(this.user));
        localStorage.setItem("accountType", JSON.stringify(this.accountType));

        const cartStore = useCartStore();
        await cartStore.fetchCart();
        const wishlistStore = useWishlistStore();
        await wishlistStore.fetchWishlist();
      } catch (error) {
        console.error("Login error:", error.message);
        this.user = null;
        this.accountType = null;
        throw error;
      }
    },

    async registerCustomer(accountData) {
      try {
        const data = await apiFetch("/account/customer", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(accountData),
        });
        console.log("Customer registration successful:", data);

        return data; // Return the registered account data
      } catch (error) {
        console.error("Registration error:", error.message);
        throw error;
      }
    },
    logout(router) {
      console.log("Logging out...");

      // Clear user state
      this.user = null;
      this.accountType = null;

      // Clear persistent storage if used
      localStorage.removeItem("user");
      localStorage.removeItem("accountType");

      // Redirect using the router instance
      if (router) {
        router.push({ name: "Catalog" });
      } else {
        console.error("Router instance is required for navigation.");
      }
    },
  },

  // Added the persist option
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'auth',
        storage: localStorage,
        paths: ['user', 'accountType'],
      },
    ],
  },

  created() {
    this.loadFromLocalStorage();
  },
});
