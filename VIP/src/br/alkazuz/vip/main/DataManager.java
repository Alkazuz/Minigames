package br.alkazuz.vip.main;

import java.io.*;
import org.bukkit.configuration.file.*;

public class DataManager
{
    public static void createFolder(String folder) {
        try {
            File pasta = new File(Main.theInstance().getDataFolder() + File.separator + folder);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static void createFile(File file) {
        try {
            file.createNewFile();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static File getFolder(String folder) {
        File Arquivo = new File(Main.theInstance().getDataFolder() + File.separator + folder);
        return Arquivo;
    }
    
    public static File getFile(String file, String folder) {
        File Arquivo = new File(Main.theInstance().getDataFolder() + File.separator + folder, String.valueOf(file) + ".yml");
        return Arquivo;
    }
    
    public static File getFile(String file) {
        File Arquivo = new File(Main.theInstance().getDataFolder() + File.separator + file + ".yml");
        return Arquivo;
    }
    
    public static FileConfiguration getConfiguration(File file) {
        FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        return config;
    }
    
    public static void deleteFile(File file) {
        file.delete();
    }
}
