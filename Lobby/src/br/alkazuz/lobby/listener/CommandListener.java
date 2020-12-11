package br.alkazuz.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import br.alkazuz.lobby.api.QueueAPI;
import br.alkazuz.lobby.object.Servidor;
import br.alkazuz.lobby.object.Servidores;
import br.alkazuz.lobby.scoreboard.ScoreBoard;

public class CommandListener implements Listener
{
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().replace("/", "");
        if (cmd.contains(" ")) {
            cmd = cmd.split(" ")[0];
        }
        
        Player p = event.getPlayer();
        if(cmd.equalsIgnoreCase("lobby") || cmd.equalsIgnoreCase("spawn")|| cmd.equalsIgnoreCase("home")) {
        	World world = Bukkit.getWorld("world");
        	if(world != null) {
        		if(QueueAPI.lobby.containsKey(p.getName())) {
        			QueueAPI.lobby.remove(p.getName());
        			
        		}
        		p.teleport(world.getSpawnLocation());
        		 event.setCancelled(true);
        		 ScoreBoard.createScoreBoard(p);
        		 for(Player all : Bukkit.getOnlinePlayers()) {
        			 if(!QueueAPI.lobby.containsKey(all.getName())) {
        				 all.showPlayer(p);
        				 p.showPlayer(all);
        			 }else {
        				 all.hidePlayer(p);
        				 p.hidePlayer(all);
        			 }
        		 }
        		 return;
        	}
        }
        for (Servidor servidor : Servidores.servidores) {
            if (cmd.equalsIgnoreCase(servidor.name)) {
                event.setCancelled(true);
                if (servidor.lobby == null) {
                    continue;
                }
                event.getPlayer().teleport(servidor.lobby);
                if (QueueAPI.queue.containsKey(p)) {
                    QueueAPI.queue.remove(p);
                }
                QueueAPI.lobby.put(p.getName(), servidor);
                for(Player all : Bukkit.getOnlinePlayers()) {
                	if(QueueAPI.lobby.containsKey(all.getName()) && QueueAPI.lobby.get(all.getName()) == servidor) {
                		p.showPlayer(all);
                		all.showPlayer(p);
                	}else {
                		p.hidePlayer(all);
                		all.hidePlayer(p);
                	}
                }
                event.getPlayer().sendMessage("§3» §bTeleportando para o lobby do §6" + servidor.flatName + "§b.");
                ScoreBoard.createScoreBoardMinigames(p, servidor);
                //ScoreBoard.updateScoreboardMinigame(p, servidor);
            }
        }
    }
}
