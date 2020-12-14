package br.alkazuz.arena.scoreboard;

import org.bukkit.entity.*;
import br.alkazuz.arena.main.*;
import org.bukkit.scoreboard.*;
import br.alkazuz.arena.game.*;
import org.bukkit.configuration.file.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

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
    
    public static void createScoreBoard(final Player player, final Arena detetive) {
        final FileConfiguration config = Main.theInstance().config;
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        final ScoreBoardAPI scoreBoardAPI = new ScoreBoardAPI(player, config.getString("configuration.scoreboard.displayname").replace("&", "§"), new RandomUUID().getUUID());
        for (final ArenaPlayer dp : detetive.players.values()) {
            if (dp.kills.get() <= 0) {
                continue;
            }
            scoreBoardAPI.add(dp.nick, dp.kills.get());
        }
        scoreBoardAPI.build();
        ScoreBoard.boards.remove(player);
        scoreBoardAPI.send(player);
        ScoreBoard.boards.put(player, scoreBoardAPI);
    }
    
    @EventHandler
    public void onExit(final PlayerQuitEvent event) {
        if (ScoreBoard.boards.containsKey(event.getPlayer())) {
            ScoreBoard.boards.remove(event.getPlayer());
        }
    }
}
