package br.alkazuz.minigame.scoreboard;

import java.util.*;
import org.bukkit.entity.*;
import br.alkazuz.minigame.game.*;
import br.alkazuz.minigame.data.*;
import org.bukkit.scoreboard.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class ScoreBoard implements Listener
{
    public static WeakHashMap<Player, ScoreBoardAPI> boards;
    
    static {
        ScoreBoard.boards = new WeakHashMap<Player, ScoreBoardAPI>();
    }
    
    public static void updateScoreBoardLobby(Player player, Round round, PlayerData data) {
        ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
        scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 8);
        scoreBoardAPI.update("§f" + data.partidas, 5);
        scoreBoardAPI.update("§f" + data.winTotal, 4);
        scoreBoardAPI.update("§f" + data.winTotal, 2);
    }
    
    public static void updateScoreBoard(Player player, Round round, PlayerData data) {
        ScoreBoardAPI scoreBoardAPI = ScoreBoard.boards.get(player);
        scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 7);
        scoreBoardAPI.update("§f" + ((round.first == null) ? "§7Ningu\u00e9m" : round.first), 4);
        scoreBoardAPI.update("§f" + ((round.second == null) ? "§7Ningu\u00e9m" : round.second), 3);
        scoreBoardAPI.update("§f" + ((round.three == null) ? "§7Ningu\u00e9m" : round.three), 2);
    }
    
    public static void createScoreBoard(Player player, Round rouns) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lDRAGON ESCAPE", new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 8);
        scoreBoardAPI.add("§fRank: ", 7);
        scoreBoardAPI.add("§d", 6);
        scoreBoardAPI.add("§fQuem venceu: ", 5);
        scoreBoardAPI.add(" §e1§e§m§e° ", 4);
        scoreBoardAPI.add(" §e2§e§m§e° ", 3);
        scoreBoardAPI.add(" §e3§e§m§e° ", 2);
        scoreBoardAPI.add("§a", 1);
        scoreBoardAPI.add("§6  minigames.zeusmc.net", 0);
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
    }
    
    public static void createScoreBoardLobby(Player player, Round rouns, PlayerData data) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lDRAGON ESCAPE", new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 9);
        scoreBoardAPI.add("§fRank: ", 8);
        scoreBoardAPI.add("§d", 7);
        scoreBoardAPI.add("§eSuas Informa\u00e7\u00f5es: ", 6);
        scoreBoardAPI.add(" §a\u2022 Partidas: ", 5);
        scoreBoardAPI.add(" §a\u2022 Vit\u00f3rias: ", 4);
        scoreBoardAPI.add("§3", 3);
        scoreBoardAPI.add("§6  minigames.zeusmc.net", 0);
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
