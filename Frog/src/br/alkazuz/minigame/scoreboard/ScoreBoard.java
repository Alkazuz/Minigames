package br.alkazuz.minigame.scoreboard;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.game.MinigameConfig;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.game.RoundState;

public class ScoreBoard implements Listener
{
    public static WeakHashMap<Player, ScoreBoardAPI> boards;
    
    static {
        ScoreBoard.boards = new WeakHashMap<Player, ScoreBoardAPI>();
    }
    
    public static void updateScoreBoardLobby(Player player,Round round,PlayerData data) {
    	ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
        scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 8);
        scoreBoardAPI.update("§f" + data.partidas, 5);
        scoreBoardAPI.update("§f" + data.winTotal, 4);
        scoreBoardAPI.update("§f" + data.winTotal, 2);
    }
    
    public static void createScoreBoardLobby(Player player,Round rouns,PlayerData data) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
       ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lFROG", new RandomUUID().getUUID());
       scoreBoardAPI.add("§e", 9);
       scoreBoardAPI.add("§fRank: ", 8);
       scoreBoardAPI.add("§d", 7);
       scoreBoardAPI.add("§eSuas Informa\u00e7\u00f5es: ", 6);
       scoreBoardAPI.add(" §a\u2022 Partidas: ", 5);
       scoreBoardAPI.add(" §a\u2022 Vit\u00f3rias: ", 4);
       scoreBoardAPI.add("§3", 3);
       scoreBoardAPI.add("  §6jogar.zeusmc.net", 0);
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
