<template>
  <v-container>
    <v-row class="align-center mb-4">
      <v-col>
        <h1>Manage Promotions</h1>
      </v-col>
      <v-col class="text-right">
        <v-btn color="green" @click="openCreatePromotionDialog">
          Create Promotion
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col
        cols="12"
        sm="6"
        md="4"
        lg="3"
        v-for="promotion in promotions"
        :key="promotion.promotionId"
      >
        <v-card class="mx-auto mb-4" outlined>
          <v-card-title>{{ promotion.description }}</v-card-title>
          <v-card-subtitle>
            Discount: {{ promotion.discountRate }}%
          </v-card-subtitle>
          <v-card-text>
            Dates: {{ formatDate(promotion.startLocalDate) }} - {{ formatDate(promotion.endLocalDate) }}
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn icon @click="editPromotion(promotion)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon @click="confirmDeletePromotion(promotion)">
              <v-icon color="red">mdi-delete</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="showPromotionDialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">
            {{ isEditing ? 'Edit Promotion' : 'Create Promotion' }}
          </span>
        </v-card-title>
        <v-card-text>
          <v-text-field
            label="Description"
            v-model="promotionForm.description"
            :rules="[requiredRule]"
          ></v-text-field>
          <v-text-field
            label="Discount Rate (%)"
            type="number"
            v-model.number="promotionForm.discountRate"
            :rules="[requiredRule, rateValidator]"
          ></v-text-field>
          <v-text-field
            label="Start Date (yyyy-MM-dd)"
            v-model="promotionForm.startLocalDate"
            :rules="[requiredRule, dateValidator]"
          ></v-text-field>
          <v-text-field
            label="End Date (yyyy-MM-dd)"
            v-model="promotionForm.endLocalDate"
            :rules="[requiredRule, dateValidator]"
          ></v-text-field>
          <v-select
            label="Select Games"
            :items="games"
            item-text="description"
            item-value="gameId"
            v-model="promotionForm.gameIds"
            multiple
            :rules="[requiredRule]"
          ></v-select>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="savePromotion">
            {{ isEditing ? 'Update' : 'Create' }}
          </v-btn>
          <v-btn color="grey darken-1" text @click="closePromotionDialog">
            Cancel
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showDeleteDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Delete Promotion</span>
        </v-card-title>
        <v-card-text>
          Are you sure you want to delete this promotion?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" text @click="deletePromotion">
            Delete
          </v-btn>
          <v-btn color="grey darken-1" text @click="showDeleteDialog = false">
            Cancel
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

export default defineComponent({
  name: 'ManagePromotions',
  setup() {
    const promotions = ref([]);
    const games = ref([]);
    const showPromotionDialog = ref(false);
    const showDeleteDialog = ref(false);
    const isEditing = ref(false);
    const promotionForm = ref({
      promotionId: null,
      description: '',
      discountRate: 0,
      startLocalDate: '',
      endLocalDate: '',
      gameIds: [],
    });
    const promotionToDelete = ref(null);

    const authStore = useAuthStore();

    const fetchPromotions = async () => {
      try {
        const response = await axios.get('http://localhost:8080/promotions');
        promotions.value = response.data.promotions || [];
      } catch (error) {
        console.error('Error fetching promotions:', error);
      }
    };

    const fetchGames = async () => {
      try {
        const response = await axios.get('http://localhost:8080/games');
        games.value = response.data.games || [];
      } catch (error) {
        console.error('Error fetching games:', error);
      }
    };

    const openCreatePromotionDialog = () => {
      isEditing.value = false;
      promotionForm.value = {
        promotionId: null,
        description: '',
        discountRate: 0,
        startLocalDate: '',
        endLocalDate: '',
        gameIds: [],
      };
      showPromotionDialog.value = true;
    };

    const savePromotion = async () => {
      try {
        // Validate dates before proceeding
        if (!validateDates()) {
          return;
        }

        const payload = {
          description: promotionForm.value.description,
          discountRate: promotionForm.value.discountRate,
          startLocalDate: promotionForm.value.startLocalDate,
          endLocalDate: promotionForm.value.endLocalDate,
          managerEmail: authStore.user?.email || 'manager@manager.com',
          gameIds: promotionForm.value.gameIds,
        };

        if (isEditing.value) {
          await axios.put(
            `http://localhost:8080/promotions/${promotionForm.value.promotionId}`,
            payload
          );
        } else {
          await axios.post('http://localhost:8080/promotions', payload);
        }

        closePromotionDialog();
        await fetchPromotions();
      } catch (error) {
        console.error('Error saving promotion:', error);
      }
    };

    const editPromotion = (promotion) => {
      isEditing.value = true;
      promotionForm.value = {
        promotionId: promotion.promotionId,
        description: promotion.description,
        discountRate: promotion.discountRate,
        startLocalDate: promotion.startLocalDate,
        endLocalDate: promotion.endLocalDate,
        gameIds: promotion.games ? promotion.games.map((g) => g.gameId) : [],
      };
      showPromotionDialog.value = true;
    };

    const confirmDeletePromotion = (promotion) => {
      promotionToDelete.value = promotion;
      showDeleteDialog.value = true;
    };

    const deletePromotion = async () => {
      try {
        await axios.delete(
          `http://localhost:8080/promotions/${promotionToDelete.value.promotionId}`
        );
        showDeleteDialog.value = false;
        await fetchPromotions();
      } catch (error) {
        console.error('Error deleting promotion:', error);
      }
    };

    const closePromotionDialog = () => {
      showPromotionDialog.value = false;
      isEditing.value = false;
      promotionForm.value = {
        promotionId: null,
        description: '',
        discountRate: 0,
        startLocalDate: '',
        endLocalDate: '',
        gameIds: [],
      };
    };

    const requiredRule = (value) => !!value || 'This field is required';

    const rateValidator = (value) => {
      if (value === '' || value === null || value === undefined) {
        return 'Discount rate is required';
      }
      if (value < 0 || value > 100) {
        return 'Discount rate must be between 0 and 100';
      }
      return true;
    };

    const dateValidator = (value) => {
      if (!value) {
        return 'Date is required';
      }
      const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
      if (!dateRegex.test(value)) {
        return 'Date must be in yyyy-MM-dd format';
      }
      return true;
    };

    const validateDates = () => {
      const startDate = new Date(promotionForm.value.startLocalDate);
      const endDate = new Date(promotionForm.value.endLocalDate);
      if (startDate > endDate) {
        alert('Start Date cannot be after End Date.');
        return false;
      }
      return true;
    };

    const formatDate = (dateStr) => {
      if (!dateStr) return '';
      // Return date in 'yyyy-MM-dd' format
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = ('0' + (date.getMonth() + 1)).slice(-2);
      const day = ('0' + date.getDate()).slice(-2);
      return `${year}-${month}-${day}`;
    };

    onMounted(() => {
      fetchPromotions();
      fetchGames();
    });

    return {
      promotions,
      games,
      showPromotionDialog,
      showDeleteDialog,
      isEditing,
      promotionForm,
      promotionToDelete,
      fetchPromotions,
      openCreatePromotionDialog,
      savePromotion,
      editPromotion,
      confirmDeletePromotion,
      deletePromotion,
      closePromotionDialog,
      requiredRule,
      rateValidator,
      dateValidator,
      validateDates,
      formatDate,
    };
  },
});
</script>

<style scoped>
.hoverable {
  cursor: pointer;
}
</style>