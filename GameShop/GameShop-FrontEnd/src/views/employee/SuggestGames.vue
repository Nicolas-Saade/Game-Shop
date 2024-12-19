<template>
    <div class="form">
        <h1>Suggest a game to be deleted</h1>
        <v-form @submit.prevent="submitForm">
            <div>
                <label for="gameName">Game Name      </label>
                <input type="text" id="gameName" v-model="gameName" class="styled-input" placeholder="Enter the name of the game" />
            </div>
            <v-btn @click="submitForm" style="margin-top: 20px;">Submit Suggestion</v-btn>
        </v-form>
            <div v-if="message" class="message">{{ message }}</div>

    </div>
</template>

<script>
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

export default defineComponent({
    name: 'SuggestGames',
    setup() {
        const router = useRouter();
        const gameName = ref('');
        const reason = ref('');
        const message = ref('');

        const submitForm = async () => {
            if (!gameName.value.trim()){
                message.value = "Please enter the name of the game.";
                return;
            }
            message.value = '';
          alert("An email has been sent to the manager to review the deletion request.");
          gameName.value = "";
       
        };

        return {
            gameName,
            reason,
            submitForm,
            message,
        };
    },
});
</script>

<style>
.styled-input {
    width: 100%;
    padding: 12px 16px;
    font-size: 16px;
    border-radius: 8px;
    border: 1px solid #ccc;
    box-sizing: border-box;
    transition: all 0.3s ease;
  }
  
  .styled-input:focus {
    border-color: #4CAF50;
    outline: none;
    box-shadow: 0 0 8px rgba(76, 175, 80, 0.2);
  }
  
  .styled-input::placeholder {
    color: #aaa;
    font-style: italic;
  }

</style>
