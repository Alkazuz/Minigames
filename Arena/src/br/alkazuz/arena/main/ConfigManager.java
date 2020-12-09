package br.alkazuz.arena.main;

import java.io.*;
import org.bukkit.configuration.file.*;

public class ConfigManager
{
    public static void createNewConfig(final String file) {
        if (!new File(Main.theInstance().getDataFolder(), String.valueOf(String.valueOf(file)) + ".yml").exists()) {
            Main.theInstance().saveResource(String.valueOf(String.valueOf(file)) + ".yml", false);
        }
    }
    
    public static FileConfiguration getConfig(final String file) {
        final File arquivo = new File(Main.theInstance().getDataFolder() + "/" + file + ".yml");
        if (!arquivo.exists()) {
            createNewConfig(file);
        }
        final FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(arquivo);
        return config;
    }
}
