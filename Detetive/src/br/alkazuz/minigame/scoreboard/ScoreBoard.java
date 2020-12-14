package br.alkazuz.minigame.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.game.DetetivePlayer;
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
        scoreBoardAPI.update("§b" + data.getRank() + "° Lugar", 6);
        scoreBoardAPI.update("§f" + data.partidas, 3);
        scoreBoardAPI.update("§f" + data.winTotal, 2);
    }
    
    public static void createScoreBoardLobby(Player player,Round rouns,PlayerData data) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
       ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§e§lDETETIVE", new RandomUUID().getUUID());
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
    
    public static void createScoreBoard(Player player,Round detetive) {
       List<DetetivePlayer> keys = new ArrayList<DetetivePlayer>(detetive.votes.keySet());
        Collections.sort(keys, new Comparator<DetetivePlayer>() {
            @Override
            public int compare(DetetivePlayer c1,DetetivePlayer c2) {
            	Double o1 = (double)detetive.votes.get(c1);
               	Double o2 = (double)detetive.votes.get(c2);
                return o2.compareTo(o1);
            }
        });
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
       ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI("§6§lVOTA\u00c7AO", new RandomUUID().getUUID());
        for (DetetivePlayer dp : keys) {
            scoreBoardAPI.add("\u2022 "+dp.nick, detetive.votes.get(dp));
        }
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
    }
}
