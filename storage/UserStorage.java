package storage;

import com.google.gson.reflect.TypeToken;
import model.Driver;
import model.Passenger;
import java.lang.reflect.Type;
import java.util.*;

public class UserStorage {

    private final Map<String, Driver> drivers;
    private static final String DRIVER_FILE = "drivers.json";
    private static final String PASSENGER_FILE = "passengers.json";
    private final Map<String, Passenger> passengers;

    public UserStorage() {
        this.drivers = new HashMap<>();
        this.passengers = new HashMap<>();
        loadPassengers();
        loadDrivers();
    }

    public void addDriver(Driver driver) {
        drivers.put(driver.getId(), driver);
        saveDriversInternal();
    }

    public void addPassenger(Passenger passenger) {
        passengers.put(passenger.getId(), passenger);
        savePassengersInternal();
    }

    public Map<String, Driver> getDrivers() {
        return drivers;
    }

    public Map<String, Passenger> getPassengers() {
        return passengers;
    }

    public void saveAll() {
        saveDriversInternal();
        savePassengersInternal();
    }

    public void saveDrivers() {
        saveDriversInternal();
    }

    public void savePassengers() {
        savePassengersInternal();
    }

    private void loadPassengers() {
        try {
            Type listType = new TypeToken<List<Passenger>>(){}.getType();
            List<Passenger> list = Database.loadFromFile(PASSENGER_FILE, listType);

            if (list != null) {
                for (Passenger p : list) {
                    passengers.put(p.getId(), p);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load passengers: " + e.getMessage());
        }
    }

    private void loadDrivers() {
        try {
            Type listType = new TypeToken<List<Driver>>(){}.getType();
            List<Driver> list = Database.loadFromFile(DRIVER_FILE, listType);

            if (list != null) {
                for (Driver d : list) {
                    drivers.put(d.getId(), d);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load drivers: " + e.getMessage());
        }
    }

    private void savePassengersInternal() {
        try {
            List<Passenger> list = new ArrayList<>(passengers.values());
            Database.saveToFile(PASSENGER_FILE, list);
        } catch (Exception e) {
            System.err.println("Could not save passengers: " + e.getMessage());
        }
    }

    private void saveDriversInternal() {
        try {
            List<Driver> list = new ArrayList<>(drivers.values());
            Database.saveToFile(DRIVER_FILE, list);
        } catch (Exception e) {
            System.err.println("Could not save drivers: " + e.getMessage());
        }
    }
}