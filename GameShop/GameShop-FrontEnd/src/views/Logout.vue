<template>
    <v-container>
      <h1>Logging Out...</h1>
    </v-container>
  </template>
  
  <script>
  import { useAuthStore } from "@/stores/auth";
  import { useRouter } from "vue-router";
  
  export default {
    name: "Logout",
    setup() {
      const auth = useAuthStore();
      const router = useRouter(); // Get the router instance
  
      // Perform logout and redirect to the Game Catalog
      const performLogout = async () => {
        try {
          auth.logout(router); // Pass the router instance to the logout function
        } catch (error) {
          console.error("Logout failed:", error);
        }
      };
  
      performLogout(); // Call the logout logic when the page loads
  
      return {};
    },
  };
  </script>
  