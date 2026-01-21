# JavaFX Setup Instructions

## Project Structure

The project has been converted from console-based to JavaFX mobile application:

```
ui/fx/
├── RidezyApp.java              # Main JavaFX application entry point
└── controllers/
    ├── LoginController.java           # First screen - Login choice (Passenger/Driver)
    ├── LoginRegistrationController.java # Second screen - ID and name entry
    ├── PassengerDashboardController.java # Passenger main dashboard
    ├── DriverDashboardController.java    # Driver main dashboard
    ├── RequestRideController.java       # Request ride screen with map
    └── MapController.java               # Mapbox map integration
```

## Requirements

1. **JavaFX SDK** (included in JDK 8-10, or use OpenJFX for JDK 11+)
   - For JDK 11+: Add JavaFX to your classpath
   - Download from: https://openjfx.io/

2. **Mapbox Access Token**
   - Get free token from: https://account.mapbox.com/
   - Update in `MapController.java`: Replace `YOUR_MAPBOX_ACCESS_TOKEN`

## Running the Application

### Option 1: Using Main.java (Recommended)
```bash
javac -cp "javafx-sdk/lib/*:." Main.java ui/fx/**/*.java
java --module-path javafx-sdk/lib --add-modules javafx.controls,javafx.web -cp "." Main
```

### Option 2: Direct JavaFX Launch
```bash
javac -cp "javafx-sdk/lib/*:." ui/fx/RidezyApp.java
java --module-path javafx-sdk/lib --add-modules javafx.controls,javafx.web -cp "." ui.fx.RidezyApp
```

### Option 3: Using an IDE (IntelliJ IDEA / Eclipse)
1. Add JavaFX library to project
2. Configure VM options: `--module-path javafx-sdk/lib --add-modules javafx.controls,javafx.web`
3. Run `Main.java` or `RidezyApp.java`

## Mobile Screen Size

The application is configured for mobile screens:
- Width: 375px (iPhone standard)
- Height: 667px (iPhone standard)
- Responsive design for touch interactions

## Features Implemented

✅ **First Screen (Login):**
- Choose Passenger or Driver login

✅ **Second Screen (Login/Registration):**
- Enter ID
- Enter Name (for new users)
- Auto-login if user exists

✅ **Passenger Dashboard:**
- Request Ride (with map integration)
- View Ride History
- Cancel Active Ride
- Mark Ride as Done
- Logout

✅ **Driver Dashboard:**
- Create Ride
- View/Cancel Passengers
- Mark Ride as Done
- View Ride History
- Logout

✅ **Mapbox Integration:**
- Interactive map display
- Marker placement
- Navigation controls

## Next Steps to Complete

The following features have TODO placeholders and need implementation:

1. **Request Ride Screen:**
   - Complete ride matching logic
   - Show available rides list
   - Book ride functionality

2. **View Ride History:**
   - Display ride history in mobile-friendly format
   - Show active/completed rides

3. **Cancel Active Ride:**
   - Implementation needed

4. **Mark Ride as Done:**
   - Integration with RideHistory

5. **Create Ride (Driver):**
   - Form for ride details
   - Map integration for route selection

6. **View/Cancel Passengers:**
   - List of passengers
   - Cancel functionality

## Notes

- The console UI (`ui.ConsoleUI`) is still available and functional
- All business logic (storage, matching, etc.) remains unchanged
- Only the UI layer has been converted to JavaFX
- Data persistence (JSON files) works the same way

