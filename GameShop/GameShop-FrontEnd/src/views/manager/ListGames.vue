<template>
    <v-container>
      <h2 class="text-center mb-4">All Games</h2>
      <v-btn color="primary" class="mb-4" @click="sortGames">Sort Alphabetically</v-btn>
      <v-list>
        <v-list-item
          v-for="game in games"
          :key="game.gameId"
          @click="editGame(game.gameId)"
          class="hoverable game-item"
        >
          <div>
            <strong>{{ game.title }}</strong>
          </div>
          <v-icon color="blue">mdi-pencil</v-icon>
        </v-list-item>
      </v-list>
    </v-container>
  </template>
  
  <script>
  import { defineComponent, ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { VContainer, VList, VListItem, VIcon } from 'vuetify/components';
  
  export default defineComponent({
    name: 'ListGames',
    components: {
      VContainer,
      VList,
      VListItem,
      VIcon,
    },
    setup() {
      const router = useRouter();
      const games = ref([]);
  
      const fetchGames = async () => {
        try {
          const response = await fetch('http://localhost:8080/games');
          const data = await response.json();
          games.value = data.games || [];
        } catch (error) {
          console.error('Error fetching games:', error);
        }
      };
  
      const sortGames = () => {
        games.value.sort((a, b) => a.title.localeCompare(b.title));
      };
  
      const editGame = (gameId) => {
        router.push({ name: 'EditGame', params: { id: gameId } });
      };
  
      onMounted(() => {
        fetchGames().then(() => sortGames()); // Fetch and sort the games
      });
  
      return {
        games,
        sortGames,
        editGame,
      };
    },
  });
  </script>
  
  <style scoped>
  .hoverable {
    cursor: pointer;
    transition: background-color 0.3s;
  }
  .hoverable:hover {
    background-color: #f5f5f5;
  }
  .game-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #ddd;
    padding: 10px 0;
  }
  .text-center {
    text-align: center;
  }
  </style>
  