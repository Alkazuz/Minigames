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
        scoreBoardAPI.update("§b" + data.getRank() + "§ Lugar", 8);
        scoreBoardAPI.update("§f" + data.partidas, 5);
        scoreBoardAPI.update("§f" + data.winTotal, 4);
        if(round.state == RoundState.AVAILABLE) {
        	if (round.counter.starting) {
        		scoreBoardAPI.update("§fAguardando...", 2);
        	}else {
        		scoreBoardAPI.update("§e"+round.counter.timer+"s", 2);
        	}
        }
        scoreBoardAPI.update("§f" + data.winTotal, 2);
    }
    
    public static void createScoreBoard(Player player, Round rouns) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lSEMAFORO", new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 8);
        scoreBoardAPI.add("§fRank: ", 7);
        scoreBoardAPI.add("§d", 6);
        scoreBoardAPI.add("§a", 1);
        scoreBoardAPI.add("§6zeusmc.net", 0);
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
    }
    
    public static void createScoreBoardLobby(Player player, Round rouns, PlayerData data) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lSEMAFORO", new RandomUUID().getUUID());
        scoreBoardAPI.add("§e", 9);
        scoreBoardAPI.add("§fRank: ", 8);
        scoreBoardAPI.add("§d", 7);
        scoreBoardAPI.add("§eSuas Informações: ", 6);
        scoreBoardAPI.add(" §a\u2022 Partidas: ", 5);
        scoreBoardAPI.add(" §a\u2022 Vitórias: ", 4);
        scoreBoardAPI.add("§3", 3);
        scoreBoardAPI.add(" §fComeça em: ", 2);
        scoreBoardAPI.add("§a", 1);
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
