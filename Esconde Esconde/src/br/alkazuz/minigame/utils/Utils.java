package br.alkazuz.minigame.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Utils
{
	
	public static List<Material> getAmountMaterials(Location loc){
		List<Material> list = new ArrayList<Material>();
		Chunk c = loc.getChunk();
		if(!c.isLoaded()) {
			c.load(true);
		}
		for(int x = -16; x <= 16; x++) {
			for(int y = -50; y <= 50; y++) {
				for(int z = -16; z <= 16; z++) {
					Block block = loc.getWorld().getBlockAt(loc.getBlockX() + x,
							loc.getBlockY() + y, loc.getBlockZ() + z);
					if(block != null && !list.contains(block.getType())) {
						list.add(block.getType());
					}
				}
			}
		}
		return list;
	}
	
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            if (file.listFiles().length == 0) {
                file.delete();
            }
            else {
                File[] listFiles;
                for (int length = (listFiles = file.listFiles()).length, i = 0; i < length; ++i) {
                    deleteFile(listFiles[i]);
                }
            }
        }
        else {
            file.delete();
        }
    }
    
    public static void copyDirectory(File file, File file2) {
        if (file.isDirectory()) {
            if (!file2.exists()) {
                file2.mkdir();
            }
            String[] list;
            for (int length = (list = file.list()).length, i = 0; i < length; ++i) {
                String s = list[i];
                copyDirectory(new File(file, s), new File(file2, s));
            }
        }
        else {
            if (file.getName().equals("uid.dat") || file.getName().equals("session.dat")) {
                return;
            }
            try {
                copyFile(new FileInputStream(file), file2);
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void copyFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] array = new byte[710];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                fileOutputStream.write(array, 0, read);
            }
            fileOutputStream.close();
            inputStream.close();
        }
        catch (Exception ex) {}
    }
}
