package me.christolis.traingadget.train;

import me.christolis.traingadget.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Train {

    private Player driver;
    private ArrayList<Player> passengers = new ArrayList<>();
    private final int MAXIMUM_PASSENGERS;
    private Location location;
    private TrainWagon driverWagon;
    private ArrayList<TrainWagon> passengerWagons = new ArrayList<>();
    private float speed = 0f;

    public static float DEFAULT_SPEED = 0.3f;

    /**
     * Constructor for the Train object.
     *
     * @param maximumPassengers The maximum passengers that this train can handle.
     * @param location The starting location of the newly-generated train.
     * @param texture Basically the block that this train will base on.
     */
    public Train(int maximumPassengers, Location location, ItemStack texture) {
        this.MAXIMUM_PASSENGERS = maximumPassengers;

        this.driverWagon = new TrainWagon(location, texture);
    }

    /**
     * Sets the train driver.
     *
     * @param player The player to assign as the driver of this train.
     */
    public void setDriver(Player player) {
        this.driver = player;

        if (driverWagon != null) {
            driverWagon.getArmorStand().setPassenger(player);
        }
    }

    /**
     * Adds a passenger to this train.
     *
     * @param newPassenger The player to assign as the new passenger of this train.
     */
    public void addPassenger(Player newPassenger) {
        passengers.add(newPassenger);
        TrainWagon newPassengerWagon = new TrainWagon(driverWagon.getArmorStand().getLocation(), new ItemStack(Material.DIAMOND_BLOCK));
        newPassengerWagon.setRider(newPassenger);
        passengerWagons.add(newPassengerWagon);
        newPassengerWagon.getArmorStand().setPassenger(newPassenger);
    }

    /**
     * Removes a passenger from this train.
     *
     * @param passenger The player to assign as the removed passenger of this train.
     */
    public void removePassenger(Player passenger) {
        passengers.remove(passenger);
        for (TrainWagon passengerWagon : passengerWagons) {
            if(passengerWagon.getRider() == passenger) {
                passengerWagon.setRider(null);
                passengerWagon.getArmorStand().remove();
                passengerWagons.remove(passengerWagon);
                break;
            }
        }
    }

    /**
     * @return Returns the current driver of this train.
     */
    public Player getDriver() {
        return driver;
    }

    /**
     * @return Returns a list of the train passengers.
     */
    public ArrayList<Player> getPassengers() {
        return passengers;
    }

    /**
     * @return Returns the number of passengers in this train.
     */
    public int getPassengerCount() {
        return passengers.size();
    }

    /**
     * @return Returns the maximum number of passengers in this train.
     */
    public int getMaxPassengerCount() {
        return MAXIMUM_PASSENGERS;
    }

    /**
     * @return Returns the current location of the train (specifically the driver's location).
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return Returns the driver's wagon of this train..
     */
    public TrainWagon getDriverWagon() {
        return driverWagon;
    }

    /**
     * @return Returns a list of all the passenger wagons.
     */
    public ArrayList<TrainWagon> getPassengerWagons() {
        return passengerWagons;
    }

    /**
     * Sets the speed of the train.
     *
     * @param speed The speed of the train.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return Returns the speed of the train.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Gets a train from just the driver.
     *
     * @param player The driver to check.
     * @return null if none was found, the train if one was found.
     */
    public static Train getTrainFromDriver(Player player) {
        for (Train train : Main.trains) {
            if(train.getDriver() == player) return train;
        }
        return null;
    }
}
