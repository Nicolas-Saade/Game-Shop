<template>
  <section class="product-container">
    <!-- Full-Width Button -->
    <v-btn 
      @click="router.push({ name: 'Catalog' })" 
      color="primary" 
      variant="elevated" 
      class="full-width-btn"
    >
      Back to Catalog
    </v-btn>

    <!-- Product Section -->
    <div class="product">
      <div class="product-image">
        <img :src="selectedProduct.photoUrl" alt="Product Image" />
        <v-btn
          v-if="isCustomer && !hasReviewed"
          variant="elevated"
          color="yellow"
          class="add-review-btn"
          @click="toggleReview"
        >
          Add Review
        </v-btn>
      </div>
      <div class="product-details">
        <h1 class="product-title">{{ selectedProduct.title }}</h1>
        <p class="product-description">{{ selectedProduct.description }}</p>
        <h2 class="product-price">Price: ${{ selectedProduct.price }}</h2>
        <div class="product-actions">
          <v-btn
            v-if="isCustomer"
            variant="elevated"
            color="indigo-lighten-3"
            @click="addToCart"
          >
            Add to Cart
          </v-btn>
          <v-btn
            v-if="isCustomer"
            variant="elevated"
            color="secondary"
            @click="addToWishlist"
          >
            Add to Wishlist
          </v-btn>
        </div>
      </div>
    </div>

    <!-- Review Form -->
    <div v-if="showReviewBox" class="review-box">
      <textarea
        v-model="reviewText"
        placeholder="Write your review here..."
        class="custom-textarea"
      ></textarea>
      <div class="rating-container">
        <label for="rating">Rating: </label>
        <select id="rating" v-model="rating" class="custom-select">
          <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
        </select>
      </div>
      <div class="review-buttons">
        <v-btn variant="elevated" color="success" @click="submitReview">
          Submit Review
        </v-btn>
        <v-btn variant="elevated" color="error" @click="cancelReview">
          Cancel
        </v-btn>
      </div>
    </div>

    <!-- Reviews Section -->
    <div class="reviews-section" v-if="reviews.length > 0">
      <h3>Customer Reviews:</h3>
      <div class="review-item" v-for="review in reviews" :key="review.reviewId">
        <div class="review-header">
          <p>
            <strong>{{ review.customerUsername || "Anonymous" }}</strong>, 
            {{ translateGameRating(review.gameRating) }}/5
          </p>
          <p>{{ review.description }}</p>
        </div>
        <div class="review-rating">
          <v-icon
            v-if="isCustomer && !review.hasVoted && review.customerEmail !== loggedInUserEmail"
            @click="increaseRating(review)"
            class="arrow mdi mdi-arrow-up"
          ></v-icon>
          <span class="score">{{ review.reviewRating }}</span>
          <v-icon
            v-if="isCustomer && !review.hasVoted && review.customerEmail !== loggedInUserEmail"
            @click="decreaseRating(review)"
            class="arrow mdi mdi-arrow-down"
          ></v-icon>
        </div>

        <!-- Reply Section -->
        <div class="replies-section">
          <h4>Reply:</h4>
          <div 
            class="reply-item" 
            v-if="replies[review.reviewId]?.length" 
            v-for="reply in replies[review.reviewId]" 
            :key="reply.replyId"
          >
            <p>
              <strong>{{ reply.managerUsername }}</strong>: {{ reply.description }}
            </p>
          </div>
          <p v-else>No replies yet.</p>
        </div>

        <!-- Reply Form -->
        <v-btn
          v-if="isManager && (!replies[review.reviewId] || replies[review.reviewId].length === 0)"
          variant="elevated"
          color="primary"
          @click="toggleReplyBox(review.reviewId)"
        >
          Reply
        </v-btn>
        <div v-if="activeReplyId === review.reviewId" class="reply-box">
          <textarea
            v-model="replyTexts[review.reviewId]"
            placeholder="Write your reply here..."
            class="custom-textarea"
          ></textarea>
          <v-btn variant="elevated" color="success" @click="submitReply(review.reviewId)">
            Submit Reply
          </v-btn>
          <v-btn variant="elevated" color="error" @click="cancelReply">
            Cancel
          </v-btn>
        </div>
      </div>
    </div>

    <!-- No Reviews Message -->
    <div v-else class="no-reviews">
      <p>No reviews available for this product.</p>
    </div>
  </section>
</template>

<script>
import { defineComponent, onMounted, ref, computed } from "vue";
import { productsStore } from "@/stores/products";
import { useRoute, useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import { useAuthStore } from "@/stores/auth";
import { useWishlistStore } from "@/stores/wishlist";
import { productDetailStore } from "@/stores/productDetail";

export default defineComponent({
  name: "ProductDetails",
  setup() {
    const store = productsStore();
    const cartStore = useCartStore();
    const wishlistStore = useWishlistStore();
    const detailStore = productDetailStore();
    const router = useRouter();
    const route = useRoute();
    const authStore = useAuthStore();
    const loggedInUserEmail = computed(() => authStore.user?.email || null);
    const translateGameRating = (gameRating) => {
        const ratingMap = {
          One: 1,
          Two: 2,
          Three: 3,
          Four: 4,
          Five: 5,
        };
        return ratingMap[gameRating] || "N/A"; // Return "N/A" if the gameRating is invalid or missing
      };

    const isCustomer = computed(() => authStore.accountType === "CUSTOMER");
    const isManager = computed(() => authStore.accountType === "MANAGER");

    const selectedProduct = computed(() =>
      store.products.find((item) => item.gameId === Number(route.params.id))
    );

    const addToCart = async () => {
      await cartStore.addGameToCart(selectedProduct.value.gameId, 1);
      router.push({ name: "CartView" });
    };

    const addToWishlist = async () => {
      try {
        await wishlistStore.addGameToWishlist(selectedProduct.value.gameId);
      } catch (error) {
        console.error("Error adding to wishlist:", error);
      }
    };

    const showReviewBox = ref(false);
    const reviewText = ref("");
    const rating = ref(null);

    const toggleReview = () => {
      showReviewBox.value = !showReviewBox.value;
    };

    const submitReview = async () => {
      if (reviewText.value.trim() === "" || rating.value === null) {
        alert("Please write a review and select a rating.");
        return;
      }

      const ratingMap = {
        1: 'One',
        2: 'Two',
        3: 'Three',
        4: 'Four',
        5: 'Five',
      };

      try {
        const reviewData = {
          description: reviewText.value.trim(),
          gameRating: ratingMap[rating.value], // Convert the numeric rating to text
          gameId: selectedProduct.value.gameId,
          customerEmail: authStore.user.email, // Ensure the logged-in user's email is included
        };

        await detailStore.submitReview(reviewData);
        fetchReviews();
        alert("Review submitted successfully!");
      } catch (error) {
        console.error("Error creating review:", error);
        alert("Failed to submit the review.");
      }
      resetReviewBox();
    };

    const cancelReview = () => {
      resetReviewBox();
    };

    const resetReviewBox = () => {
      showReviewBox.value = false;
      reviewText.value = "";
      rating.value = null;
    };
    const hasReviewed = computed(() => {
      // Check if any review is by the logged-in customer
      return reviews.value.some((review) => review.customerEmail === loggedInUserEmail.value);
    });

    const reviews = ref([]);

    const replies = ref({}); // Stores replies for each review

    const fetchReplies = async (reviewId) => {
      try {
        const response = await detailStore.fetchReplies(reviewId);
        // Use Promise.all to fetch manager usernames for replies
        replies.value[reviewId] = await Promise.all(
          response.map(async (reply) => {
            const managerUsername = await detailStore.fetchManagerUsername(reply.managerEmail);
            return {
              replyId: reply.replyId,
              description: reply.description,
              managerUsername: managerUsername, // Add fetched username
            };
          })
        );
      } catch (error) {
        console.error(`Error fetching replies for review ${reviewId}:`, error.message);
      }
    };

    const fetchReviews = async () => {
      try {
        // Fetch reviews for the product
        await detailStore.fetchReviews(selectedProduct.value.gameId);

        // Use Promise.all to fetch customer usernames for reviews
        reviews.value = await Promise.all(
          detailStore.reviews.map(async (review) => {
            const customerUsername = await detailStore.fetchCustomerUsername(review.customerEmail);
            return {
              reviewId: review.reviewId || review.id,
              description: review.description,
              gameRating: review.gameRating,
              reviewRating: review.reviewRating || 0,
              customerUsername: customerUsername || "Anonymous",
              hasVoted: review.hasVoted || false,
              customerEmail: review.customerEmail,
            };
          })
        );

        // Fetch replies for each review after fetching reviews
        reviews.value.forEach(async (review) => {
          await fetchReplies(review.reviewId);
        });
      } catch (error) {
        console.error("Error fetching reviews:", error.message);
      }
    };

    onMounted(() => {
      fetchReviews();
    });

    
    const increaseRating = async (review) => {
      try {
        await detailStore.increaseRatingReview(review.reviewId);
        const index = reviews.value.findIndex((r) => r.reviewId === review.reviewId);
        if (index !== -1) {
          reviews.value[index].reviewRating += 1; // Update rating locally
          reviews.value[index].hasVoted = true; // Mark as voted
        }
      } catch (error) {
        console.error("Error increasing review rating:", error.message);
        alert("Failed to upvote review.");
      }
    };

    const decreaseRating = async (review) => {
      try {
        await detailStore.decreaseRatingReview(review.reviewId);
        const index = reviews.value.findIndex((r) => r.reviewId === review.reviewId);
        if (index !== -1) {
          reviews.value[index].reviewRating -= 1; // Update rating locally
          reviews.value[index].hasVoted = true; // Mark as voted
        }
      } catch (error) {
        console.error("Error decreasing review rating:", error.message);
        alert("Failed to downvote review.");
      }
    };

    // Reply functionality
    const activeReplyId = ref(null); // Tracks which review's reply box is active
    const replyTexts = ref({}); // Stores reply text for each review

    const toggleReplyBox = (reviewId) => {
      activeReplyId.value = activeReplyId.value === reviewId ? null : reviewId;
      if (!replyTexts.value[reviewId]) replyTexts.value[reviewId] = ""; // Initialize reply text for the review
    };

    const submitReply = async (reviewId) => {
      const replyText = replyTexts.value[reviewId]?.trim();
      if (!replyText) {
        alert("Reply cannot be empty!");
        return;
      }
      try {
        const replyData = {
          replyDate : '2031-10-10',
          description : replyText,
          reviewRating : 'Like',
          reviewId : reviewId,
          managerEmail: authStore.user.email,
        }
        // Clear the reply text after submission
        await detailStore.submitReply(replyData);
        await fetchReplies(reviewId);
        replyTexts.value[reviewId] = "";
        activeReplyId.value = null;
        alert("Reply submitted successfully!");
      } catch (error) {
        console.error("Error submitting reply:", error);
        alert("Failed to submit reply.");
      }
    };

    const cancelReply = () => {
      activeReplyId.value = null; // Close the reply box
    };

    onMounted(() => {
      fetchReviews();
    });

    return {
      router,
      selectedProduct,
      addToCart,
      isCustomer,
      isManager,
      addToWishlist,
      showReviewBox,
      toggleReview,
      reviewText,
      rating,
      submitReview,
      cancelReview,
      reviews,
      replies,
      increaseRating,
      decreaseRating,
      loggedInUserEmail,
      translateGameRating,
      activeReplyId,
      replyTexts,
      toggleReplyBox,
      submitReply,
      cancelReply,
      hasReviewed,
    };
  },
});
</script>

<style scoped>
/* General Product Section */
.full-width-btn{
  display: block; /* Ensures the button behaves as a block-level element */
  width: 100%; /* Makes the button span the full width of its container */
  text-align: center; /* Centers the text inside the button */
  margin-bottom: 20px; /* Optional: Adds spacing below the button */
}
.product {
  display: flex;
  margin-top: 50px;
}

.product-image {
  margin-right: 24px;
  display: flex;
  flex-direction: column;
}

.add-review-btn {
  margin-top: 20px;
}

.review-box {
  margin-top: 20px;
}

.custom-textarea {
  width: 100%;
  margin-bottom: 10px;
  border: 2px solid #ccc;
  border-radius: 5px;
  padding: 8px;
  font-size: 16px;
  resize: none;
}

.rating-container {
  margin-top: 10px;
  margin-bottom: 10px;
}

.custom-select {
  width: 150px;
  height: 40px;
  font-size: 16px;
  border: 2px solid #ccc;
  border-radius: 5px;
  padding: 5px;
}

/* Reviews Section */
.reviews-section {
  margin-top: 30px;
  padding: 20px;
  border-top: 1px solid #ddd;
}

.review-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
}

.review-header {
  margin-bottom: 10px;
}

.review-header p {
  margin: 5px 0;
  font-size: 14px;
  color: #333;
}

.review-header p strong {
  font-size: 16px;
  color: #000;
}

.review-rating {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.review-rating .arrow {
  cursor: pointer;
  font-size: 24px;
  margin: 0 5px;
  color: #999;
  transition: color 0.2s;
}

.review-rating .arrow:hover {
  color: #333;
}

.review-rating .score {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

/* Replies Section */
.replies-section {
  margin-top: 15px;
  padding: 10px;
  background: #f1f1f1;
  border-radius: 8px;
}

.reply-item {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
}

.reply-item p {
  margin: 5px 0;
  font-size: 14px;
  color: #333;
}

.reply-item p strong {
  color: #000;
}

/* Buttons */
v-btn.primary {
  background-color: #3f51b5;
  color: white;
}

v-btn.secondary {
  background-color: #f44336;
  color: white;
}

v-btn.success {
  background-color: #4caf50;
  color: white;
}

v-btn.error {
  background-color: #e53935;
  color: white;
}

/* Miscellaneous */
.no-reviews {
  text-align: center;
  color: #666;
  font-style: italic;
  font-size: 16px;
  margin-top: 20px;
}

</style>