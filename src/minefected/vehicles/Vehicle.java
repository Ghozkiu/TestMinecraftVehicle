package minefected.vehicles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private String owner;
    private ArmorStand main;
    private List<ArmorStand> seats = new ArrayList<>();
    private TestMinecraftVehicle plugin;

    public Vehicle(Player player, TestMinecraftVehicle plugin){
        this.plugin = plugin;
        this.owner = player.getName();
        //We create an independent location with default pitch and yaw set to 0
        Location playerLocation = new Location(player.getWorld(),player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),0,0);
        this.main = (ArmorStand) player.getWorld().spawnEntity(playerLocation, EntityType.ARMOR_STAND);


        seats.add((ArmorStand) player.getWorld().spawnEntity(playerLocation.add(1,0,0),EntityType.ARMOR_STAND));
        main.setCustomName(owner+"."+main);

        main.setMetadata(owner, new FixedMetadataValue(plugin, "MFVehicles"));
        main.setHelmet(new ItemStack(Material.SAPLING));
        seats.get(0).setMetadata(owner, new FixedMetadataValue(plugin, "MFVehicles"));
    }

    public ArmorStand getMain() {
        return main;
    }

    //aaa
    public void move(Player player){
        Vector velocity = new Vector(0, 0, 0.3);
        Vector direction = player.getLocation().getDirection();

    }

    public void mount(Player player){
        main.addPassenger(player);
    }

}