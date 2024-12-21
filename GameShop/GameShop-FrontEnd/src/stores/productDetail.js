import { defineStore } from "pinia";
import { useAuthStore } from "@/stores/auth";
import apiFetch from "./../utils/apiFetch";

export const productDetailStore = defineStore("productDetail", {
  state: () => ({
    reviews: [],
  }),

  actions: {
    // Submit a new review
    async submitReview(reviewData) {
      const authStore = useAuthStore();

      if (!authStore.user || authStore.accountType !== "CUSTOMER") {
        console.warn("Reviews can only be submitted by customers.");
        return;
      }

      try {
        const response = await apiFetch("/reviews", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            gameRating: reviewData.gameRating,
            description: reviewData.description.trim(),
            gameId: reviewData.gameId,
            customerEmail: authStore.user.email,
          }),
        });

        // if (!response.ok) {
        //   const errorText = await response.text();
        //   throw new Error(`Failed to submit review. Server response: ${errorText}`);
        // }

        await this.fetchReviews(reviewData.gameId);
      } catch (error) {
        console.error("Error submitting review:", error.message);
      }
    },

    async submitReply(replyData){
      console.log(replyData);
      try {
        const response = await apiFetch("/replies", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            replyDate: replyData.replyDate,
            description: replyData.description,
            reviewRating: replyData.reviewRating,
            reviewId: replyData.reviewId,
            managerEmail: replyData.managerEmail,
          }),
        });
      
        // if (!response.ok) {
        //   const errorText = await response.text();
        //   console.error("Server error response:", errorText); // Log the exact server error
        //   throw new Error(`Failed to submit reply. Server response: ${errorText}`);
        // }
      
      } catch (error) {
        console.error("Error submitting reply:", error.message);
      }
    },

    // Fetch reviews for a specific product
    async fetchReviews(gameId) {
      try {
        const data = await apiFetch(`/reviews/review/game${gameId}`);
        // if (!response.ok) {
        //   throw new Error(`Failed to fetch reviews. Status: ${response.status}`);
        // }
        // const data = await response.json();

        this.reviews = data.reviews.map((review) => ({
          id: review.reviewId,
          description: review.description,
          gameRating: review.gameRating,
          reviewRating: review.rating || 0,
          customerEmail: review.customerEmail || "Anonymous",
        }));
      } catch (error) {
        console.error("Error fetching reviews:", error.message);
      }
    },

    // Increase review rating
    async increaseRatingReview(reviewId) {
      try {
        const updatedReview = await apiFetch(`/reviews/review/${reviewId}/1`, {
          method: "PUT",
        });
    
        // if (!response.ok) {
        //   const errorText = await response.text();
        //   throw new Error(`Failed to upvote review. Server response: ${errorText}`);
        // }
    
        // const updatedReview = await response.json();
        const reviewIndex = this.reviews.findIndex((r) => r.id === reviewId);
        if (reviewIndex !== -1) {
          this.reviews[reviewIndex].reviewRating = updatedReview.rating;
        }
      } catch (error) {
        console.error("Error increasing review rating:", error.message);
        throw error;
      }
    },
    async fetchReplies(reviewId) {
      try {
        const data = await apiFetch(`/reviews/review/reply/${reviewId}`);
        // if (!response.ok) {
        //   throw new Error(`Failed to fetch replies. Status: ${response.status}`);
        // }
        // const data = await response.json();
        return data.replies.map((reply) => ({
          replyId: reply.replyId,
          description: reply.description,
          managerEmail: reply.managerEmail,
        }));
        console.log(data);
      } catch (error) {
        console.error("Error fetching replies:", error.message);
        throw error;
      }
    },

    // Decrease review rating
    async decreaseRatingReview(reviewId) {
      try {
        const updatedReview = await apiFetch(`/reviews/review/${reviewId}/-1`, {
          method: "PUT",
        });
    
        // if (!response.ok) {
        //   const errorText = await response.text();
        //   throw new Error(`Failed to downvote review. Server response: ${errorText}`);
        // }
    
        // const updatedReview = await response.json();
        const reviewIndex = this.reviews.findIndex((r) => r.id === reviewId);
        if (reviewIndex !== -1) {
          this.reviews[reviewIndex].reviewRating = updatedReview.rating;
        }
      } catch (error) {
        console.error("Error decreasing review rating:", error.message);
        throw error;
      }
    },
    async fetchManagerUsername(email) {
      try {
        const manager = await apiFetch(`/account/getmanager?email=${email}`);
        // if (!response.ok) {
        //   throw new Error(`Failed to fetch manager details. Status: ${response.status}`);
        // }
        // const manager = await response.json();
        return manager.username;
      } catch (error) {
        console.error("Error fetching manager username:", error.message);
        throw error;
      }
    },
    async fetchCustomerUsername(email) {
      try {
        const customer = await apiFetch(`/account/customer/${email}`);
        // if (!response.ok) {
        //   throw new Error(`Failed to fetch customer details. Status: ${response.status}`);
        // }
        // const customer = await response.json();
        return customer.username;
      } catch (error) {
        console.error("Error fetching customer username:", error.message);
        throw error;
      }
    },
  },
});