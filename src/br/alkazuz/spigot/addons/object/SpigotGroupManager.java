package br.alkazuz.spigot.addons.object;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class SpigotGroupManager {
	
	public static HashMap<String, SpigotGroup> groups = new HashMap<String, SpigotGroup>();
	public static SpigotGroup defaultGroup;
	
	public static SpigotGroup getPlayerGroup(Player player) {
		for(String perm : groups.keySet()) {
			if(player.hasPermission(perm)) return groups.get(perm);
		}
		return defaultGroup;
	}
	
}
