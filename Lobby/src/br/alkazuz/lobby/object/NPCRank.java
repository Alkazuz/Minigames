package br.alkazuz.lobby.object;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import br.alkazuz.lobby.main.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class NPCRank
{
    private Hologram hologram;
    private NPC npc;
    private Location location;
    private int pos;
    
    public NPCRank(Location location, int pos) {
        this.pos = pos;
        this.location = location.add(0.5D, 0.0D, 0.5D);
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
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(int pos) {
        this.pos = pos;
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
    
    public void createAndUpdate(List<PlayerData> list) {
    	if(list.size() < getPos())return;
    	PlayerData clazz = list.get(getPos() - 1);
    	
    	
    	
    	if(!location.getChunk().isLoaded()) {
    		location.getChunk().load(true);
    	}
    	
    	String nick = clazz.nick.replace("\"", "");
    	int wins = clazz.winTotal;
    	String prefix = Main.theInstance().getPrefix(nick);
        if (this.npc == null) {
        	npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, UUID.randomUUID(), nick.length() + this.pos, "");
        }
        this.npc.data().set("player-skin-name", nick);
        try {
            this.npc.data().set("nameplate-visible", (Object)false);
        }
        catch (Exception ex) {}
        this.npc.spawn(location);
        if (this.hologram != null) {
            this.hologram.delete();
        }
        hologram = HologramsAPI.createHologram(Main.theInstance(), location.clone().add(0.0, 3.0, 0.0));
        hologram.clearLines();
        hologram.appendTextLine(String.format("§b%d°§b Lugar", getPos()));
        hologram.appendTextLine(prefix+nick);
        hologram.appendTextLine(String.format("§e%d vitórias.", wins));
    }
    
}
