Build System Instructions for Deliverable 3
--------
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* As per TA instructions, the project report is fully completed with all required sections in the wiki and is navigable using this link: [https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-3-Project-Report.](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-3:-Project-Report)
* In the report, you will find the complete build system instructions as well as all the other project report components required in the rubric.
------------------------------------------------------------------------------------------------------------------------------------------------------------------------



Build System Instructions for Deliverable 2
--------
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* As per TA instructions, the project report, QA Plan, and QA Report are fully completed with all required sections in the wiki and are navigable using this link: [https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-1-Project-Report.](https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-2:-Project-Report)
* In the report, you will also find the Deliverable 2 Build System Instructions, Instructions on how to access the Jacoco Test Coverage Reports (In the QA Report), and Instructions on how to access the API docs for the controller methods implemented.
* The Deliverable 2 Build System Instructions operate under the assumption that the build system instructions from Deliverable 1 have been followed (see the section below and the Deliverable 1 project report for that).
* Note about the backlog: As per the professor's instructions when we asked her, to link commits and issues in the backlog, we go into our deliverable 2 issues and, for each issue, add the link to the commit that completes the issue.
------------------------------------------------------------------------------------------------------------------------------------------------------------------------



Build System Instructions for Deliverable 1
--------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* As per TA instructions, the project report is fully completed with all required sections in the wiki and is navigable using this link: https://github.com/McGill-ECSE321-Fall2024/project-group-10/wiki/Deliverable-1-Project-Report.
* As per deliverable 1 instructions and confirmation by TAs, this README file is to contain instructions regarding the required configs aspect of the build system.
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
