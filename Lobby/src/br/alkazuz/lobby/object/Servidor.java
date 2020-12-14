package br.alkazuz.lobby.object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.lobby.api.ItemBuilder;

public class Servidor
{
    public String name;
    public String flatName;
    public ItemStack icon;
    public String ip;
    public List<String> description;
    public Location lobby;
    public AtomicInteger played;
    public Long lastStart;
    public HashMap<Location, NPCRank> ranks = new HashMap<Location, NPCRank>();
    public NPCServidor npc;
	public String npcSkin;
    public Location npcLocation;
	
    public Servidor() {
        this.played = new AtomicInteger(0);
        this.lastStart = 0L;
    }
    
    public ItemStack getIcon() {
        return new ItemBuilder(this.icon.clone()).name("§e§l" + this.flatName).listLore(this.description).build();
    }
    
    public void start() {
    	File serverPath = new File("/root/Servidor/" + this.flatName);
    	File serverPlugin = new File("/root/Servidor/" + this.flatName + "/plugins/"+flatName+".jar");
    	File serversFiles = new File("/root/ServerFiles");
    	File serverFolderPlugin = new File("/root/ServerPlugins/"+flatName+".jar");
    	
    	if(serverFolderPlugin.exists()) {
    		if(serverPlugin.exists()) {
    			serverPlugin.delete();
    		}
    		try {
				FileUtils.copyFile(serverFolderPlugin, serverPlugin);
			} catch (IOException e) { e.printStackTrace(); }
    	}
    	
    	for(File files : serversFiles.listFiles()) {
    		String fileName = files.getName(); 
    		File filePath = new File("/root/Servidor/" + this.flatName + "/plugins/"+fileName);
    		if(filePath.exists()) {
    			filePath.delete();
    		}
    		if(files.isDirectory()) {
    			try {
					FileUtils.copyDirectory(files, filePath);
				} catch (IOException e) { e.printStackTrace(); }
    		}else {
    			try {
					FileUtils.copyFile(files, filePath);
				} catch (IOException e) { e.printStackTrace(); }
    		}
    	}
    	
        ProcessBuilder pb = new ProcessBuilder(new String[] { "sh", "start.sh" });
        pb.directory(serverPath);
        try {
            pb.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void copyFile(InputStream inputStream, File file) {
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
