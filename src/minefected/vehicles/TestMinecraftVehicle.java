package minefected.vehicles;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TestMinecraftVehicle extends JavaPlugin {
    public String configPath;


    public void onEnable() {
        getLogger().info(ChatColor.GREEN + "[TestVehicles] Enabled");
        configRegister();
    }


    public void onDisable() {
        getLogger().info(ChatColor.YELLOW + "[TestVehicles] Disabling...");
    }

    public void configRegister() {
        File config = new File(getDataFolder(), "config.yml");
        this.configPath = config.getPath();
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }
}
