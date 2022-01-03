package minefected.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;

public class PlayerListener implements Listener {
    private HashMap<String, Vehicle> vehicleMap = new HashMap<>();
    private TestMinecraftVehicle plugin;
    public PlayerListener(TestMinecraftVehicle plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e){




    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand){
            Entity clickedEntity = e.getRightClicked();
            System.out.println("entro 1");
            if(clickedEntity.hasMetadata(e.getPlayer().getName())){
                System.out.println("entro 2");
                clickedEntity.addPassenger(e.getPlayer());

            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Vehicle vehicle = new Vehicle(e.getPlayer(),this.plugin);
        vehicleMap.put(e.getPlayer().getName(),vehicle);
        vehicle.mount(e.getPlayer());

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                Entity b = player.getVehicle();
                if (b instanceof Player) {
                    return;
                }

                if(packet.getFloat().read(1)>0){
                    System.out.println("W");
                }
                if(packet.getFloat().read(1)<0){
                    System.out.println("S");
                }
                if(packet.getFloat().read(0)>0){
                    System.out.println("A");
                }
                if(packet.getFloat().read(0)<0){
                    System.out.println("D");
                }
                if(packet.getBooleans().read(0)){
                    System.out.println("Space");
                }
            }
        }), 0, 2000);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e){
        System.out.println("Hay movimiento");
    }




}
