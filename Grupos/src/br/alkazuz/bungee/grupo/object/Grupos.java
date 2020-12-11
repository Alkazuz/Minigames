package br.alkazuz.bungee.grupo.object;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.alkazuz.bungee.addons.object.BungeeGroup;
import br.alkazuz.bungee.addons.object.BungeeGroupManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Grupos
{
    public static Map<String, Grupo> data = Collections.synchronizedMap( new HashMap<String, Grupo>());
	public static Map<Grupo, List<String>> invites = Collections.synchronizedMap( new HashMap<Grupo, List<String>>());
    
    public static Grupo getGroupPlayer(String player) {
    	if(data.containsKey(player))return data.get(player);
    	for(Grupo grupos : data.values()) {
    		if(grupos.members.contains(player)) {
    			return grupos;
    		}
    	}
    	return null;
    }
    
    public static Grupo getInvite(String name, String owner) {
    	if(data.containsKey(name)) return null;
    	for(Grupo g : invites.keySet()) {
    		if(g.leader.equalsIgnoreCase(owner)) {
    			if(invites.get(g).contains(name.toLowerCase())) {
    				return g;
    			}
    		}
    	}
    	return null;
    }

	public static Grupo getGrupo(ProxiedPlayer player) {
		return getGroupPlayer(player.getName());
	}

	public static void create(ProxiedPlayer player) {
		Grupo grupo = new Grupo();
		grupo.leader = player.getName();
		data.put(player.getName(), grupo);
		grupo.members.add(player.getName());
	}

	public static String getDisplayName(ProxiedPlayer player) {
		BungeeGroup group = BungeeGroupManager.getPlayerGroup(player);
		return group.getPrefix() + player.getName() + group.getSuffix();
	}
    
}
