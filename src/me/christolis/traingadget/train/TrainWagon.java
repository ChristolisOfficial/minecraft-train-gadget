package me.christolis.traingadget.train;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class TrainWagon {

    private ArmorStand wagonArmorStand;

    private Entity rider;

    public Location lastPositions[] = new Location[500];
    public int lastPositionsIndex = 0;

    public int MAXIMUM_CACHE_POSITION_STORAGE;

    /**
     * The train wagon constructor.
     *
     * @param location The location of the train wagon.
     * @param texture Basically the block of the texture.
     */
    public TrainWagon(Location location, final ItemStack texture) {
        this.wagonArmorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        this.wagonArmorStand.setVisible(false);
        this.wagonArmorStand.setBasePlate(false);
        this.wagonArmorStand.setGravity(true);
        this.wagonArmorStand.setArms(false);
        this.wagonArmorStand.setHelmet(texture);
    }

    /**
     * @return The armor stand of the wagon.
     */
    public ArmorStand getArmorStand() {
        return wagonArmorStand;
    }

    /**
     * @return The rider of this wagon.
     */
    public Entity getRider() {
        return rider;
    }

    /**
     * @param rider The new rider of this wagon.
     */
    public void setRider(Entity rider) {
        this.rider = rider;
    }
}
