package br.alkazuz.minigame.main;

import java.io.*;
import org.bukkit.configuration.file.*;

public class ConfigManager
{
    public static void createNewConfig(String file) {
        if (!new File(Main.theInstance().getDataFolder(), String.valueOf(String.valueOf(file)) + ".yml").exists()) {
            Main.theInstance().saveResource(String.valueOf(String.valueOf(file)) + ".yml", false);
        }
    }
    
    public static FileConfiguration getConfig(String file) {
        File arquivo = new File(Main.theInstance().getDataFolder() + "/" + file + ".yml");
        if (!arquivo.exists()) {
            createNewConfig(file);
        }
        FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(arquivo);
        return config;
    }
}
