<template>
    <v-container>
      <h2>Manage Games</h2>
      <v-row justify="center" spacing="4">
        <v-col cols="auto">
          <v-btn color="primary" @click="router.push({ name: 'AddGame' })">Add a New Game</v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn color="primary" @click="router.push({ name: 'ViewAllGames' })">View All Games</v-btn>
        </v-col>
        <v-col cols="auto">
        <v-btn color="primary" @click="router.push({ name: 'ManagerViewsQuantity' })">View Games and Quantities</v-btn>
      </v-col>
      </v-row>
      <router-view></router-view>
    </v-container>
  </template>
  
  <script>
  import { defineComponent } from 'vue';
  import { useRouter } from 'vue-router';
  
  export default defineComponent({
    name: 'ManageGames',
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