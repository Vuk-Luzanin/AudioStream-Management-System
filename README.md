# Audio Management System

This project is part of the coursework for **Information Systems 1** at the **School of Electrical Engineering, University of Belgrade**.  
The system manages audio recordings, users, subscriptions, and related functionalities using a **client-server architecture** with **RESTful APIs**, **JMS (Java Message Service)**, and **JPA (Java Persistence API)** for database interactions.  

---

## 📂 Project Structure

The project consists of the following components:

### 1. **Client Application**
- A **Java SE** application with a console or graphical interface.
- Sends user requests to the central server using HTTP.
- Displays server responses to the user.

### 2. **Central Server**
- Bridges communication between the client application and subsystems.
- Processes REST requests and forwards them to appropriate subsystems via JMS.
- Does not store data locally.

### 3. **Subsystems**
- **Subsystem 1:** Manages data related to locations and users.
- **Subsystem 2:** Handles categories and audio recordings.
- **Subsystem 3:** Manages packages, subscriptions, listening sessions, ratings, and favorite lists.

---

## ⚙️ Technologies Used
- **Java SE** for the client application.
- **JMS** (Java Message Service) for inter-subsystem communication.
- **JPA** (Java Persistence API) with **MySQL** for database operations.
- **MySQL Workbench** for database design and management.
- **NetBeans IDE** (or any IDE of your choice) for development.
- **StarUML** or similar tools for creating UML diagrams.

---

## 💾 Database Design

The system uses **MySQL** as the database backend, with the following key features:
- Relational database design with foreign key constraints.
- Tables for users, locations, audio recordings, categories, packages, subscriptions, listening sessions, ratings, and favorite lists.
- Pre-filled data for testing and demonstration purposes.

---

## 🛠️ How to Run

1. **Set Up the Database**:
   - Install MySQL and import the database dump file from the `baze/` folder.
   - Then initialize databases (podsistem1, podsistem2 and podsistem3)

2. **Set up Glassfish server**:
   - In Netbeans IDE, in Services -> Servers -> add Server -> Glassfish server 5.1.0.
   - Start Glassfish server and open http://localhost:4848/
   - create JDBC Connection Pools: (podsistem1Pool, podsistem2Pool and podsistem3Pool) with following specifications:
    - Resource Type: `javax.sql.DataSource`
    - Datasource Classname: `com.mysql.cj.jdbc.MysqlDataSource`
  - In Additional Properties:
    - password: `123` (The password for your MySQL user must not be empty, and you can change it in MySQL Workbench -> Administration -> Users and Privileges)
    - databaseName: `podsistem1`/`podsistem2`/`podsistem3` for the corresponding pool
    - serverName: `localhost`
    - user: The name of your MySQL user, by default there is a user named `root`.
    - portnumber: By default `3306`, but sometimes MySQL Workbench may set it to `3308`
    - useSSL: `false`
    - allowPublicKeyRetrieval: `true`
  - create JDBC Resources: `podsistem1Resource`/`podsistem1Resource`/`podsistem1Resource` for the corresponding pool
  - create JMS Connection Factory: `projectConnFactory`
  - create JMS Destination Resources: `projectTopicServer`
  - Add `mysql-connector-j-9.1.0.jar` (given in biblioteke folder) in GlassFish server installation folder and in project Libraries

2. **Set up Netbeans IDE**
  - in Services -> Databases -> add Driver -> add `mysql-connector-j-9.1.0.jar` file

3. **Start project** 
  - Set Java JDK 1.8 for every project
  - Run all applications
  - Application server will be available on http://localhost:8080/

---

## 📜 UML Diagrams

The following UML diagrams are included in the `uml/` folder:
1. **Class Diagrams**: Illustrating the structure of each application and entity classes.
2. **Sequence Diagram**: Depicting the workflow for subscription creation.

