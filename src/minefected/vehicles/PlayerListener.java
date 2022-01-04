package minefected.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PlayerListener implements Listener {
    private final HashMap<String, Vehicle> vehicleMap = new HashMap<>();
    private final TestMinecraftVehicle plugin;
    public PlayerListener(TestMinecraftVehicle plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e){
        System.out.println(e.getPlayer().getLocation().getYaw());
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand){
            Entity clickedEntity = e.getRightClicked();
            if(clickedEntity.hasMetadata(e.getPlayer().getName())){
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
                float playerYaw = player.getLocation().getYaw();

                ArmorStand vehicle = (ArmorStand) player.getVehicle();


                if(packet.getFloat().read(1)>0){
                    System.out.println("W");
                    //just a test using player direction, it kinda works.
                    Vector velocity = new Vector(0, 0, 0.3);
//                    if(playerYaw <= 0){
//                        velocity = new Vector(1, 0, 0);
//                    }else{
//                        velocity = new Vector(0, 0, -1);
//                    }
                    System.out.println(playerYaw);
                    vehicle.setHeadPose(new EulerAngle(0, getPlayerDirection(player) * 0.1, 0));
                    vehicle.setVelocity(player.getLocation().getDirection().add(velocity));

                }
                if(packet.getFloat().read(1)<0){
                    System.out.println("S");

                    System.out.println("old yaw = " + vehicle.getLocation().getYaw());
//                    Location loc = vehicle.getLocation();
//                    loc.setYaw(player.getLocation().getYaw());
//                    vehicle.teleport(loc);
                    System.out.println("new yaw = " + vehicle.getLocation().getYaw());

                    Vector velocity = new Vector(0, 0, -0.3);
                    vehicle.setBodyPose(new EulerAngle(0, Math.toRadians(playerYaw), 0));
                    vehicle.setVelocity(player.getLocation().getDirection().add(velocity));
                }
                if(packet.getFloat().read(0)>0){
                    System.out.println("A");
                    Vector velocity = new Vector(-0.3, 0, 0);
                    vehicle.getLocation().setYaw(player.getLocation().getYaw());
                    vehicle.setVelocity(player.getLocation().getDirection().add(velocity));
                }
                if(packet.getFloat().read(0)<0){
                    System.out.println("D");
                    Vector velocity = new Vector(0.3, 0, 0);
                    vehicle.getLocation().setYaw(player.getLocation().getYaw());
                    vehicle.setVelocity(player.getLocation().getDirection().add(velocity));
                }
                if(packet.getBooleans().read(0)){
                    System.out.println("Space");
                    Vector velocity = new Vector(0, 0.1, 0);
                    vehicle.setVelocity(vehicle.getVelocity().add(velocity));
                }
            }
        }), 0, 2000);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e){
        System.out.println("Hay movimiento");
    }

    public static double getPlayerDirection(Player p) {
        String dir;
        float y= p.getLocation().getYaw();
        if( y < 0 ){
            y += 360;
        }
        y %= 360;
        int i = (int)((y+8) / 22.5);
        double pi = Math.PI;

        switch(i){
            case 0:
                return 0;
            case 1:
                return pi/6;
            case 2:
                return pi/4;
            case 3:
                return pi/3;
            case 4:
                return pi/2;
            case 5:
                return (2*pi)/3;
            case 6:
                return (3*pi)/4;
            case 7:
                return (5*pi)/6;
            case 8:
                return pi;
            case 9:
                return (7*pi)/6;
            case 10:
                return (5*pi)/4;
            case 11:
                return (4*pi)/3;
            case 12:
                return (3*pi)/2;
            case 13:
                return (5*pi)/3;
            case 14:
                return (7*pi)/4;
            case 15:
                return (11*pi)/6;
            default:
                return 2*pi;
        }
    }


}


