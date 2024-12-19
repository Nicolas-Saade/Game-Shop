// File: src/router/index.js

import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

// Import views
import Catalog from '@/views/Catalog.vue';
import ProductDetail from '@/views/ProductDetail.vue';
import Cart from '@/views/Cart.vue';
import Login from '@/views/Login.vue';
import Register from '@/views/Register.vue';
import ManagerDashboard from '@/views/manager/ManagerDashboard.vue';
import AddGame from '@/views/manager/AddGame.vue';
import EditGame from '@/views/manager/EditGame.vue';
import Checkout from '@/views/Checkout.vue';
import ManageCategories from '@/views/manager/ManageCategories.vue';
import ManagePlatforms from '@/views/manager/ManagePlatforms.vue';
import AddEmployee from '@/views/manager/AddEmployee.vue';
import UpdateAccount from '@/views/UpdateAccount.vue';
import ArchiveEmployee from '@/views/manager/ArchiveEmployee.vue';
import ListEmployees from '@/views/manager/ListEmployees.vue';
import ListCustomers from '@/views/manager/ListCustomers.vue';
import EmployeeDashboard from '@/views/employee/EmployeeDashboard.vue';
import ViewEmployees from '@/views/employee/ViewEmployees.vue';
import ViewCustomers from '@/views/employee/ViewCustomers.vue';
import ManageEmployees from '@/views/manager/ManageEmployees.vue';
import ManagePromotions from '@/views/manager/ManagePromotions.vue';
import ManageGames from '@/views/manager/ManageGames.vue';
import ListGames from '@/views/manager/ListGames.vue';
import OrderDetails from "@/views/OrderDetails.vue";
import OrderConfirmation from "@/views/OrderConfirmation.vue";
import OrderHistory from "@/views/OrderHistory.vue";
import ManagerViewsQuantity from "@/views/manager/ManagerViewsQuantity.vue";

import ViewGameQuantity from '@/views/employee/ViewGameQuantity.vue';

const routes = [
  {
    path: '/',
    name: 'Catalog',
    component: Catalog,
  },
  {
    path: '/product/:id',
    name: 'ProductView',
    component: ProductDetail,
  },
  {
    path: '/cart',
    name: 'CartView',
    component: Cart,
    meta: { requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: Checkout,
    meta: { requiresAuth: true, role: 'CUSTOMER' },
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },

  {
    path: "/update-account",
    name: "UpdateAccount",
    component: UpdateAccount,
    meta: { requiresAuth: true },
  },
  {
    path: "/logout",
    name: "Logout",
    component: () => import("@/views/Logout.vue"),
  },
  {
    path: "/wishlist",
    name: "WishlistView",
    component: () => import("@/views/Wishlist.vue"),
    meta: { requiresAuth: true, role: "CUSTOMER" },
  },
  {
    path: '/employee',
    name: 'EmployeeDashboard',
    component: EmployeeDashboard,
    meta: { requiresAuth: true, role: 'EMPLOYEE' },
    children: [{
      path: "/employee/view-employees",
      name: "ViewEmployees",
      component: ViewEmployees,
    },
    {
      path: "/employee/view-customers",
      name: "ViewCustomers",
      component: ViewCustomers,
    },
    {
      path:"/employee/view-game-quantiy",
      name:"ViewGameQuantity",
      component: ViewGameQuantity,
    },
    {
      path: "/employee/suggest-games",
      name: "SuggestGames",
      component: () => import("@/views/employee/SuggestGames.vue"),
    },
    {path: "/employee/requests-games",
      name: "RequestsGames",
      component: () => import("@/views/employee/RequestsGames.vue"),
    }
  ],
  },
  {
    path: '/manager',
    name: 'ManagerDashboard',
    component: ManagerDashboard,
    meta: { requiresAuth: true, role: 'MANAGER' },
    children: [
      {
        path: 'manage-games',
        name: 'ManageGames',
        component: ManageGames,
        children: [
          {
            path: 'add-game',
            name: 'AddGame',
            component: AddGame,
          },
          {
            path: 'view-all-games',
            name: 'ViewAllGames',
            component: ListGames,
          },
          {
            path: 'edit-game/:id',
            name: 'EditGame',
            component: EditGame,
            props: true,
          },
          {
            path: 'view-quantity',
            name: 'ManagerViewsQuantity',
            component: ManagerViewsQuantity,
          }
        ],
      },
      {
        path: 'categories',
        name: 'ManageCategories',
        component: ManageCategories,
      },
      {
        path: 'platforms',
        name: 'ManagePlatforms',
        component: ManagePlatforms,
      },
      {
        path: 'promotions',
        name: 'ManagePromotions',
        component: ManagePromotions,
      },
      {
        path: "/list-customers",
        name: "ListCustomers",
        component: ListCustomers,
      },
    ],
  },
  {
    path: "/manage-employees",
    name: "ManageEmployees",
    component: ManageEmployees,
  },
  {
    path: "/order-confirmation",
    name: "OrderConfirmation",
    component: () => import("@/views/OrderConfirmation.vue"),
  },
  {
    path: "/order-history",
    name: "OrderHistory",
    component: () => import("@/views/OrderHistory.vue"),
  },
  {
    path: "/orders/:trackingNumber",
    name: "OrderDetails",
    component: OrderDetails,
    meta: { requiresAuth: true, role: "CUSTOMER" },
  },
  {
    path: "/manage-employees/manage-archive-employee",
    name: "ArchiveEmployee",
    component: ArchiveEmployee,
  },
  {path: "/manage-employees/add-employee",
    name: "AddEmployee",
    component: AddEmployee,
  },
  {
    path: "/manage-employees/list-employees",
    name: "ListEmployees",
    component: ListEmployees,
  },

];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth) {
    if (!auth.user) {
      return next({ name: "Login" });
    }

    if (to.meta.role && auth.accountType !== to.meta.role.toUpperCase()) {
      return next({ name: "Catalog" });
    }
  }

  next();
});

// Navigation Guards (no changes)
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth) {
    if (!auth.user) {
      return next({ name: "Login" });
    }

    if (to.meta.role && auth.accountType !== to.meta.role.toUpperCase()) {
      return next({ name: "Catalog" });
    }
  }

  next();
});

export default router;
