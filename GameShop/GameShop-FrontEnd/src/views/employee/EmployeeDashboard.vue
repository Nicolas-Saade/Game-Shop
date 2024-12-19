<template>
    <v-container>
      <h1>Employee Dashboard</h1>
      <v-row justify="center" spacing="4">
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ViewEmployees' })">View all Employees</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ViewCustomers' })">View all Customers</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ViewGameQuantity' })">View Game Quantities</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'RequestsGames' })">Request Addition of Game</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'SuggestGames' })">Request Deletion of Game</v-btn>
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
    name: 'EmployeeDashboard',
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
  