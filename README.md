# 🎮 GameShop

## **Overview**

GameShop is a full-stack web application for:
- Managing an online storefront of a video game store as the manager or employee.
- Purchasing and browsing video games as a customer.

It features a **Vue.js** frontend (built with **Vite**) and a **Spring Boot** backend, deployed on **Render** with a **PostgreSQL** database, configured thruogh **Docker Containers**.

---

## **Documentation**

- **[Database, Persistence, and Design Decisions](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)**
- **[Back-end/Controller Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report)**
- **[Front-end/View Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-3:-Project-Report)**
- **[API Swagger Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)**
- **[QA Plan with Jacoco Coverage](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)**

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
> ⚠️ To login as a user, create a user through the Register button and then login again.

Have fun!

### **Option 2: Try Locally for a smoother experience**
Follow these steps to run the project locally.

1. Clone the Repository:
   ```bash
   git clone https://github.com/McGill-ECSE321-Fall2024/project-group-10.git
   cd GameShop
   ```

2. Build and Run:
   Ensure you have Docker installed on your system. Then run:
   ```bash
   docker build -t gameshop-app .
   docker run -p 8080:8080 gameshop-app
   ```

3. Access the Application:
   - Open your browser and navigate to `http://localhost:8080`.

4. **Admin Login**:
   Use the following credentials to access admin features:
   - Email: `manager@manager.com`
   - Password: `manager123`

### **Option 3: Watch Demo Video**
For those who prefer not to test the application themselves, here are demo videos showcasing the features:
- **User Registration and Login**: [Watch Video](link-to-video)
- **Game Management by Admin**: [Watch Video](link-to-video)
- **Purchasing a Game as a Customer**: [Watch Video](link-to-video)

## **Screenshots**

### Homepage
[Insert screenshot here]

### Admin Dashboard
[Insert screenshot here]

---

## **Tech Stack**

- **Frontend**: Vue.js (Vite), JavaScript, HTML, CSS
- **Backend**: Spring Boot, Java
- **Database**: PostgreSQL, Hibernate
- **Deployment**: Render
- **Containerization**: Docker

---

## **Contact**
For any inquiries, please contact me at [nicolasnsaade@gmail.com](mailto:nicolasnsaade@gmail.com).

---
