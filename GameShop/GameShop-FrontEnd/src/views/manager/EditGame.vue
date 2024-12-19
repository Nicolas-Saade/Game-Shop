<template>
  <v-container>
    <h2>Edit Game</h2>
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
      <!-- Categories Selection -->
      <v-select
        v-model="game.categories"
        :items="categories"
        label="Categories"
        multiple
        :rules="[rules.required]"
        required
      ></v-select>
      <!-- Platforms Selection -->
      <v-select
        v-model="game.platforms"
        :items="platforms"
        label="Platforms"
        multiple
        :rules="[rules.required]"
        required
      ></v-select>
      <v-btn :disabled="!valid" type="submit" color="success">Save Changes</v-btn>
      <v-btn @click="cancelEdit" color="error" text>Cancel</v-btn>
    </v-form>
  </v-container>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { productsStore } from '@/stores/products'; // Adjust the import if necessary

export default defineComponent({
  name: 'EditGame',
  setup() {
    const store = productsStore();
    const router = useRouter();
    const route = useRoute();

    const gameId = route.params.id; // Extract game ID from route params

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

    const fetchGameDetails = async () => {
      try {
        const response = await fetch(`http://localhost:8080/games/${gameId}`);
        const data = await response.json();
        console.log('Fetched game data:', data); // Debugging log

        // // Map category and platform IDs to names for display
        // const categoryNames = data.categories.map((id) => {
        //   return Object.keys(categoryMap.value).find(
        //     (key) => categoryMap.value[key] === id
        //   );
        // });

        // const platformNames = data.platforms.map((id) => {
        //   return Object.keys(platformMap.value).find(
        //     (key) => platformMap.value[key] === id
        //   );
        // });

        game.value = {
          aTitle: data.aTitle,
          aDescription: data.aDescription,
          aPrice: data.aPrice,
          aGameStatus: data.aGameStatus,
          aStockQuantity: data.aStockQuantity,
          aPhotoUrl: data.aPhotoUrl,
          categories: data.categories.categories.map((category) => category.categoryName),
          platforms: data.platforms.platforms.map((platform) => platform.platformName),
        };
      } catch (error) {
        console.error('Error fetching game details:', error);
        alert('Failed to fetch game details.');
      }
    };

    const submitForm = async () => {
      try {
        const categoryIds = game.value.categories.map((name) => categoryMap.value[name]);
        const platformIds = game.value.platforms.map((name) => platformMap.value[name]);

        const updatedGame = {
          aTitle: game.value.aTitle,
          aDescription: game.value.aDescription,
          aPrice: game.value.aPrice,
          aGameStatus: game.value.aGameStatus,
          aStockQuantity: game.value.aStockQuantity,
          aPhotoUrl: game.value.aPhotoUrl,
          categories: categoryIds,
          platforms: platformIds,
        };

        await fetch(`http://localhost:8080/games/${gameId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedGame),
        });

        router.push({ name: 'ViewAllGames' });
      } catch (error) {
        console.error('Error updating game:', error);
        alert('Failed to update game.');
      }
    };

    const cancelEdit = () => {
      router.push({ name: 'ViewAllGames' });
    };

    onMounted(async () => {
      await fetchCategories();
      await fetchPlatforms();
      await fetchGameDetails();
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
      cancelEdit,
    };
  },
});
</script>

