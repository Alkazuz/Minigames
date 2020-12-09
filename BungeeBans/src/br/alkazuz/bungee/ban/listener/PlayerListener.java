package br.alkazuz.bungee.ban.listener;

import br.alkazuz.bungee.ban.main.BungeeConfig;
import br.alkazuz.bungee.ban.manager.Ban;
import br.alkazuz.bungee.ban.manager.Ban.BanType;
import br.alkazuz.bungee.ban.manager.Bans;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener
{
	
	@EventHandler
	public void onJoin(LoginEvent event) {
		String player =  event.getConnection().getName().toLowerCase();
		if(!Bans.bans.containsKey(player)) return;
    	Ban ban = Bans.bans.get(player);
    	if(ban.type != BanType.BAN) return;
    	
    	if(ban.desban != -1L && System.currentTimeMillis() > ban.desban) {
    		ban.unban();
    		Bans.bans.remove(player);
    		return;
    	}
    	
    	StringBuilder sb = new StringBuilder();
		if(ban.desban == -1L) {
			for(String a : BungeeConfig.BAN) {
				sb.append(a.replace("{0}", ban.applied())
						.replace("{1}", ban.title)
						.replace("{2}", ban.proof)
						.replace("{3}", ban.author));
				sb.append("\n");
			}
		}else {
			for(String a : BungeeConfig.MUTE_TEMP) {
				sb.append(a.replace("{0}", ban.applied())
						.replace("{1}", ban.title)
						.replace("{2}", ban.proof)
						.replace("{3}", ban.author)
						.replace("{4}", ban.debanTime().trim()));
				sb.append("\n");
			}
		}
    	event.setCancelReason(sb.toString());
    	event.setCancelled(true);
	}
	
    @EventHandler
    public void onCommand(ChatEvent event) {
    	if(!(event.getSender() instanceof ProxiedPlayer)) return;
    	ProxiedPlayer player = (ProxiedPlayer) event.getSender();
    	if(!Bans.bans.containsKey(player.getName().toLowerCase())) return;
    	Ban ban = Bans.bans.get(player.getName().toLowerCase());
    	if(ban.type != BanType.MUTE) return;
    	if(ban.desban != -1L && System.currentTimeMillis() > ban.desban) {
    		ban.unban();
    		Bans.bans.remove(player.getName().toLowerCase());
    		return;
    	}
    	if(event.getMessage().startsWith("/")) {
    		String cmd = event.getMessage().split("/")[1];
    		if(cmd.contains(" ")) {
    			cmd = cmd.split(" ")[0];
    		}
    		cmd = cmd.toLowerCase();
    		if(BungeeConfig.MUTE_CMDS.contains(cmd)) {
    			event.setCancelled(true);
    			StringBuilder sb = new StringBuilder();
    			if(ban.desban == -1L ) {
        			for(String a : BungeeConfig.BAN) {
        				sb.append(a.replace("{0}", ban.applied())
        						.replace("{1}", ban.title)
        						.replace("{2}", ban.proof)
        						.replace("{3}", ban.author));
        				sb.append("\n");
        			}
        		}else {
        			for(String a : BungeeConfig.MUTE_TEMP) {
        				sb.append(a.replace("{0}", ban.title)
        						.replace("{1}", ban.applied())
        						.replace("{2}", ban.proof)
        						.replace("{3}", ban.author)
        						.replace("{4}", ban.debanTime().trim()));
        				sb.append("\n");
        			}
        		}
    			
    			player.sendMessage(sb.toString());
    		}
    	}else {
    		event.setCancelled(true);
			StringBuilder sb = new StringBuilder();
			if(ban.desban == -1L ) {
    			for(String a : BungeeConfig.BAN) {
    				sb.append(a.replace("{0}", ban.applied())
    						.replace("{1}", ban.title)
    						.replace("{2}", ban.proof)
    						.replace("{3}", ban.author));
    				sb.append("\n");
    			}
    		}else {
    			for(String a : BungeeConfig.MUTE_TEMP) {
    				sb.append(a.replace("{0}", ban.applied())
    						.replace("{1}", ban.title)
    						.replace("{2}", ban.proof)
    						.replace("{3}", ban.author)
    						.replace("{4}", ban.debanTime().trim()));
    				sb.append("\n");
    			}
    		}
			
			player.sendMessage(sb.toString());
    	}
    	
    }
}
