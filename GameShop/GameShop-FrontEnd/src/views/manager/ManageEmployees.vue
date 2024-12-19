<template>
    <v-row class="button-row" justify="center">
    <v-col cols="auto">
      <v-btn color="primary" @click="goToAddEmployee">Add New Employee</v-btn>
    </v-col>
    <v-col cols="auto">
      <v-btn color="primary" @click="goToListEmployees">View all Employees</v-btn>
    </v-col>
    <v-col cols="auto">
      <v-btn color="primary" @click="goToArchiveEmployee">Archive Employee</v-btn>
    </v-col>
  </v-row>
<v-row justify="center" style="height: 100vh;">
        <v-btn color="primary" @click="goBack">Back</v-btn>
  </v-row>  
  </template>
  
  <script>
  import { defineComponent } from 'vue';
  import { useRouter } from 'vue-router';
  
  export default defineComponent({
    name: 'ManageEmployees',
    setup() {
      const router = useRouter();
  
        const goBack = () => {
            router.push("/manager");
        };
      const goToAddEmployee = () => {
        router.push({ name: 'AddEmployee' });
      };
  
      const goToListEmployees = () => {
        router.push({ name: 'ListEmployees' });
      };
  
      const goToArchiveEmployee = () => {
        router.push({ name: 'ArchiveEmployee' });
      };
  
      return {
        goToAddEmployee,
        goToListEmployees,
        goToArchiveEmployee,
        goBack,
      };
    },
  });
  </script>
  
  <style scoped>
.button-row {
  margin: 100px 0;
}
</style>