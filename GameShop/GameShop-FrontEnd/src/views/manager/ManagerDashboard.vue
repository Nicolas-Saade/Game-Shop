<template>
  <v-container>
    <h1 class="text-center mb-5">Manager Dashboard</h1>
    
    <v-row justify="center" spacing="4">
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ManageEmployees' })">Manage Employees</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ListCustomers' })">View all Customers</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ManageGames' })">Manage Games</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ManageCategories' })">Manage Categories</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ManagePlatforms' })">Manage Platforms</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ManagePromotions' })">Manage Promotions</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'Catalog' })">Browse Catalog</v-btn>
      </v-col>
    </v-row>
    <router-view></router-view>
  </v-container>
</template>


  
  <script>
  import { defineComponent } from 'vue';
  import { useRouter } from 'vue-router';
  
  export default defineComponent({
    name: 'ManagerDashboard',
    setup() {
      const router = useRouter();
  
      return {
        router,
      };
    },
  });
  </script>
  
  <style scoped>
  </style>
  