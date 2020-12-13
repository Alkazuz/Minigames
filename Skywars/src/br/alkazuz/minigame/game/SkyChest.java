package br.alkazuz.minigame.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

public class SkyChest {
	
	public Hologram hologram;
	public Location location;
	public Chest chest;
	public ChestType type;
	
	public SkyChest(Hologram hologram, Location location, ChestType type) {
		this.hologram = hologram;
		this.location = location;
		this.type = type;
		if(!location.getChunk().isLoaded()) {
			location.getChunk().load(true);
		}
		Block block = location.getWorld().getBlockAt(location);
		if(block != null && block.getType() == Material.CHEST) {
			this.chest = (Chest) block.getState();;
		}
	}
	
	public enum ChestType{
		NORMAL, MINI_FEAST, FEAST;
	}

}
