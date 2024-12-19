<template>
  <v-container>
    <h1>List of Employees</h1>

    <v-simple-table dense>
      <template v-slot:default>
        <thead>
          <tr>
            <th>Email</th>
            <th>Username</th>
            <th>PhoneNumber</th>
            <th>Address</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="employee in employees" :key="employee.email">
            <td>{{ employee.email }}</td>
            <td>{{ employee.username }}</td>
            <td>{{ employee.phoneNumber }}</td>
            <td>{{ employee.address }}</td>
            <td>{{ employee.employeeStatus }}</td>
          </tr>
        </tbody>
      </template>
    </v-simple-table>

    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </v-container>
  <v-btn @click="goBack">Back</v-btn>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

export default {
  name: "ListEmployees",
  setup() {
    const router = useRouter();
    const employees = ref([]); 
    const errorMessage = ref("");

    const goBack = () => {
      router.push({ name: 'ManageEmployees' });
    };
    // Fetch employees from backend
    const fetchEmployees = async () => {
try {
  errorMessage.value = "";
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
      goBack,
    };
  },
};
</script>

<style scoped>
/* Add styles for the table */
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
