

# 🚗 Ridezy

## Console-Based Ride Sharing System (Java)

Ridezy is a **console-based ride-sharing application** developed using **Java and Object-Oriented Programming principles**.
The system enables **Passengers** to request rides and **Drivers** to offer rides, while an intelligent **matching engine** pairs them based on distance, time compatibility, and cost.

---

## 📖 Table of Contents

* [Introduction](#introduction)
* [Features](#features)
* [User Roles](#user-roles)
* [System Architecture](#system-architecture)
* [Matching Algorithm](#matching-algorithm)
* [Console Workflow](#console-workflow)
* [Technologies Used](#technologies-used)
* [Design Principles](#design-principles)
* [Future Enhancements](#future-enhancements)
* [Conclusion](#conclusion)

---

## 📌 Introduction

Ridezy is designed to simulate a real-world ride-sharing platform in a **console environment**.
It focuses on clean architecture, modular design, and algorithmic efficiency while maintaining a simple and intuitive user interface.

The system demonstrates:

* Java OOP concepts
* Priority-based matching
* Graph algorithms (shortest path)
* Clean separation between UI, logic, and storage layers

---

## ✨ Features

### General

* Console-based interactive UI
* Secure login and registration via unique IDs
* Real-time ride matching
* Active and completed ride tracking

### Passenger Features

* Register / Login as Passenger
* Request rides dynamically (pickup, drop-off, time)
* View best-matched rides
* Confirm or cancel rides
* View ride history
* Mark rides as completed

### Driver Features

* Register / Login as Driver
* Offer rides with custom routes
* Manage available seats
* View and cancel passengers
* Mark rides as completed

---

## 👥 User Roles

### 👤 Passenger

A passenger can:

* Request a ride
* Choose from available drivers
* Track current and past rides

### 🚘 Driver

A driver can:

* Offer rides
* Manage passengers
* Complete rides

---

## 🧱 System Architecture

```
ui/
 └── ConsoleUI.java          → Handles user interaction

model/
 ├── Passenger.java          → Passenger entity
 ├── Driver.java             → Driver entity
 └── RideMatch.java          → Ride relationship

service/
 └── MatchingEngine.java     → Ride matching logic

storage/
 ├── UserStorage.java        → In-memory user storage
 └── RideHistory.java        → Active & completed rides

datastructures/
 ├── Graph.java              → Road network
 └── Dijkstra.java           → Shortest path algorithm
```

---

## 🧠 Matching Algorithm

Ridezy uses a **priority-based matching system** to determine the most suitable driver.

### Score Calculation

```
Score = (Distance × 0.5) + (Time Difference × 0.3) + (Price × 0.2)
```

* **Distance**: Shortest path using Dijkstra’s algorithm
* **Time Difference**: Absolute difference between driver and passenger times
* **Price**: Cost per seat offered by driver

Lower scores represent **better ride matches**.

A `PriorityQueue<RideMatch>` ensures efficient retrieval of the best option.

---

## 🖥 Console Workflow

### Main Menu

```
1. Register / Login as Passenger
2. Register / Login as Driver
0. Exit
```

### Passenger Menu

```
1. Request Ride
2. View Ride History
3. Cancel Active Ride
4. Mark Ride as Done
5. Logout
```

### Driver Menu

```
1. View / Cancel Passengers
2. Mark Ride as Done
3. Logout
```

---

## 🛠 Technologies Used

* **Java**
* **Object-Oriented Programming**
* **Collections Framework**
* **Priority Queue**
* **Graph Algorithms (Dijkstra)**
* **Console-Based UI**

---

## 🧩 Design Principles

* **Separation of Concerns**
* **Single Responsibility Principle**
* **Loose Coupling**
* **High Cohesion**
* **Scalable Architecture**

Each layer (UI, service, model, storage) operates independently, making the system easy to maintain and extend.

---

## 🚀 Future Enhancements

* Role switching (Passenger ⇄ Driver)
* Database integration (MySQL / PostgreSQL)
* GUI or Web interface
* Real-time GPS integration
* Ride ratings and feedback
* Admin dashboard
* Payment simulation

---

## ✅ Conclusion

Ridezy is a well-structured, efficient, and extensible console-based ride-sharing system.
It effectively demonstrates core software engineering concepts while remaining simple and user-friendly.

This project serves as a strong foundation for building **real-world transportation platforms** or advancing into GUI and web-based applications.

---

### 👨‍💻 Developed By

**Ridezy Team**
Danish Talpur
Sohaib Rafiq

Java Console Project
