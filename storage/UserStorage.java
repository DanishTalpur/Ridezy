package storage;

import model.Driver;
import model.Passenger;

import java.util.HashMap;
import java.util.Map;

public class UserStorage {

    private final Map<String, Driver> drivers;
    private final Map<String, Passenger> passengers;

    public UserStorage() {
        this.drivers = new HashMap<>();
        this.passengers = new HashMap<>();
    }

    public void addDriver(Driver driver) {
        drivers.put(driver.getId(), driver);
    }

    public void addPassenger(Passenger passenger) {
        passengers.put(passenger.getId(), passenger);
    }

    public Map<String, Driver> getDrivers() {
        return drivers;
    }

    public Map<String, Passenger> getPassengers() {
        return passengers;
    }
}
