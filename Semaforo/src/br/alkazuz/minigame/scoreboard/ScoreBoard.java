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
    }
    
    public static void createScoreBoardLobby(Player player, Round rouns, PlayerData data) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lSEMAFORO", new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 7);
        scoreBoardAPI.add("§fRank: ", 6);
        scoreBoardAPI.add("§d", 5);
        scoreBoardAPI.add("§eSuas Informações: ", 4);
        scoreBoardAPI.add(" §a\u2022 Partidas: ", 3);
        scoreBoardAPI.add(" §a\u2022 Vitórias: ", 2);
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
}
