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

public class ScoreBoard implements Listener
{
    public static WeakHashMap<Player, ScoreBoardAPI> boards;
    
    static {
        ScoreBoard.boards = new WeakHashMap<Player, ScoreBoardAPI>();
    }
    
    public static void updateScoreBoardLobby(Player player,Round round,PlayerData data) {
       ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
        scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 6);
        scoreBoardAPI.update("§f" + data.partidas, 3);
        scoreBoardAPI.update("§f" + data.winTotal, 2);
    }
    
    public static void createScoreBoardLobby(Player player,Round rouns,PlayerData data) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§l"+MinigameConfig.NAME.toUpperCase(), new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 7);
        scoreBoardAPI.add("§fRank: ", 6);
        scoreBoardAPI.add("§d", 5);
        scoreBoardAPI.add("§eSuas Informa\u00e7\u00f5es: ", 4);
        scoreBoardAPI.add(" §a\u2022 Partidas: ", 3);
        scoreBoardAPI.add(" §a\u2022 Vit\u00f3rias: ", 2);
        scoreBoardAPI.add("§3", 1);
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
    
    public static void updateScoreBoard(Player player,Round round,PlayerData data) {
        ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
         scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 6);
         scoreBoardAPI.update(round.theme == null ? "---" : round.theme, 3);
         scoreBoardAPI.update("§f" + round.players.size(), 2);
     }
    
    public static void createScoreBoard(Player player,Round detetive) {
    	ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§l"+MinigameConfig.NAME.toUpperCase(), new RandomUUID().getUUID());
    	scoreBoardAPI.add("§e", 7);
        scoreBoardAPI.add("§fRank: ", 6);
        scoreBoardAPI.add("§d", 5);
        scoreBoardAPI.add("§eInfo. da Partida:", 4);
        scoreBoardAPI.add(" §aTema: §f", 3);
        scoreBoardAPI.add(" §aPlayers: §f", 2);
        scoreBoardAPI.add("§b", 1);
        scoreBoardAPI.add("  §6jogar.zeusmc.net", 0);
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
    }
}
