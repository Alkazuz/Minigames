package br.alkazuz.vip.main;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import br.alkazuz.vip.api.MorphAPI;
import br.alkazuz.vip.gadget.Gadgets;
import br.alkazuz.vip.menus.VIPMenu;
import de.robingrether.idisguise.api.DisguiseAPI;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener
{
    private static Main instance;
    public FileConfiguration config;
    public FileConfiguration games;
    public Economy economy;
    public DisguiseAPI api;
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public void writeMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage("§e[VIP] §7" + msg);
    }
    
    //private CancellationDetector<CreatureSpawnEvent> detector = new CancellationDetector<CreatureSpawnEvent>(CreatureSpawnEvent.class);
    
    @Override
    public void onEnable() {
 
        //detector.addListener(new CancelListener<CreatureSpawnEvent>() {
    	//@Override
    	//public void onCancelled(Plugin plugin, CreatureSpawnEvent event) {
    	//     System.out.println(event + " cancelled by " + plugin);
    	//    }
    	//});
        Main.instance = this;
        this.config = ConfigManager.getConfig("config");
        this.api = (DisguiseAPI)this.getServer().getServicesManager().getRegistration((Class)DisguiseAPI.class).getProvider();
        this.setupEconomy();
        itemNoPickupString = UUID.randomUUID().toString();
        MorphAPI.load();
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new VIPMenu(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents(new Gadgets(), (Plugin)this);
        Gadgets.load();
    }
    
    private boolean setupEconomy() {
        RegisteredServiceProvider registration = Bukkit.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (registration != null) {
            this.economy = (Economy)registration.getProvider();
            return true;
        }
        return false;
    }
    private String itemNoPickupString;
	public String getItemNoPickupString() {
		return itemNoPickupString;
	}
}
