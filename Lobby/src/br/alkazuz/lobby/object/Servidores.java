package br.alkazuz.lobby.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.lobby.api.ItemBuilder;
import br.alkazuz.lobby.api.Methods;
import br.alkazuz.lobby.main.Main;

public class Servidores
{
    public static List<Servidor> servidores;
    
    static {
        Servidores.servidores = new ArrayList<Servidor>();
    }
    
    public Servidores() {
        FileConfiguration config = Main.theInstance().config;
        for (String name : config.getConfigurationSection("servers").getKeys(false)) {
        	try {
        		String key = name;
                String flatName = config.getString("servers." + key + ".flat");
                int id = Integer.valueOf(config.getString("servers." + key + ".icon").split(":")[0]);
                int data = Integer.valueOf(config.getString("servers." + key + ".icon").split(":")[1]);
                ItemStack ico = new ItemBuilder(id, 1, data).build();
                List<String> desc = new ArrayList<String>();
                String ip = config.getString("servers." + key + ".ip");
                for (String n : config.getStringList("servers." + key + ".flat")) {
                    desc.add(n.replace("&", "§"));
                }
                Servidor servidor = new Servidor();
                servidor.name = name;
                servidor.flatName = flatName;
                servidor.icon = ico;
                servidor.description = desc;
                servidor.ip = ip;
                if (config.get("servers." + key + ".lobby") != null) {
                    servidor.lobby = Methods.decodeLocation(config.getString("servers." + key + ".lobby"));
                }
                
                if (config.get("servers." + key + ".npcskin") != null) {
                	servidor.npcSkin = config.getString("servers." + key + ".npcskin");
                }
                if (config.get("servers." + key + ".npc") != null) {
                	Location loc = Methods.decodeLocation(config.getString("servers." + key + ".npc"));
                	servidor.npcLocation = loc;
                	NPCServidor npcserver = new NPCServidor(loc, servidor);
                	npcserver.createOrUpdate();
                	servidor.npc = npcserver;
                }
                if (config.get("servers." + key + ".npcs") != null) {
                	for (String top : config.getConfigurationSection("servers." + key + ".npcs").getKeys(false)) {
                		Location loc = Methods.decodeLocation(config.getString("servers." + key + ".npcs."+top));
                		NPCRank npc = new NPCRank(loc, Integer.valueOf(top));
                		servidor.ranks.put(loc
                				, npc);
                	}
                }
                Servidores.servidores.add(servidor);
        	}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Ocorreu um erro ao carregar o servidor " + name);
			}
            
        }
    }
}
