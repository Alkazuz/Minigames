package br.alkazuz.bungee.grupo.object;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Grupo
{
    public String leader;
    public List<String> members = new ArrayList<String>();
    public int maxMember = 3;
    
    public Grupo() {
    	Grupos.invites.put(this, new ArrayList<String>());
    }
    
    public void broadcast(final String message) {
        for (final String d : this.members) {
            final ProxiedPlayer all = ProxyServer.getInstance().getPlayer(d);
            if (all != null) {
                all.sendMessage(message);
            }
        }
    }
    
    public String getMemberName(String name) {
    	for(String n : members) {
    		if (n.equalsIgnoreCase(name)) return n;
    	}
    	return null;
    }
    
    public void sendMessage(final ProxiedPlayer player, final String message) {
        String format = "§e[Grupo] %s §f%s§3: §b%s";
        if (player.hasPermission("minigames.staff")) {
            format = String.format(format, "§4[STAFF]", player.getName(), message);
        }
        else if (player.hasPermission("minigames.vip")) {
            format = String.format(format, "§6[VIP]", player.getName(), message);
        }
        else {
            format = String.format(format, "", player.getName(), message);
        }
        for (final String d : this.members) {
            final ProxiedPlayer all = ProxyServer.getInstance().getPlayer(d);
            if (all != null) {
                all.sendMessage(format);
            }
        }
    }
    
    public void sendAll(final String server, final String flatName) {
        final ServerInfo target = ProxyServer.getInstance().getServerInfo(server);
        if (target != null) {
            if (server.contains("lobby")) {
                return;
            }
            for (final String d : this.members) {
                final ProxiedPlayer all = ProxyServer.getInstance().getPlayer(d);
                if(all.getName().equals(leader)) continue;
                if (all != null && all.getServer().getInfo().getName().contains("lobby")) {
                    all.connect(target);
                    all.sendMessage("§a" + this.leader + " te levou para uma partida.");
                }
            }
        }
    }
}
