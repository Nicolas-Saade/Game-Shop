<template>
  <v-container>
    <h2>Add New Employee</h2>
    <v-form ref="form" v-model="valid" @submit.prevent="submitForm">
      <v-text-field
        v-model="employee.email"
        label="Email"
        :rules="[rules.required, rules.email]"
        required
      ></v-text-field>
      <v-text-field
        v-model="employee.username"
        label="Username"
        :rules="[rules.required]"
        required
      ></v-text-field>
      <v-text-field
        v-model="employee.password"
        label="Password"
        type="password"
        :rules="[rules.required, rules.password]"
        required
      ></v-text-field>

      <v-btn :disabled="!valid" type="submit" color="success">Add Employee</v-btn>
      <v-btn @click="clearForm" color="error" text>Clear</v-btn>
      <v-btn @click="goBack" color="primary" text>Back</v-btn>
    </v-form>
  </v-container>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

export default defineComponent({
  name: 'AddEmployee',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();

    const goBack = () => {
      router.push({ name: 'ManageEmployees' });
    };
    // Employee data
    const employee = ref({
      email: '',
      username: '',
      password: '',
      employeeStatus: 'Active', // Set default status as Active
    });

    // Form validity and rules
    const valid = ref(false);
    const rules = {
      required: (value) => !!value || 'Required.',
      email: (value) => /.+@.+\..+/.test(value) || 'Must be a valid email.',
      password: (value) => value.length >= 6 || 'Password must be at least 6 characters.',
    };

    // Submit form function
    const submitForm = async () => {
      try {
        // Assuming you have an API endpoint for creating employees
        const response = await fetch('http://localhost:8080/account/employee', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${authStore.token}`,
          },
          body: JSON.stringify({
            email: employee.value.email,
            username: employee.value.username,
            password: employee.value.password,
            employeeStatus: employee.value.employeeStatus,
          }),
        });

        const data = await response.json();
        if (response.ok) {
          alert('Employee created successfully.');
          router.push({ name: 'ManagerDashboard' });
        } else {
          alert(`Failed to create employee: Email already exists`);
        }
      } catch (error) {
        console.error('Error creating employee: Email already exists');
        alert('Error creating employee.');
      }
    };

    // Clear form data
    const clearForm = () => {
      employee.value = {
        email: '',
        username: '',
        password: '',
        employeeStatus: 'Active', // Reset to Active
      };
    };

    return {
      employee,
      valid,
      rules,
      submitForm,
      clearForm,
      goBack,
    };
  },
});
</script>
