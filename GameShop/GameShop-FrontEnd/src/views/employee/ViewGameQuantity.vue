<template>
    <v-container>
    <h1>View Stock Quantity</h1>

    <v-simple-table dense>
        <thead>
        <tr>
            <th>Game Title</th>
            <th>Stock Quantity</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="game in games" :key="game.game_id">
            <td>{{ game.title }}</td>
            <td>{{ game.stockQuantity }}</td>
        </tr>
        </tbody>
</v-simple-table>

    </v-container>
<v-btn @click="goBack">Back</v-btn>
</template>

<script>
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';

export default defineComponent({
  name: 'ViewGameQuantity',
  setup() {
    const router = useRouter();
    const games = ref([]);
    const errorMessage = ref("");



    const goBack = () => {
        router.push("/employee");
    };

    const fetchGames = async () => {
        try {
            console.log("Fetching games...");

            const response = await fetch("http://localhost:8080/games", {
                method: "GET",
            });

            console.log("API response status:", response.status);

            if (!response.ok) {
                throw new Error(`Failed to fetch games. Status: ${response.status}`);
            }

            const data = await response.json();
            console.log("API response data:", data);

            games.value = data.games;
        } catch (error) {
            console.error("Failed to fetch games:", error);
            errorMessage.value = "Failed to fetch games. Please try again.";
        }
    };

    onMounted(() => {
        fetchGames();
    });
    return {
      router,
        games,
        errorMessage,
        goBack,
    };
  },
});

</script>

<style scoped>
  v-simple-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1em;
  }
  
  thead th {
    text-align: left;
    background: #f5f5f5;
    padding: 0.5em;
  }
  
  tbody td {
    padding: 0.5em;
  }
  </style>