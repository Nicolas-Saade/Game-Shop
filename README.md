# 🎮 GameShop

## **Overview**

GameShop is a full-stack web application for:
- Managing an online storefront of a video game store as the manager or employee.
- Purchasing and browsing video games as a customer.

It features a **Vue.js** frontend (built with **Vite**) and a **Spring Boot** backend, deployed on **Render** with a **PostgreSQL** database, configured thruogh **Docker Containers**.

---

## **Features**

- User registration, login, and authentication.
- Manager and customer functionalities (add games to catalog, add games to cart/wishlist, manage orders, view inventory).
- Dynamic categorization of games and live promotions.
- Smart search options like searching by game name, or game filters.

---

## **Instructions for Use**

### **Option 1: Live Demo**
The live application is hosted on **Render**. You can access it here: [Live Website](https://game-shop-zabi.onrender.com).

> **Note**: The server has limited resources, so please ensure to close the session after use to avoid overloading.
> ⚠️ Please DON'T use any of your real-life sensitive passwords for the demo, as our database and communications is NOT 100% secure from malicious attacks.
> ⚠️ To login as a manager use the following credentials: Email: manager@manager.com, Password: manager123
> ⚠️ To login as a user, create a user through the Register button and then login again (you can also use the user I have in the demo video: email: NicolasSaade@gmail.com, password: nicolas).

Have fun!

### **Option 2: Watch Demo Video**
For those who prefer not to test the application themselves, here are demo videos showcasing the features:
- **User/Manager Registration and Login**: [Watch Video](https://youtu.be/GCT4zUlXeZo)
- **Management by Admin**: [Watch Video](https://youtu.be/iRN_fIQOKLk)
- **Purchasing a Game as a Customer**: [Watch Video](https://youtu.be/mu1dvoMBnrQ)

---

## **Documentation**

- **[Database, Persistence, and Design Decisions](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)**
- **[Back-end/Controller Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report)**
- **[Front-end/View Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-3:-Project-Report)**
- **[API Swagger Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)**
- **[QA Plan with Jacoco Coverage](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)**

---

## **Tech Stack**

- **Frontend**: Vue.js (Vite), JavaScript, HTML, CSS
- **Backend**: Spring Boot, Java
- **Database**: PostgreSQL, Hibernate
- **Deployment**: Render
- **Containerization**: Docker
