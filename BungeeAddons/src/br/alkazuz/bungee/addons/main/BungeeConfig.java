package br.alkazuz.bungee.addons.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeConfig {
	
	
	public static boolean MAINTENANCE;
	public static String MAINTENANCE_ERROR;
	public static List<String> MAINTENANCE_USERS;
	public static Main main;
	public static void load(Main main) {
		BungeeConfig.main = main;
		MAINTENANCE = main.configuration.getBoolean("maintenance.enabled");
		MAINTENANCE_ERROR = main.configuration.getString("maintenance.error").replace("&", "§").replace("%n%", "\n");
		MAINTENANCE_USERS = main.configuration.getStringList("maintenance.users");
	}
	
	public static void save() {
		File configFile = new File(main.getDataFolder(), "config.yml");
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(main.configuration, configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
