package br.alkazuz.lobby.scoreboard;

import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.lobby.api.PingServer;
import br.alkazuz.lobby.api.QueueAPI;
import br.alkazuz.lobby.api.RankingMinigamesAPI;
import br.alkazuz.lobby.object.PlayerData;
import br.alkazuz.lobby.object.Servidor;
import br.alkazuz.lobby.object.Servidores;

public class ScoreBoard implements Listener
{
    public static WeakHashMap<Player, ScoreBoardAPI> boards;
    public boolean mcMMO;
    
    static {
        ScoreBoard.boards = new WeakHashMap<Player, ScoreBoardAPI>();
    }
    
    public ScoreBoard() {
        this.mcMMO = false;
    }
    
    public static void updateScoreboardMinigame(Player player, Servidor servidor) {
    	ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
    	 int playing = 0;
         if(QueueAPI.status.containsKey(servidor)) {
         	PingServer ping = QueueAPI.status.get(servidor);
         	if(ping.isOnline()) {
         		playing = ping.onlinePlayers;
         	}
         }
         scoreBoardAPI.update("§f"+ playing, 9);
    }
    
    public static void createScoreBoardMinigames(Player player, Servidor servidor) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§6§l"+servidor.flatName, new RandomUUID().getUUID());
       
        String top1 = "§7---";
        String top2 = "§7---";
        String top3 = "§7---";
        int pos = 0;
        if(RankingMinigamesAPI.dataSeverInfo.containsKey(servidor)) {
        	List<PlayerData> data = RankingMinigamesAPI.dataSeverInfo.get(servidor);
        	if(data.size() > 0) {
        		PlayerData info = null;
        		for(PlayerData all : data) {
        			if(all.nick.replace("\"", "").equalsIgnoreCase(player.getName())) {
        				info = all;
        				break;
        			}
        		}
            	if(info != null) {
            		pos = data.indexOf(info);
            	}
        	}
        	if(data.size() > 0) {
        		top1 = data.get(0).nick.replace("\"", "");
        		if(top1.length() >= 10) {
        			top1 = top1.substring(0, 10) + "...";
        		}
        	}
        	if(data.size() > 1) {
        		top2 = data.get(1).nick.replace("\"", "");
        		if(top2.length() >= 10) {
        			top2 = top2.substring(0, 10) + "...";
        		}
        	}
        	if(data.size() > 2) {
        		top3 = data.get(2).nick.replace("\"", "");
        		if(top3.length() >= 10) {
        			top3 = top3.substring(0, 10) + "...";
        		}
        	}
        }
        
        scoreBoardAPI.add("§d", 10);
        scoreBoardAPI.add("§eJ. agora: ", 9);
        scoreBoardAPI.add("§c", 8);
        scoreBoardAPI.add("§eRanking:", 7);
        
        scoreBoardAPI.add(" §b1° ", 6);
        scoreBoardAPI.add(" §b2° ", 5);
        scoreBoardAPI.add(" §b3° ", 4);
        scoreBoardAPI.add(" §bVocê: ", 3);
       
        scoreBoardAPI.add("§b", 2);
        scoreBoardAPI.add("§6 jogar.servidor.com", 1);
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
        
        scoreBoardAPI.update("§7"+top1, 6);
        scoreBoardAPI.update("§7"+top2, 5);
        scoreBoardAPI.update("§7"+top3, 4);
        scoreBoardAPI.update("§f"+pos+"° pos.", 3);
    }
    
    public static void createScoreBoard(Player player) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§6§l+ JOGADOS HOJE", new RandomUUID().getUUID());
        int index = 0;
        for (Servidor servidor : Servidores.servidores) {
            int count = servidor.played.get();
            if (index >= 10) {
                break;
            }
            scoreBoardAPI.add(servidor.flatName, count);
            ++index;
        }
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
    }
    
    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        if (ScoreBoard.boards.containsKey(event.getPlayer())) {
            ScoreBoard.boards.remove(event.getPlayer());
        }
    }
}
