package me.christolis.traingadget;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class Util {

    private static final Method[] methods = ((Supplier<Method[]>) () -> {
        try {
            Method getHandle = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity").getDeclaredMethod("getHandle");

            return new Method[] {
                    getHandle, getHandle.getReturnType().getDeclaredMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
            };
        } catch (Exception ex) {return null;}
    }).get();

    /**
     * Teleports an entity to a given location (Optimized).
     *
     * @param entity The entity to teleport.
     * @param loc The location to teleport the entity to.
     */
    public static void teleportEntityEx(Entity entity, Location loc) {
        try {
            methods[1].invoke(methods[0].invoke(entity), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generates a customized skull.
     *
     * @param skullOwner The 'SkullOwner' parameter.
     * @param displayName The display name of the given item stack.
     * @param quantity The quantity of this item stack.
     * @return The newly-generated item stack.
     */
    public static ItemStack generateSkull(String skullOwner, String displayName, int quantity) {
        ItemStack skull     = new ItemStack(Material.SKULL_ITEM, quantity, (byte) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

        if (displayName != null) skullMeta.setDisplayName(ChatColor.RESET + displayName);
        skullMeta.setOwner(skullOwner);
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
