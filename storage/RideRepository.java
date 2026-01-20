package storage;

import model.*;
import java.sql.*;
import java.util.*;

public class RideRepository {

    public void createRide(Ride r) {
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement("""
                INSERT INTO rides (passengerId, driverId, pickup, dropoff, time, status)
                VALUES (?, ?, ?, ?, ?, ?)
            """);
            ps.setString(1, r.passengerId);
            ps.setString(2, r.driverId);
            ps.setString(3, r.pickup);
            ps.setString(4, r.dropoff);
            ps.setString(5, r.time);
            ps.setString(6, r.status.name());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Ride getActiveRideForPassenger(String passengerId) {
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement("""
                SELECT * FROM rides WHERE passengerId=? AND status='ACTIVE'
            """);
            ps.setString(1, passengerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ride r = new Ride();
                r.id = rs.getInt("id");
                r.driverId = rs.getString("driverId");
                r.pickup = rs.getString("pickup");
                r.dropoff = rs.getString("dropoff");
                r.time = rs.getString("time");
                r.status = RideStatus.ACTIVE;
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void markRideCompleted(int rideId) {
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement("""
                UPDATE rides SET status='COMPLETED' WHERE id=?
            """);
            ps.setInt(1, rideId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Ride> getCompletedRides(String passengerId) {
        List<Ride> list = new ArrayList<>();
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement("""
                SELECT * FROM rides WHERE passengerId=? AND status='COMPLETED'
            """);
            ps.setString(1, passengerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ride r = new Ride();
                r.pickup = rs.getString("pickup");
                r.dropoff = rs.getString("dropoff");
                r.time = rs.getString("time");
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
