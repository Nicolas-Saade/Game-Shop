<template>
  <v-container>
    <h2>Add New Game</h2>
    <v-form ref="form" v-model="valid" @submit.prevent="submitForm">
      <v-text-field
        v-model="game.aTitle"
        label="Game Title"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-textarea
        v-model="game.aDescription"
        label="Description"
        :rules="[rules.required]"
        required
      ></v-textarea>
      <v-text-field
        v-model.number="game.aPrice"
        label="Price"
        type="number"
        :rules="[rules.required, rules.number]"
        required
      ></v-text-field>
      <v-select
        v-model="game.aGameStatus"
        :items="gameStatuses"
        label="Game Status"
        :rules="[rules.required]"
        required
      ></v-select>
      <v-text-field
        v-model.number="game.aStockQuantity"
        label="Stock Quantity"
        type="number"
        :rules="[rules.required, rules.number]"
        required
      ></v-text-field>
      <v-text-field
        v-model="game.aPhotoUrl"
        label="Photo URL"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-select
        v-model="game.categories"
        :items="categories"
        label="Categories"
        multiple
        :rules="[rules.required]"
        required
      ></v-select>
      <v-select
        v-model="game.platforms"
        :items="platforms"
        label="Platforms"
        multiple
        :rules="[rules.required]"
        required
      ></v-select>
      <v-btn :disabled="!valid" type="submit" color="success">Add Game</v-btn>
      <v-btn @click="clearForm" color="error" text>Clear</v-btn>
    </v-form>
  </v-container>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { productsStore } from '@/stores/products';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'AddGame',
  setup() {
    const store = productsStore();
    const router = useRouter();

    const game = ref({
      aTitle: '',
      aDescription: '',
      aPrice: null,
      aGameStatus: '',
      aStockQuantity: null,
      aPhotoUrl: '',
      categories: [],
      platforms: [],
    });

    const categories = ref([]);
    const platforms = ref([]);
    const categoryMap = ref({});
    const platformMap = ref({});
    const gameStatuses = ref(['InStock', 'OutOfStock', 'Archived']);
    const valid = ref(false);
    const form = ref(null);

    const rules = {
      required: (value) => !!value || 'Required.',
      number: (value) => !isNaN(value) || 'Must be a number.',
    };

    const fetchCategories = async () => {
      try {
        const response = await fetch('http://localhost:8080/categories');
        const data = await response.json();
        categories.value = data.categories.map((cat) => cat.categoryName);
        categoryMap.value = {};
        data.categories.forEach((cat) => {
          categoryMap.value[cat.categoryName] = cat.categoryId;
        });
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };

    const fetchPlatforms = async () => {
      try {
        const response = await fetch('http://localhost:8080/platforms');
        const data = await response.json();
        platforms.value = data.platforms.map((plat) => plat.platformName);
        platformMap.value = {};
        data.platforms.forEach((plat) => {
          platformMap.value[plat.platformName] = plat.platformId;
        });
      } catch (error) {
        console.error('Error fetching platforms:', error);
      }
    };

    const submitForm = async () => {
      try {
        const categoryIds = game.value.categories.map((name) => categoryMap.value[name]);
        const platformIds = game.value.platforms.map((name) => platformMap.value[name]);

        await store.addGame({
          aTitle: game.value.aTitle,
          aDescription: game.value.aDescription,
          aPrice: game.value.aPrice,
          aGameStatus: game.value.aGameStatus,
          aStockQuantity: game.value.aStockQuantity,
          aPhotoUrl: game.value.aPhotoUrl,
          categories: categoryIds,
          platforms: platformIds,
        });
        router.push({ name: 'ManagerDashboard' });
      } catch (error) {
        console.error('Error adding game:', error);
        alert('Failed to add game.');
      }
    };

    const clearForm = () => {
      game.value = {
        aTitle: '',
        aDescription: '',
        aPrice: null,
        aGameStatus: '',
        aStockQuantity: null,
        aPhotoUrl: '',
        categories: [],
        platforms: [],
      };
    };

    onMounted(async () => {
      await fetchCategories();
      await fetchPlatforms();
    });

    return {
      game,
      categories,
      platforms,
      gameStatuses,
      valid,
      form,
      rules,
      submitForm,
      clearForm,
    };
  },
});
</script>

