import api from './../utils/axiosConfig';
import { defineStore } from "pinia";
import axios from "axios";

export const usePromotionsStore = defineStore("promotions", {
  state: () => ({
    promotions: [], // Array to hold promotions
  }),

  actions: {
    async fetchPromotions() {
      try {
        const response = await api.get("/promotions");
        this.promotions = response.data.promotions || [];
        return this.promotions; // Return the array of promotions
      } catch (error) {
        console.error("Error fetching promotions:", error);
        return []; // Return empty array on error
      }
    },

    // Fethes promotions that have start date 
    // smaller than today
    // And end date after today
    async fetchValidPromotions() {
      try {
        const response = await api.get("/promotions");
        this.promotions = response.data.promotions || [];
        let today = new Date();
        let validPromotions = this.promotions.filter(promotion => {
          let startDate = new Date(promotion.startLocalDate);
          let endDate = new Date(promotion.endLocalDate);
          return startDate <= today && endDate >= today;
        });
        return validPromotions; // Return the array of promotions
      } catch (error) {
        console.error("Error fetching promotions:", error);
        return []; // Return empty array on error
      }
    },

    async createPromotion(promotionData) {
      try {
        await api.post('/promotions', promotionData);
        await this.fetchPromotions();
      } catch (error) {
        console.error('Error creating promotion:', error);
        throw error;
      }
    },

    async updatePromotion(promotionId, promotionData) {
      try {
        await api.put(
          `/promotions/${promotionId}`,
          promotionData
        );
        await this.fetchPromotions();
      } catch (error) {
        console.error('Error updating promotion:', error);
        throw error;
      }
    },

    async deletePromotion(promotionId) {
      try {
        await api.delete(`/promotions/${promotionId}`);
        await this.fetchPromotions();
      } catch (error) {
        console.error('Error deleting promotion:', error);
        throw error;
      }
    },
  },
});