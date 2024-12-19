<template>
  <v-container>
    <v-form ref="form" v-model="valid" @submit.prevent="submitRegister">
      <v-text-field
        v-model="accountData.email"
        label="Email"
        :rules="[rules.required, rules.email]"
        required
      ></v-text-field>
      <v-text-field
        v-model="accountData.username"
        label="Username"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-text-field
        v-model="accountData.password"
        label="Password"
        type="password"
        :rules="[rules.required, rules.password]"
        required
      ></v-text-field>
      <v-text-field
        v-model="accountData.phoneNumber"
        label="Phone Number"
        :rules="[rules.required, rules.phoneNumber]"
        required
      ></v-text-field>
      <v-text-field
        v-model="accountData.address"
        label="Address"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-btn :disabled="!valid" type="submit">Register</v-btn>
    </v-form>
  </v-container>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'Register',
  setup() {
    const auth = useAuthStore();
    const router = useRouter();

    const accountData = ref({
      email: '',
      username: '',
      password: '',
      phoneNumber: '',
      address: '',
    });

    const valid = ref(false);
    const form = ref(null);

    const rules = {
      required: (value) => !!value || 'Required.',
      email: (value) => /.+@.+\..+/.test(value) || 'Invalid email.',
      password: (value) => value.length >= 6 || "Password must be at least 6 characters.",
      phoneNumber: (value) => /^[0-9]{10}$/.test(value) || 'Phone number must be 10 digits.',
    };

    const submitRegister = async () => {
      try {
      // Attempt registration
      await auth.registerCustomer(accountData.value);
      
      // If successful, redirect to the Login page
      alert('Registration successful! Please log in.');
      router.push({ name: 'Login' });
    } catch (error) {
      // Handle failure
      console.error('Registration failed:', error);
      alert('Registration failed. Email already exists. Please try again.');
    }
    };

    return {
      accountData,
      valid,
      form,
      rules,
      submitRegister,
    };
  },
});
</script>

