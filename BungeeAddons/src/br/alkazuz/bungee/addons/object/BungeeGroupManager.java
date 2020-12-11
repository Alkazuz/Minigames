package br.alkazuz.bungee.addons.object;

import java.util.HashMap;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeGroupManager {
	
	public static HashMap<String, BungeeGroup> groups = new HashMap<String, BungeeGroup>();
	public static BungeeGroup defaultGroup;
	
	public static BungeeGroup getPlayerGroup(ProxiedPlayer player) {
		for(String perm : groups.keySet()) {
			if(player.hasPermission(perm)) return groups.get(perm);
		}
		return defaultGroup;
	}
	
}
