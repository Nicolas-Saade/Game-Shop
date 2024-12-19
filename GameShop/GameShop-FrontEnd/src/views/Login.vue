<template>
  <v-container>
    <h1>Login</h1>
    <v-form ref="form" v-model="valid" @submit.prevent="submitLogin">
      <v-text-field
        v-model="email"
        label="Email"
        :rules="[rules.required, rules.email]"
        required
      ></v-text-field>
      <v-text-field
        v-model="password"
        label="Password"
        type="password"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-btn :disabled="!valid" type="submit" color="primary">Login</v-btn>
    </v-form>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    <p>
      Don't have an account?
      <v-btn @click="router.push({ name: 'Register' })" text>Register here</v-btn>
    </p>

  </v-container>
</template>

<script>
import { ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

export default {
  name: "Login",
  setup() {
    const auth = useAuthStore();
    const router = useRouter();

    const email = ref("");
    const password = ref("");
    const valid = ref(false);
    const errorMessage = ref("");

    const rules = {
      required: (value) => !!value || "Required.",
      email: (value) => /.+@.+\..+/.test(value) || "Invalid email.",
    };

    const submitLogin = async () => {
      try {
        errorMessage.value = ""; // Clear any previous error message
        await auth.login(email.value, password.value);

        if (auth.user) {
          console.log("Account type:", auth.accountType);

          // Redirect based on account type
          if (auth.accountType === "CUSTOMER") {
            router.push({ name: "Catalog" });
          } else if (auth.accountType === "EMPLOYEE") {
            router.push({ name: "EmployeeDashboard" });
          } else if (auth.accountType === "MANAGER") {
            router.push({ name: "ManagerDashboard" });
          } else {
            throw new Error("Invalid account type.");
          }
        }
      } catch (error) {
        errorMessage.value = error.message;
      }
    };

    return {
      email,
      password,
      valid,
      errorMessage,
      rules,
      submitLogin,
      router,
    };
  },
};
</script>
