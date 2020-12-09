package br.alkazuz.bungee.addons.listener;

import br.alkazuz.bungee.addons.main.BungeeConfig;
import br.alkazuz.bungee.addons.main.Config;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener
{
	
	@EventHandler
	public void onJoin(ServerConnectEvent event) {
		if(BungeeConfig.MAINTENANCE && !event.getPlayer().hasPermission("maintenance.whitelist") && !BungeeConfig.MAINTENANCE_USERS.contains(event.getPlayer().getName().toLowerCase())) {
			event.getPlayer().disconnect(BungeeConfig.MAINTENANCE_ERROR);
			event.setCancelled(true);
		}
	}
	
    @EventHandler
    public void onCommand(ChatEvent event) {
    	ProxiedPlayer player = (ProxiedPlayer) event.getSender();
    	if(event.getMessage().startsWith("/")) {
    		String cmd = event.getMessage().split("/")[1];
    		if(cmd.contains(" ")) {
    			cmd = cmd.split(" ")[0];
    		}
    		cmd = cmd.toLowerCase();
    		if(Config.BLOCKED_CMDS.contains(cmd) && !player.hasPermission("bungee.command." + cmd)) {
    			player.sendMessage("§cComando inexistente ou você não tem acesso.");
    			event.setCancelled(true);
    		}
    		
    	}
    }
}
