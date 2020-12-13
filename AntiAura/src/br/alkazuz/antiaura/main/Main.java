package br.alkazuz.antiaura.main;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import br.alkazuz.antiaura.listener.EntityAttackListener;

public class Main extends JavaPlugin{
	private static Plugin plugin;
	
	
	public void onEnable() {
		plugin = this;
		Bukkit.getPluginManager().registerEvents((Listener)new EntityAttackListener(), (Plugin)this);
	}
	
	public static Plugin theInstance() {
		return plugin;
	}

}
