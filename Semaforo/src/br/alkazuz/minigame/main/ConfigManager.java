package br.alkazuz.minigame.main;

import java.io.*;
import java.util.logging.*;

import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;

import com.google.common.base.*;

public class ConfigManager
{
    public static FileConfiguration load(String filename)
    {
        File file = new File(Main.theInstance().getDataFolder(), filename + ".yml");
        if (!file.exists()) {
            Main.theInstance().saveResource(file.toString(), false);
        }
        return load(file);
    }
    public static void save(String filename, FileConfiguration config)
    {
        File file = new File(Main.theInstance().getDataFolder(), filename + ".yml");
        
        try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            os.write(config.saveToString());
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save " + filename, ex);
        }
    }
    
    private static YamlConfiguration load(File file)
    {
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
        return config;
    }
}
