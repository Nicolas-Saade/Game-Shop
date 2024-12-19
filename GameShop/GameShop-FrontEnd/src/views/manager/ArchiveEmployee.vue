 <template>
  <v-container>
    <h1>Archive Employee Accounts</h1>
    <v-alert v-if="successMessage" style="color: green;">{{ successMessage }}</v-alert>
    <v-alert v-if="errorMessage" style="color: red;">{{ errorMessage }}</v-alert>

    <v-list>
      <v-list-item v-for="(employee, index) in employees" :key="index" class="employee-item">

        <v-list-item-content>
          <v-list-item-title>{{ employee.email }}</v-list-item-title>
          <v-list-item-subtitle> Username: {{ employee.username }} | Status: {{ employee.status }}</v-list-item-subtitle>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn @click="archiveEmployee(employee.email)" color="error">Archive</v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-list>

      <v-btn @click="goBack" style="margin-top: 20px;">Back</v-btn>  
  </v-container>
 </template>


  <script>
  import { onMounted } from "vue";
  import { ref } from "vue";
  import { useRouter } from "vue-router";

  export default {
    name: "ArchiveEmployee",
    setup() {
      const router = useRouter();
      const email = ref(""); // Employee email
      const employees = ref([]); // List of employees
      const formValid = ref(false); // Form validation status
      const successMessage = ref(""); // Success message
      const errorMessage = ref(""); // Error message

      const goBack = () => {
        router.push({ name: "ManageEmployees" });
      };

      const rules = {
        required: (value) => !!value || "Required.",
        email: (value) => /.+@.+\..+/.test(value) || "Invalid email.",
      };

      const fetchEmployees = async () => {
        try {
          const response = await fetch("http://localhost:8080/account/employees");
          if (!response.ok) {
            throw new Error("Failed to fetch employees.");
          }
          const data = await response.json();
          
          employees.value = data.accounts.map((employee) => ({
            email: employee.email,
            username: employee.username,
            status: employee.employeeStatus,
          }));
        } catch (error) {
          errorMessage.value = error.message;
        }
      };

      const archiveEmployee = async (email) => {
        try {
          // Clear previous messages
          successMessage.value = "";
          errorMessage.value = "";

          // Send PUT request to archive the employee
          const response = await fetch(`http://localhost:8080/account/employee/${email}`, {
            method: "PUT",
          });

          if (!response.ok) {
            // Handle not found or other errors
            if (response.status === 404) {
              throw new Error("Employee account not found.");
            } else {
              throw new Error(`Failed to archive employee. Status: ${response.status}`);
            }
          }

          successMessage.value = `Employee account with email ${email} has been archived successfully.`;
          fetchEmployees(); // Refresh the list of employees
        } catch (error) {
          errorMessage.value = error.message;
        }
      };

      onMounted(fetchEmployees);

      return {
        email,
        formValid,
        successMessage,
        errorMessage,
        rules,
        archiveEmployee,
        goBack,
        employees,
      };

    },
  };
  </script>