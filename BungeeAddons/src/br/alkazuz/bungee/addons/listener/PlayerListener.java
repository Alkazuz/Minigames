package br.alkazuz.bungee.addons.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.alkazuz.bungee.addons.command.CommandBungeeTP;
import br.alkazuz.bungee.addons.main.BungeeConfig;
import br.alkazuz.bungee.addons.main.Config;
import br.alkazuz.bungee.addons.main.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
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
			return;
		}
		if(event.getTarget().getName().equalsIgnoreCase("lobby") && CommandBungeeTP.vanishList.contains(event.getPlayer().getName())) {
			CommandBungeeTP.vanishList.remove(event.getPlayer().getName());
			for(ServerInfo target : BungeeCord.getInstance().getServers().values()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
		    	//out.writeUTF( "BungeeCord" ); 
		        try {
		        	out.writeUTF( Main.channel ); 
		            out.writeUTF( event.getPlayer().getName() ); 
		            out.writeUTF( "---" ); 
		            out.writeBoolean(false);
		        }catch (Exception e) {
					e.printStackTrace();
				}
		        //target.getServer().sendData("Vanish", bb.toByteArray());
		        target.sendData("Vanish", out.toByteArray());
			}
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
    			player.sendMessage("�cComando inexistente ou voc� n�o tem acesso.");
    			event.setCancelled(true);
    		}
    		
    	}
    }
}
