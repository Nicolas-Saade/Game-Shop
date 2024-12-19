<template>
  <v-container>
    <v-row class="align-center mb-4">
      <v-col>
        <h1>Manage Categories</h1>
      </v-col>
      <v-col class="text-right">
        <v-btn color="green" @click="showCreateDialog = true">Create Category</v-btn>
        <v-btn color="red darken-1" @click="showDeleteDialog = true">Delete Category</v-btn>
      </v-col>
    </v-row>

    <v-list two-line>
      <v-list-item
        v-for="category in sortedCategories"
        :key="category"
        @click="editCategory(category)"
        class="hoverable"
      >
        <v-list-item-content>
          <v-list-item-title>{{ category }}</v-list-item-title>
        </v-list-item-content>
        <v-list-item-icon>
          <v-icon>mdi-pencil</v-icon>
        </v-list-item-icon>
      </v-list-item>
    </v-list>

    <v-dialog v-model="showCreateDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Create Category</span>
        </v-card-title>
        <v-card-text>
          <v-text-field label="Category Name" v-model="newCategoryName"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="createCategory">Create</v-btn>
          <v-btn color="grey darken-1" text @click="showCreateDialog = false">Cancel</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showEditDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Edit Category</span>
        </v-card-title>
        <v-card-text>
          <v-text-field label="Category Name" v-model="selectedCategoryName"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="updateCategory">Save</v-btn>
          <v-btn color="grey darken-1" text @click="showEditDialog = false">Cancel</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showDeleteDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Delete Category</span>
        </v-card-title>
        <v-card-text>
          <v-select
            label="Select Category to Delete"
            :items="categories"
            v-model="categoryToDeleteName"
          ></v-select>
        </v-card-text>
        <v-card-text>
          <v-alert type="error" v-if="deleteError">{{ deleteError }}</v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" text @click="deleteCategory">Delete</v-btn>
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
  name: 'ManageCategories',
  setup() {
    const categories = ref([]);
    const categoryMap = ref({});
    const newCategoryName = ref('');
    const showCreateDialog = ref(false);
    const showEditDialog = ref(false);
    const showDeleteDialog = ref(false);
    const selectedCategoryName = ref('');
    const selectedCategoryId = ref(null);
    const categoryToDeleteName = ref('');
    const deleteError = ref('');

    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8080/categories');
        const data = response.data.categories;
        categories.value = data.map((cat) => cat.categoryName);
        categoryMap.value = {};
        data.forEach((cat) => {
          categoryMap.value[cat.categoryName] = cat.categoryId;
        });
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };

    const createCategory = async () => {
      try {
        await axios.post('http://localhost:8080/categories', {
          categoryName: newCategoryName.value,
          managerEmail: 'manager@manager.com', // Replace with actual manager email
        });
        showCreateDialog.value = false;
        newCategoryName.value = '';
        await fetchCategories();
      } catch (error) {
        console.error('Error creating category:', error);
      }
    };

    const editCategory = (categoryName) => {
      selectedCategoryName.value = categoryName;
      selectedCategoryId.value = categoryMap.value[categoryName];
      showEditDialog.value = true;
    };

    const updateCategory = async () => {
      if (!selectedCategoryId.value) {
        console.error('No category selected for update.');
        return;
      }
      try {
        await axios.put(`http://localhost:8080/categories/${selectedCategoryId.value}`, {
          categoryName: selectedCategoryName.value,
        });
        showEditDialog.value = false;
        await fetchCategories();
      } catch (error) {
        console.error('Error updating category:', error);
      }
    };

    const deleteCategory = async () => {
      if (!categoryToDeleteName.value) {
        deleteError.value = 'Please select a category to delete.';
        return;
      }
      const categoryId = categoryMap.value[categoryToDeleteName.value];
      if (!categoryId) {
        deleteError.value = 'Selected category not found.';
        return;
      }
      try {
        await axios.delete(`http://localhost:8080/categories/${categoryId}`);
        showDeleteDialog.value = false;
        categoryToDeleteName.value = '';
        deleteError.value = '';
        await fetchCategories();
      } catch (error) {
        console.error('Error deleting category:', error);
        deleteError.value = 'Failed to delete category. Please try again.';
      }
    };

    const sortedCategories = computed(() => {
      return categories.value.slice().sort((a, b) => a.localeCompare(b));
    });

    onMounted(() => {
      fetchCategories();
    });

    return {
      categories,
      categoryMap,
      newCategoryName,
      showCreateDialog,
      showEditDialog,
      showDeleteDialog,
      selectedCategoryName,
      selectedCategoryId,
      categoryToDeleteName,
      deleteError,
      fetchCategories,
      createCategory,
      editCategory,
      updateCategory,
      deleteCategory,
      sortedCategories,
    };
  },
});
</script>

<style scoped>
.hoverable {
  cursor: pointer;
}
</style>
