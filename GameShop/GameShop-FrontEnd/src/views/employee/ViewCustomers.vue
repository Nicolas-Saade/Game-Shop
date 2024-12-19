<template>
    <v-container>
      <h1>List of Customers</h1>
  
      <v-simple-table dense>
        <template v-slot:default>
          <thead>
            <tr>
              <th>Email</th>
              <th>Username</th>
              <th>Address</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="customer in customers" :key="customer.email">
              <td>{{ customer.email }}</td>
              <td>{{ customer.username }}</td>
              <td>{{ customer.address }}</td>
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
    name: "ViewCustomers",
    setup() {
      const customers = ref([]); // Customer list fetched from the database
      const errorMessage = ref(""); // Error message in case of failure
  
      // Fetch customers from the backend
      const fetchCustomers = async () => {
  try {
    errorMessage.value = ""; // Clear previous errors
    console.log("Fetching customers...");
    // Fetch customer data from the backend
    const response = await fetch("http://localhost:8080/account/customers", {
      method: "GET",
    });
    console.log("API response status:", response.status);
    if (!response.ok) {
      throw new Error(`Failed to fetch customers. Status: ${response.status}`);
    }
    // Parse and store the customer data
    const data = await response.json();
    console.log("API response data:", data);
    // Access the 'accounts' array from the response
    customers.value = data.accounts; // Correctly accessing accounts instead of customers
    console.log("Customers stored in state:", customers.value);
  } catch (error) {
    console.error("Error fetching customers:", error);
    errorMessage.value = error.message;
  }
};
  
      // Fetch customers when the component is mounted
      onMounted(() => {
        fetchCustomers();
      });
  
      return {
        customers,
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
  