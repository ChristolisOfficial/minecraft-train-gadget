package me.christolis.traingadget;

import me.christolis.traingadget.train.Train;
import me.christolis.traingadget.train.TrainWagon;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;

import static me.christolis.traingadget.train.Train.DEFAULT_SPEED;

public class Main extends JavaPlugin implements Listener {

    public int trainUpdateTaskID;

    public static ArrayList<Train> trains = new ArrayList<>();
    public ArrayList<Player> inTrain = new ArrayList<>();


    /**
     * Gets called when the plugin is enabled on the running server.
     */
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        trainUpdateTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Train train : this.trains) {
                TrainWagon driverWagon = train.getDriverWagon();
                ArmorStand driverWagonArmorStand = train.getDriverWagon().getArmorStand();
                Player driver = train.getDriver();
                float m = train.getSpeed();
                if (driverWagon.getArmorStand().getPassenger() == null) return;

                driverWagon.lastPositions[driverWagon.lastPositionsIndex] = driverWagon.getArmorStand().getLocation();
                System.out.println("lastPositions | Cell " + driverWagon.lastPositionsIndex + " has changed."); //  Debug.

                driverWagonArmorStand.setVelocity(driver.getLocation().getDirection().multiply(m));
                driver.getWorld().playEffect(driver.getLocation().add(0.0f, 0.7f, 0.0f), Effect.COLOURED_DUST, 5);

                for (int i = 0; i < train.getPassengerWagons().size(); i++) {
                    TrainWagon passengerWagon = train.getPassengerWagons().get(i);
                    System.out.println("Attempting to change cell " + driverWagon.lastPositionsIndex); //  Debug.
                    passengerWagon.getArmorStand().setVelocity(driverWagon.lastPositions[driverWagon.lastPositionsIndex + i].getDirection().multiply(m));
                    passengerWagon.getRider().sendMessage(driverWagon.lastPositionsIndex + i + "");
                    driver.getWorld().playEffect(train.getPassengers().get(i).getLocation().add(0.0f, 0.7f, 0.0f), Effect.COLOURED_DUST, 5); //TODO
                }

                driverWagon.lastPositionsIndex++;
                if(driverWagon.lastPositionsIndex > (train.getMaxPassengerCount() + 1)) driverWagon.lastPositionsIndex = 0;
            }
        }, 1L, 1L);
    }

    /**
     * Gets called when the plugin is disabled on the running server.
     */
    @Override
    public void onDisable() {
        for (Train trains : this.trains) {
            for (TrainWagon trainsPassengerWagons : trains.getPassengerWagons()) {
                trainsPassengerWagons.getArmorStand().remove();
            }
            trains.getDriverWagon().getArmorStand().remove();
            for (Player trainPassengers : trains.getPassengers()) trainPassengers.sendMessage(ChatColor.RED + "You had to get out of " + trains.getDriver().getName() + "'s train because the server has restarted. Sorry about that!");
            trains.getDriver().sendMessage(ChatColor.RED + "Your train had to be destroyed because the server has restarted. Sorry about that!");
            inTrain.remove(trains.getDriver());
        }
        trains.clear();
    }

    /**
     * Gets called when a command is being sent to the server for processing.
     * @param sender The sender of the command (can be any entity, even console).
     * @param cmd The command the player wrote.
     * @param commandLabel The command label.
     * @param args Tokenized command arguments.
     * @return A boolean on if the command succeeded.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String commandName = cmd.getName().toLowerCase();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (commandName.equals("spawntrain")) {
                if (inTrain.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You are already riding a train. Exit the current one and try again!");
                    return true;
                }
                ItemStack skull = Util.generateSkull(player.getName(), player.getName(), 1);

                Train train = new Train(5, player.getLocation(), skull);
                train.setDriver(player);
                train.setSpeed(0.3f);
                trains.add(train);
                player.sendMessage(ChatColor.GREEN + "Your new train has spawned!");
                inTrain.add(player);
                return true;
            }

            if (commandName.equals("trainboost")) {
                if (!inTrain.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You are not in a train to do that!");
                    return true;
                }

                Train train = Train.getTrainFromDriver(player);
                Player driver = train.getDriver();
                train.setSpeed(2f);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> train.setSpeed(DEFAULT_SPEED), 100L);

                driver.getWorld().createExplosion(driver.getLocation(), 0.0f, false);
                player.sendMessage(ChatColor.RED + "W" + ChatColor.YELLOW + "o" + ChatColor.DARK_PURPLE + "o" + ChatColor.GREEN + "s" + ChatColor.DARK_BLUE + "h" + ChatColor.WHITE+ "!");
            }
        }
        return false;
    }

    /**
     * This event gets called when an entity dismounts an other entity.
     *
     * @param event An instance of the event.
     */
    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            for (Train trainIterate : trains) {
                if (trainIterate.getDriver() == player) {
                    for (TrainWagon trainWagons : trainIterate.getPassengerWagons()) {
                        if(trainWagons.getArmorStand().getPassenger() != null) trainWagons.getArmorStand().getPassenger().sendMessage(ChatColor.GREEN + "You have exited the train because the driver " + trainIterate.getDriver().getName() + " destroyed it!");
                        trainWagons.getArmorStand().remove();
                    }
                    trainIterate.getDriverWagon().getArmorStand().remove();
                    trains.remove(trainIterate);
                    player.sendMessage(ChatColor.GREEN + "Your train has been destroyed because you exited it!");
                    inTrain.remove(player);
                    break;
                }

                for(TrainWagon passengerWagons : trainIterate.getPassengerWagons()) {
                    if (passengerWagons.getRider() == player) {
                        trainIterate.removePassenger(player);
                        player.sendMessage(ChatColor.GREEN + "Exited the train!");
                        inTrain.remove(player);
                        break;
                    }
                }
            }
        }
    }

    /**
     * This event gets called when a player interacts with an other entity.
     *
     * @param event An instance of the event.
     */
    @EventHandler
    public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Entity rightClickedEntity = event.getRightClicked();
        Player clicker = event.getPlayer();

        for (Train trains : this.trains) {
            if (trains.getDriver().equals(rightClickedEntity)) {
                trains.addPassenger(clicker);
                inTrain.add(clicker);
                break;
            }
        }
    }

    /**
     * This event gets called when an entity is damaged.
     *
     * @param event An instance of the event.
     */
    @EventHandler
    public void entityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if ((event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) && inTrain.contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * This event gets called when a player dies.
     *
     * @param event An instance of the event.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (inTrain.contains(player)) {
            for (Train trainIterate : trains) {
                if (trainIterate.getDriver() == player) {
                    for (TrainWagon trainWagons : trainIterate.getPassengerWagons()) {
                        trainWagons.getArmorStand().remove();
                    }
                    trainIterate.getDriverWagon().getArmorStand().remove();
                    trains.remove(trainIterate);
                    player.sendMessage(ChatColor.GREEN + "Your train has been destroyed because you died!");
                    inTrain.remove(player);
                    break;
                }
            }
        }
    }

    /**
     * This event gets called when a player quits the running server.
     *
     * @param event An instance of the event.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inTrain.contains(player)) {
            for (Train trainIterate : trains) {
                if (trainIterate.getDriver() == player) {
                    for (TrainWagon trainWagons : trainIterate.getPassengerWagons()) {
                        trainWagons.getArmorStand().remove();
                    }
                    trainIterate.getDriverWagon().getArmorStand().remove();
                    trains.remove(trainIterate);
                    inTrain.remove(player);
                    break;
                }
            }
        }
    }
}
