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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PlayerListener implements Listener {
    private final HashMap<String, Vehicle> vehicleMap = new HashMap<>();
    private final TestMinecraftVehicle plugin;
    final double pi = Math.PI;
    public PlayerListener(TestMinecraftVehicle plugin){
        this.plugin = plugin;
    }

    //

    @EventHandler
    public void onMovement(PlayerMoveEvent e){
        System.out.println(e.getPlayer().getLocation().getYaw());
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand){
            Entity clickedEntity = e.getRightClicked();
            if(clickedEntity instanceof ArmorStand){
                ArmorStand armorstand = (ArmorStand) clickedEntity;
                System.out.println("Euler Head: "+armorstand.getHeadPose().getY());
                System.out.println("Euler Body: "+armorstand.getBodyPose().getY());
                armorstand.addPassenger(e.getPlayer());
            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Vehicle newVehicle = new Vehicle(e.getPlayer(),this.plugin);
        vehicleMap.put(e.getPlayer().getName(),newVehicle);
        newVehicle.mount(e.getPlayer());


        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                double playerYaw = player.getLocation().getYaw();
                Vehicle vehicle = vehicleMap.get(player.getName());
                ArmorStand vehicleBody = vehicle.getMain();

                // ArmorStand vehicle = (ArmorStand) player.getVehicle();


                if(packet.getFloat().read(1)>0){
                    double reductor = 0.02;

                    System.out.println("W");
                    //just a test using player direction, it kinda works.
                    Vector velocity = new Vector(0, 0, 0.3);

                    double vehicleYaw = vehicleBody.getHeadPose().getY();

                    handleRotation(playerYaw, vehicleYaw, reductor, vehicleBody);



                    vehicleBody.setVelocity(player.getLocation().getDirection().add(velocity));

                }
                if(packet.getFloat().read(1)<0){
                    System.out.println("S");

                    System.out.println("old yaw = " + vehicleBody.getLocation().getYaw());
                    System.out.println("new yaw = " + vehicleBody.getLocation().getYaw());

                    Vector velocity = new Vector(0, 0, -0.3);
                    vehicleBody.setBodyPose(new EulerAngle(0, Math.toRadians(playerYaw), 0));

                    vehicleBody.setVelocity(player.getLocation().getDirection().add(velocity));
                }
                if(packet.getBooleans().read(0)){
                    System.out.println("Space");
                    Vector velocity = new Vector(0, 0.1, 0);
                    vehicleBody.setVelocity(vehicleBody.getVelocity().add(velocity));
                }
            }
        }), 0, 2000);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e){
        System.out.println("Hay movimiento");
    }

    //Handles rotation of an armorStand relative to a player yaw
    public void handleRotation(double playerYaw, double vehicleYaw, double reductor, ArmorStand vehicle){
        if(playerYaw<0)
            playerYaw +=360;

        playerYaw = Math.toRadians(playerYaw);

        double difference = Math.abs(vehicleYaw-playerYaw);

        if(difference<=reductor)
            reductor = difference;

        playerYaw= Math.abs(playerYaw);

        System.out.println("PY: "+playerYaw+" VY: "+vehicleYaw);

        if(playerYaw > vehicleYaw){
            System.out.println("Sumando");
            if(difference<=pi){
                vehicle.setHeadPose(new EulerAngle(0,vehicleYaw+reductor,0));
                vehicle.setBodyPose(new EulerAngle(0,vehicleYaw+reductor,0));
            } else {
                if(vehicleYaw-reductor<0)
                    vehicleYaw = 2*pi;

                vehicle.setHeadPose(new EulerAngle(0,vehicleYaw-reductor,0));
                vehicle.setBodyPose(new EulerAngle(0,vehicleYaw-reductor,0));
            }

        }

        else {
            if(difference<=pi){
                vehicle.setHeadPose(new EulerAngle(0,vehicleYaw-reductor,0));
                vehicle.setBodyPose(new EulerAngle(0,vehicleYaw-reductor,0));
            } else {
                if(vehicleYaw+reductor>2*pi)
                    vehicleYaw = 0;

                vehicle.setHeadPose(new EulerAngle(0,vehicleYaw+reductor,0));
                vehicle.setBodyPose(new EulerAngle(0,vehicleYaw+reductor,0));
            }
        }
    }

}
