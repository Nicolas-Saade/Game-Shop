Documentation
--------
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* [Database, Persistence and Design Decisions](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)
* [Back-end/Controller Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report)
* [Front-end/View Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-3:-Project-Report)

* [API Swagger Documentation](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)
* [QA Plan with Jacoco Coverage](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report#iii2-api-docs-instructions)
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Build System Instructions
--------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* The other part of the build system submission (Testing Summary) is to be completed as part of the wiki along with the project report.
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

STEP 1: Clone The Repository
--------
Begin by opening your terminal, using the cd command to navigate your local directories, and creating a local clone of our git repository in a location of your choosing using the following command:
git clone https://github.com/McGill-ECSE321-Fall2024/project-group-10.git

Once this is done, you will have a folder called project-group-10 on your machine, containing our codebase under the GameShop folder.


STEP 2: Set up the Local PostgreSQL Database
--------

With the repository now locally, cloned, you must create a local PostgreSQL database on your machine, 
following the detailed instructions found here: https://mcgill-ecse321-fall2024.github.io/tutorial-notes/#_setting_up_a_local_postgresql_database

You are to follow these instructions with the following information specific to our database:
- NAME: gameshop
- PORT NUMBER: 5432
- PASSWORD: grouptenpsql


Quick Summary of PostgreSQL Setup:
1. Start PostgreSQL server.
2. Open psql.
3. Create a database with the name gameshop.
4. Set the port to 5432 and ensure you have the correct password (grouptenpsql).

STEP 3: Launch IDE & Run Tests
--------
Now, with the repository and PostgreSQL database set up locally on your machine, you may build and run the tests.
Launch your IDE and connect your IDE to the database. For example, on VS Code, you would connect through the "Select Postgres Server" button in the bottom ribbon:

<img width="184" alt="Screenshot 2024-10-13 at 1 09 19 PM" src="https://github.com/user-attachments/assets/a94c06a4-a278-4775-aae0-bf973ba3e82b">


Then, run the following command in your terminal to build and test: ./gradlew build -xtest
