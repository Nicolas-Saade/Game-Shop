<template>
  <v-container>
    <h1>Update Account Information</h1>

    <v-form ref="updateForm" v-model="updateValid" lazy-validation @submit.prevent="updateAccount">
      <v-text-field
        v-model="email"
        label="Email"
        :rules="[rules.required, rules.email]"
        required
      ></v-text-field>
      <v-text-field
        v-model="username"
        label="Username"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-text-field
        v-model="password"
        label="Password"
        type="password"
        :rules="[rules.required, rules.password]"
        required
      ></v-text-field>
      <v-text-field
        v-model="phoneNumber"
        label="Phone Number"
        :rules="[rules.required, rules.phoneNumber]"
        required
      ></v-text-field>
      <v-text-field
        v-model="address"
        label="Address"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-btn :disabled="!updateValid" type="submit" color="primary">Update Account</v-btn>
    </v-form>
    <p v-if="successMessage" style="color: green;">{{ successMessage }}</p>
  </v-container>
</template>

<script>
import { ref } from "vue";

export default {
  name: "UpdateAccount",
  setup() {
    const email = ref("");
    const username = ref("");
    const password = ref("");
    const phoneNumber = ref("");
    const address = ref("");
    const updateValid = ref(false);
    const successMessage = ref("");

    const rules = {
      required: (value) => !!value || "Required.",
      email: (value) => /.+@.+\..+/.test(value) || "Invalid email.",
      password: (value) => value.length >= 6 || "Password must be at least 6 characters.",
      phoneNumber: (value) => /^[0-9]{10}$/.test(value) || 'Phone number must be 10 digits.',
    };

    const clearFields = () => {
      email.value = "";
      username.value = "";
      password.value = "";
      phoneNumber.value = "";
      address.value = "";
    };

    const updateAccount = async () => {
      try {
        successMessage.value = "";

        const response = await fetch(`http://localhost:8080/account/${email.value}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: username.value,
            password: password.value,
            phoneNumber: phoneNumber.value,
            address: address.value,
          }),
        });

        if (!response.ok) {
          if (response.status === 404) {
            throw new Error("Account does not exist.");
          } else {
            throw new Error(`Failed to update account. Status: ${response.status}`);
          }
        }

        successMessage.value = "Account updated successfully!";
        clearFields();
      } catch (error) {
        alert(error.message); // Show a pop-up with the error message
        clearFields(); // Clear all fields on error
      }
    };

    return {
      email,
      username,
      password,
      phoneNumber,
      address,
      updateValid,
      successMessage,
      rules,
      updateAccount,
      clearFields,
    };
  },
};
</script>