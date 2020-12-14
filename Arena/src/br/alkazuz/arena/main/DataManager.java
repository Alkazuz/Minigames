package br.alkazuz.arena.main;

import java.io.*;
import org.bukkit.configuration.file.*;

public class DataManager
{
    public static void createFolder(final String folder) {
        try {
            final File pasta = new File(Main.theInstance().getDataFolder() + File.separator + folder);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static void createFile(final File file) {
        try {
            file.createNewFile();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static File getFolder(final String folder) {
        final File Arquivo = new File(Main.theInstance().getDataFolder() + File.separator + folder);
        return Arquivo;
    }
    
    public static File getFile(final String file, final String folder) {
        final File Arquivo = new File(Main.theInstance().getDataFolder() + File.separator + folder, String.valueOf(file) + ".yml");
        return Arquivo;
    }
    
    public static File getFile(final String file) {
        final File Arquivo = new File(Main.theInstance().getDataFolder() + File.separator + file + ".yml");
        return Arquivo;
    }
    
    public static FileConfiguration getConfiguration(final File file) {
        final FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        return config;
    }
    
    public static void deleteFile(final File file) {
        file.delete();
    }
}
