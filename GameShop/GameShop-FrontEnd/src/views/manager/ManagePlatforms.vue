<template>
    <v-container>
      <v-row class="align-center mb-4">
        <v-col>
          <h1>Manage Platforms</h1>
        </v-col>
        <v-col class="text-right">
          <v-btn color="green" @click="showCreateDialog = true">Create Platform</v-btn>
          <v-btn color="red darken-1" @click="showDeleteDialog = true">Delete Platform</v-btn>
        </v-col>
      </v-row>
  
      <v-list two-line>
        <v-list-item
          v-for="platform in sortedPlatforms"
          :key="platform"
          @click="editPlatform(platform)"
          class="hoverable"
        >
          <v-list-item-content>
            <v-list-item-title>{{ platform }}</v-list-item-title>
          </v-list-item-content>
          <v-list-item-icon>
            <v-icon>mdi-pencil</v-icon>
          </v-list-item-icon>
        </v-list-item>
      </v-list>
  
      <v-dialog v-model="showCreateDialog" max-width="500px">
        <v-card>
          <v-card-title>
            <span class="headline">Create Platform</span>
          </v-card-title>
          <v-card-text>
            <v-text-field label="Platform Name" v-model="newPlatformName"></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-1" text @click="createPlatform">Create</v-btn>
            <v-btn color="grey darken-1" text @click="showCreateDialog = false">Cancel</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <v-dialog v-model="showEditDialog" max-width="500px">
        <v-card>
          <v-card-title>
            <span class="headline">Edit Platform</span>
          </v-card-title>
          <v-card-text>
            <v-text-field label="Platform Name" v-model="selectedPlatformName"></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="updatePlatform">Save</v-btn>
            <v-btn color="grey darken-1" text @click="showEditDialog = false">Cancel</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <v-dialog v-model="showDeleteDialog" max-width="500px">
        <v-card>
          <v-card-title>
            <span class="headline">Delete Platform</span>
          </v-card-title>
          <v-card-text>
            <v-select
              label="Select Platform to Delete"
              :items="platforms"
              v-model="platformToDeleteName"
            ></v-select>
          </v-card-text>
          <v-card-text>
            <v-alert type="error" v-if="deleteError">{{ deleteError }}</v-alert>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="red darken-1" text @click="deletePlatform">Delete</v-btn>
            <v-btn color="grey darken-1" text @click="showDeleteDialog = false">Cancel</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </template>
  
  <script>
  import { defineComponent, ref, onMounted, computed } from 'vue';
  import axios from 'axios';
  
  export default defineComponent({
    name: 'ManagePlatforms',
    setup() {
      const platforms = ref([]);
      const platformMap = ref({});
      const newPlatformName = ref('');
      const showCreateDialog = ref(false);
      const showEditDialog = ref(false);
      const showDeleteDialog = ref(false);
      const selectedPlatformName = ref('');
      const selectedPlatformId = ref(null);
      const platformToDeleteName = ref('');
      const deleteError = ref('');
  
      const fetchPlatforms = async () => {
        try {
          const response = await axios.get('http://localhost:8080/platforms');
          const data = response.data.platforms;
          platforms.value = data.map((plat) => plat.platformName);
          platformMap.value = {};
          data.forEach((plat) => {
            platformMap.value[plat.platformName] = plat.platformId;
          });
        } catch (error) {
          console.error('Error fetching platforms:', error);
        }
      };
  
      const createPlatform = async () => {
        try {
          await axios.post('http://localhost:8080/platforms', {
            platformName: newPlatformName.value,
            managerEmail: 'manager@manager.com', // Replace with actual manager email
          });
          showCreateDialog.value = false;
          newPlatformName.value = '';
          await fetchPlatforms();
        } catch (error) {
          console.error('Error creating platform:', error);
        }
      };
  
      const editPlatform = (platformName) => {
        selectedPlatformName.value = platformName;
        selectedPlatformId.value = platformMap.value[platformName];
        showEditDialog.value = true;
      };
  
      const updatePlatform = async () => {
        if (!selectedPlatformId.value) {
          console.error('No platform selected for update.');
          return;
        }
        try {
          await axios.put(`http://localhost:8080/platforms/${selectedPlatformId.value}`, {
            platformName: selectedPlatformName.value,
          });
          showEditDialog.value = false;
          await fetchPlatforms();
        } catch (error) {
          console.error('Error updating platform:', error);
        }
      };
  
      const deletePlatform = async () => {
        if (!platformToDeleteName.value) {
          deleteError.value = 'Please select a platform to delete.';
          return;
        }
        const platformId = platformMap.value[platformToDeleteName.value];
        if (!platformId) {
          deleteError.value = 'Selected platform not found.';
          return;
        }
        try {
          await axios.delete(`http://localhost:8080/platforms/${platformId}`);
          showDeleteDialog.value = false;
          platformToDeleteName.value = '';
          deleteError.value = '';
          await fetchPlatforms();
        } catch (error) {
          console.error('Error deleting platform:', error);
          deleteError.value = 'Failed to delete platform. Please try again.';
        }
      };
  
      const sortedPlatforms = computed(() => {
        return platforms.value.slice().sort((a, b) => a.localeCompare(b));
      });
  
      onMounted(() => {
        fetchPlatforms();
      });
  
      return {
        platforms,
        platformMap,
        newPlatformName,
        showCreateDialog,
        showEditDialog,
        showDeleteDialog,
        selectedPlatformName,
        selectedPlatformId,
        platformToDeleteName,
        deleteError,
        fetchPlatforms,
        createPlatform,
        editPlatform,
        updatePlatform,
        deletePlatform,
        sortedPlatforms,
      };
    },
  });
  </script>
  
  <style scoped>
  .hoverable {
    cursor: pointer;
  }
  </style>
  