# Audio Management System

This project is part of the coursework for **Information Systems 1 (13S113IS1)** at the **School of Electrical Engineering, University of Belgrade**, academic year 2024/2025.  

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

2. **Run the Subsystems**:
   - Navigate to each subsystem folder and start the application.

3. **Run the Central Server**:
   - Start the central server to enable communication between the client and subsystems.

4. **Run the Client Application**:
   - Use the console to send requests and interact with the system.

---

## 📜 UML Diagrams

The following UML diagrams are included in the `uml/` folder:
1. **Class Diagrams**: Illustrating the structure of each application.
2. **Sequence Diagram**: Depicting the workflow for subscription creation.

