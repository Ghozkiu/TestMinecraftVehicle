package minefected.vehicles;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;

public class PlayerListener implements Listener {
    private HashMap<String, Vehicle> vehicleMap =new HashMap<>();
    private TestMinecraftVehicle plugin;

    public PlayerListener(TestMinecraftVehicle plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e){
        System.out.println("Event fired");

        if(e.getFrom().distanceSquared(e.getTo())>0){
            Entity vehicle = e.getPlayer().getVehicle();
            if(vehicle instanceof ArmorStand){
                if(vehicle.getCustomName()!=null && vehicle.getCustomName().equals(e.getPlayer().getName()+"."+0)){
                    Vehicle mfVehicle = vehicleMap.get(e.getPlayer().getName());
                    mfVehicle.move(e.getPlayer());
                }


            }
        }


    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand){
            Entity clickedEntity = e.getRightClicked();
            if(clickedEntity.hasMetadata(e.getPlayer().getName())){

            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Vehicle vehicle = new Vehicle(e.getPlayer(),this.plugin);
        vehicleMap.put(e.getPlayer().getName(),vehicle);
        vehicle.mount(e.getPlayer());
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e){
        System.out.println("Hay movimiento");
    }


}
