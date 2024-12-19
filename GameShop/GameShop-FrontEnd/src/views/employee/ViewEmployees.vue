<template>
    <v-container>
      <h1>List of Employees</h1>
  
      <v-simple-table dense>
        <template v-slot:default>
          <thead>
            <tr>
              <th>Email</th>
              <th>Username</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="employee in employees" :key="employee.email">
              <td>{{ employee.email }}</td>
              <td>{{ employee.username }}</td>
              <td>{{ employee.employeeStatus }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
  
      <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    </v-container>
  </template>
  
  <script>
  import { ref, onMounted } from "vue";
  
  export default {
    name: "ViewEmployees",
    setup() {
      const employees = ref([]); // Employee list fetched from the database
      const errorMessage = ref(""); // Error message in case of failure
  
      // Fetch employees from the backend
      const fetchEmployees = async () => {
  try {
    errorMessage.value = ""; // Clear previous errors
    console.log("Fetching employees...");
    const response = await fetch("http://localhost:8080/account/employees", {
      method: "GET",
    });
    console.log("API response status:", response.status);
    if (!response.ok) {
      throw new Error(`Failed to fetch employees. Status: ${response.status}`);
    }
    const data = await response.json();
    console.log("API response data:", data);
    // Access the 'accounts' array from the response
    employees.value = data.accounts;
    console.log("Employees stored in state:", employees.value);
  } catch (error) {
    console.error("Error fetching employees:", error);
    errorMessage.value = error.message;
  }
};
  
      // Fetch employees when the component is mounted
      onMounted(() => {
        fetchEmployees();
      });
  
      return {
        employees,
        errorMessage,
      };
    },
  };
  </script>
  
  <style scoped>
  v-simple-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1em;
  }
  
  thead th {
    text-align: left;
    background: #f5f5f5;
    padding: 0.5em;
  }
  
  tbody td {
    padding: 0.5em;
  }
  </style>
  