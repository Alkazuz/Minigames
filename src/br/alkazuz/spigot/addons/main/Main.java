package br.alkazuz.spigot.addons.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import br.alkazuz.spigot.addons.listener.PlayerListener;
import br.alkazuz.spigot.addons.mysql.MySQLConnection;

public class Main extends JavaPlugin
{
    public static Main instance;
    public String channel;
    public FileConfiguration config;
    
    public Main() {
        this.channel = "PortalPlayer";
    }
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public void onEnable() {
        Main.instance = this;
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        config = ConfigManager.getConfig("config");
        SpigotConfig.load(this);
        new MySQLConnection();
    }
}
