package br.alkazuz.minigame.scoreboard;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.game.EscPlayer;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.utils.Methods;

public class ScoreBoard implements Listener
{
    public static WeakHashMap<Player, ScoreBoardAPI> boards;
    
    static {
        ScoreBoard.boards = new WeakHashMap<Player, ScoreBoardAPI>();
    }
    
    public static void update(Player player) {
    	ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
    	scoreBoardAPI.updateplayer();
    }
    
    public static void startHiden(Player player,Round round){
    	ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
    	scoreBoardAPI.hideNameTag();
    }
    
    public static void updateScoreBoardLobby(Player player,Round round,PlayerData data) {
       ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
        scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 6);
        scoreBoardAPI.update("§f" + data.partidas, 3);
        scoreBoardAPI.update("§f" + data.winTotal, 2);
    }
    
    public static void createScoreBoardLobby(EscPlayer player,Round rouns,PlayerData data) {
        player.player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
       ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI(player, rouns, "§e§lESCONDE-ESCONDE", new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 7);
        scoreBoardAPI.add("§fRank: ", 6);
        scoreBoardAPI.add("§d", 5);
        scoreBoardAPI.add("§eSuas Informa\u00e7\u00f5es: ", 4);
        scoreBoardAPI.add(" §a\u2022 Partidas: ", 3);
        scoreBoardAPI.add(" §a\u2022 Vit\u00f3rias: ", 2);
        scoreBoardAPI.add("§a", 1);
        scoreBoardAPI.add("  §6jogar.zeusmc.net", 0);
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player.player);
        scoreBoardAPI.send(player.player);
        ScoreBoard.boards.put(player.player, scoreBoardAPI);
    }
    
    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        if (ScoreBoard.boards.containsKey(event.getPlayer())) {
            ScoreBoard.boards.remove(event.getPlayer());
        }
    }
    
    public static void updateScoreBoard(Player player,Round round) {
        ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
         scoreBoardAPI.update("§f " + round.getSeekCount(), 5);
         scoreBoardAPI.update("§f " + round.getHiddenCount(), 4);
         scoreBoardAPI.update("§a" + Methods.getRemainingTime2("", round.counter.timer), 2);
     }
    
    public static void createScoreBoard(EscPlayer player,Round detetive) {
        player.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI(player, detetive, "§e§lESCONDE-ESCONDE", new RandomUUID().getUUID());
        
        scoreBoardAPI.add("§e", 7);
        scoreBoardAPI.add("§fStatus", 6);
        scoreBoardAPI.add("§3 \u2022 Procurando:", 5);
        scoreBoardAPI.add("§6 \u2022 Escondidos:", 4);
        scoreBoardAPI.add("§d", 3);
        scoreBoardAPI.add("§eAcaba em: ", 2);
        scoreBoardAPI.add("§a", 1);
        scoreBoardAPI.add("  §6jogar.zeusmc.net", 0);
        
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player.player);
        scoreBoardAPI.send(player.player);
        ScoreBoard.boards.put(player.player, scoreBoardAPI);
    }
}
