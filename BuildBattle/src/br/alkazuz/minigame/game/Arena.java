package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Arena {

	public Player owner;
	public Location spawn;
	public int points = 0;
	public List<Location> floor = new ArrayList<Location>();
	public Block floorBlock;
	public boolean voted = false;
	
	public Arena(Player owner, Location spawn) {
		this.owner = owner;
		this.spawn = spawn;
	}
	
	public Location randomLocationSpawn() {
		return spawn.clone().add(0, 25, 0);
	}
	
	public void load() {
		for(int y = 0;y > -2; y--) {
			Block block = spawn.getWorld().getBlockAt(spawn.clone().add(0, y, 0));
			if(block != null && block.getType() == Material.STAINED_CLAY && block.getData() == 0) {
				
				for(int x = -50; x < 50; x++) {
					for(int z = -50; z < 50; z++) {
						block = spawn.getWorld().getBlockAt(spawn.clone().add(x, y, z));
						if(block != null && block.getType() == Material.STAINED_CLAY && block.getData() == 0) {
							floor.add(block.getLocation());
						}
					}
				}
				break;
			}
		}
		setFloorBlock(Material.GRASS, (byte) 0);
	}
	
	public boolean canInteractLocation(Location loc) {
		if(inBuildArea(loc) || floor.contains(loc)) return true;
		return false;
	}
	
	public boolean canInteractBlock(Block block) {
		Location loc = block.getLocation();
		return canInteractLocation(loc);
	}
	
	public boolean inBuildArea(Location loc) {
		if(loc.getWorld() != spawn.getWorld()) return false;
		if(loc.getBlockY() >= 55) return false;
		if(loc.getBlockY() < floorBlock.getLocation().getBlockY()) return false;
		for(Location location : floor) {
			if(location.getBlockX() == loc.getBlockX() && location.getBlockZ() == loc.getBlockZ()) return true;
		}
		return false;
	}
	
	public void setFloorBlock(Material material, byte data) {
		for(Location location : floor) {
			location.getWorld().getBlockAt(location).setType(material);
			location.getWorld().getBlockAt(location).setData(data);
			floorBlock = location.getWorld().getBlockAt(location);
		}
	}
	
}
