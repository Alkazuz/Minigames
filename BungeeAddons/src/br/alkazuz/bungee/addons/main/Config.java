package br.alkazuz.bungee.addons.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config {
	
	public static List<String> BLOCKED_CMDS = new ArrayList<String>();
	
	public Config(Main main) {
		 if (!main.getDataFolder().exists()) {
			 main.getDataFolder().mkdir();
	        }
	        File configFile = new File(main.getDataFolder(), "config.yml");
	        if (!configFile.exists()) {
	            try {
	                configFile.createNewFile();
	                try (InputStream is = main.getResourceAsStream("config.yml");
	                     OutputStream os = new FileOutputStream(configFile)) {
	                    ByteStreams.copy(is, os);
	                }
	            } catch (IOException e) {
	                throw new RuntimeException("Unable to create configuration file", e);
	            }
	        }
	        try {
				main.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
				for(String cmd : main.configuration.getStringList("blocked-cmds")) {
					BLOCKED_CMDS.add(cmd.toLowerCase());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
