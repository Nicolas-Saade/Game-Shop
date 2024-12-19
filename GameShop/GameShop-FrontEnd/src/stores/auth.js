// auth.js

import { defineStore } from "pinia";
import { useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import { useWishlistStore } from "@/stores/wishlist";

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
        console.log("Starting login process for:", email);

        const response = await fetch("http://localhost:8080/account/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password }),
        });

        if (!response.ok) {
          throw new Error("Invalid credentials or account not found.");
        }

        const data = await response.json();
        console.log("Login successful:", data);

        this.user = { email: data.email };
        this.accountType = data.type;

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
        const response = await fetch("http://localhost:8080/account/customer", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(accountData),
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Registration failed: ${errorText}`);
        }

        const data = await response.json();
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
