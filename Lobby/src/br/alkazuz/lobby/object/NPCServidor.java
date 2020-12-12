package br.alkazuz.lobby.object;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import br.alkazuz.lobby.api.Methods;
import br.alkazuz.lobby.api.PingServer;
import br.alkazuz.lobby.api.QueueAPI;
import br.alkazuz.lobby.main.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class NPCServidor
{
    private Hologram hologram;
    private NPC npc;
    private Location location;
    private Servidor servidor;
    
    public NPCServidor(Location location, Servidor servidor) {
        this.servidor = servidor;
        this.location = location.add(0.5D, 0.0D, 0.5D);
        hologram = HologramsAPI.createHologram(Main.theInstance(), location.clone().add(0.0, 3D, 0.0));
    }
    
    public Hologram getHologram() {
        return this.hologram;
    }
    
    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }
    
    public NPC getNpc() {
        return this.npc;
    }
    
    public void setNpc(NPC npc) {
        this.npc = npc;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    
    public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public void delete() {
    	if (this.npc != null) {
            this.npc.despawn();
            this.npc.destroy();
        }
    	if (this.hologram != null) {
            this.hologram.delete();
        }
    }
	
	public int lastPlayer = 0;
	public void Update() {
    	if(!location.getChunk().isLoaded()) {
    		location.getChunk().load(true);
    	}
    	
        int playing = 0;
        if(QueueAPI.status.containsKey(servidor)) {
        	 PingServer ping = QueueAPI.status.get(servidor);
        	 if(ping.isOnline()) {
          		playing = ping.onlinePlayers;
          		
          	}
        }
        
        if(lastPlayer != playing) {
        	hologram.removeLine(1);
        	hologram.insertTextLine(1, String.format("§eJogando agora: §b%d§e.", playing));
        }
        
        lastPlayer = playing;
    }
    
    public void createOrUpdate() {
    	if(!location.getChunk().isLoaded()) {
    		location.getChunk().load(true);
    	}
    	
    	npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, UUID.randomUUID(), servidor.name.length() + servidor.flatName.length(), "");
    	
    	System.out.println("npc "+servidor.flatName + " criado em  "+ Methods.encodeLocation(location));
        if(servidor.npcSkin != null) {
        	this.npc.data().set("player-skin-name", servidor.npcSkin);
        }
        
        try {
            this.npc.data().set("nameplate-visible", (Object)false);
        }
        catch (Exception ex) {}
       
        this.npc.spawn(location);
        hologram.appendTextLine(String.format("§6[§l%s§6]", servidor.flatName));
       
        int playing = 0;
        if(QueueAPI.status.containsKey(servidor)) {
        	 PingServer ping = QueueAPI.status.get(servidor);
        	 if(ping.isOnline()) {
          		playing = ping.onlinePlayers;
          	}
        }
     	
        hologram.appendTextLine(String.format("§e%d conectado(s).", playing));
        hologram.appendTextLine("§a§lClique para conectar!");
    }
    
}
