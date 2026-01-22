# 🚗 Ridezy  
### GUI-Based Ride Sharing System (JavaFX)

Ridezy is a **desktop-based ride-sharing application** developed using **Java and JavaFX**, following strong **Object-Oriented Programming (OOP)** principles.  
The system allows **Passengers** to request rides and **Drivers** to offer rides, while an intelligent **matching engine** pairs them based on distance, availability, and score.

The application provides a **graphical user interface (GUI)** for smooth user interaction and persists ride history using **JSON file storage**.

---

## 📌 Features

### 👤 User Management
- Passenger and Driver registration
- Unique user identification
- Centralized user storage

### 🚕 Ride Management
- Ride request creation
- Driver–Passenger matching
- Active ride detection
- Ride completion tracking
- Ride cancellation

### 📊 Ride History
- Persistent ride storage using JSON
- Active and completed ride tracking
- Pickup and drop-off location support
- Distance and score calculation

### 🖥️ Graphical User Interface (JavaFX)
- Clean and interactive UI
- Separate views for passengers and drivers
- Event-driven design using JavaFX controllers
- User-friendly navigation

---

## 🛠️ Technologies Used

- **Java (JDK 17+)**
- **JavaFX**
- **Gson** (for JSON serialization & deserialization)
- **Object-Oriented Programming (OOP)**
- **File Handling (JSON-based persistence)**

---

## 📂 Project Structure

Ridezy/
├── .idea/                  # IDE config and library references
│   └── libraries/
│       ├── javafx_base.xml
│       └── sqlite_jdbc_3_51_1_0.xml
├── algorithm/              # Pathfinding algorithms
│   ├── Dijkstra.class
│   └── Dijkstra.java
├── config/                 # Application configuration
│   └── AppConfig.java
├── data/                   # JSON data storage
│   ├── drivers.json
│   ├── passengers.json
│   └── rides.json
├── datastructures/         # Graph and edges
│   ├── Edge.class
│   ├── Edge.java
│   ├── Graph.class
│   └── Graph.java
├── img/                    # Images
│   └── logo.png
├── lib/                    # External libraries
│   └── gson-2.10.1.jar
├── model/                  # Core entities
│   ├── Driver.class
│   ├── Driver.java
│   ├── Passenger.class
│   ├── Passenger.java
│   ├── Ride.java
│   ├── RideMatch.class
│   ├── RideMatch.java
│   ├── RideStatus.class
│   └── RideStatus.java
├── service/                # Business logic
│   ├── MatchingEngine.class
│   └── MatchingEngine.java
├── storage/                # File handling and persistence
│   ├── Database.java
│   ├── RideHistory.class
│   ├── RideHistory.java
│   └── UserStorage.java
├── ui/                     # User interface
│   ├── ConsoleUI.class
│   ├── ConsoleUI.java
│   └── fx/
│       ├── RidezyApp.java
│       └── controllers/
│           ├── CreateRideController.java
│           ├── CurrentRideController.java
│           ├── DriverDashboardController.java
│           ├── LoginController.java
│           ├── LoginRegistrationController.java
│           ├── MapController.java
│           ├── PassengerDashboardController.java
│           ├── RequestRideController.java
│           ├── RideHistoryController.java
│           ├── SelectRideController.java
│           └── SplashScreenController.java
└── util/                   # Utility classes
    ├── CityGraphBuilder.class
    └── CityGraphBuilder.java


---

## 🧠 Core Concepts Applied

- Encapsulation, Inheritance, Polymorphism
- Separation of concerns (Model–View–Controller)
- Generics and collections
- File I/O with JSON
- Exception handling
- JavaFX event handling

---

## ▶️ How to Run the Project

### Prerequisites
- Java JDK 17 or later
- JavaFX properly configured
- Gson JAR added to classpath

### Steps
1. Clone or download the repository
2. Open the project in an IDE (IntelliJ IDEA / Eclipse)
3. Ensure `gson-2.10.1.jar` is added to the project libraries
4. Run `Main.java`

---

## 📄 Data Persistence

- Ride data is stored in `rides.json`
- Uses Gson for serialization and deserialization
- Automatically loads previous ride history on startup
- Saves updates after each ride operation

---

## 🔐 Ride Status Handling

Each ride can have the following states:
- `ACTIVE`
- `COMPLETED`

The system ensures:
- Only one active ride per user
- Accurate ride status updates
- Safe recovery of state on application restart

---

## 🎯 Future Enhancements

- Real-time distance calculation using maps
- Pricing algorithm based on distance and demand
- Authentication & login system
- Database integration (MySQL/PostgreSQL)
- Multi-threaded ride matching
- UI animations and themes

---

## 👨‍💻 Author

**Danish Talpur**                                                        
**Sohaib Rafiq** 
Java & Software Engineering Student  
Focused on clean architecture, OOP design, and scalable systems

---

## 📜 License

This project is intended for **educational and learning purposes**.

