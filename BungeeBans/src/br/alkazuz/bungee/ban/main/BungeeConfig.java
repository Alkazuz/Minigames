package br.alkazuz.bungee.ban.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeConfig {
	
	public static List<String> MUTE_TEMP;
	public static List<String> MUTE;
	public static List<String> BAN_TEMP;
	public static List<String> BAN;
	
	public static List<String> MUTE_CMDS;
	
	public static Main main;
	public static void load(Main main) {
		BungeeConfig.main = main;
		MUTE_TEMP = loadList("messages.mute-temp", main.configuration);
		MUTE = loadList("messages.mute", main.configuration);
		BAN_TEMP = loadList("messages.ban-temp", main.configuration);
		BAN = loadList("messages.ban", main.configuration);
		
		MUTE_CMDS = loadList("config.mute-commands", main.configuration);
	}
	
	public static List<String> loadList(String key, Configuration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "§"));
        }
        return list;
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
