package br.alkazuz.arena.scoreboard;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import br.alkazuz.arena.game.Arena;
import br.alkazuz.arena.game.GameManager;

public class ScoreBoardAPI
{
    private Scoreboard scoreboard;
    private String title;
    private String objName;
    private Map<String, Integer> scores;
    private HashMap<Integer, Team> teams;
    private Objective obj;
    int nteams;
    int nnn;
    
    private Objective nameHealthObj;
    private Objective scoreObjective;
    private Objective tabObjective;
    private Team gameEnemy;
    private Team gameTeams;
    
    private Player player;
    
    public ScoreBoardAPI(Player player, final String title, final String objName) {
        this.objName = null;
        this.obj = null;
        this.nteams = 50;
        this.nnn = 100;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = title;
        this.objName = objName;
        this.scores = Maps.newLinkedHashMap();
        this.teams = Maps.newLinkedHashMap();
        this.player = player;
    }
    
    public void blankLine() {
        this.add("§c ");
    }
    
    public void blankLine(final int n) {
        this.add("§c ", n);
    }
    
    public HashMap<Integer, Team> getTeams() {
        return this.teams;
    }
    
    public void add(final String s) {
        this.add(s, null);
    }
    
    public void add(String s, final Integer n) {
        Preconditions.checkArgument(s.length() < 48, (Object)"text cannot be over 48 characters in length");
        s = this.fixDuplicates(s);
        s = ChatColor.translateAlternateColorCodes('&', s);
        this.scores.put(s, n);
    }
    
    private String fixDuplicates(String string) {
        while (this.scores.containsKey(string)) {
            string = String.valueOf(String.valueOf(string)) + "§r";
        }
        return string;
    }
    
    private Map.Entry<Team, String> createTeam(final String s) {
        final Team registerNewTeam = this.scoreboard.registerNewTeam(new StringBuilder().append(this.nteams--).toString());
        if (s.length() <= 16) {
            return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, s);
        }
        final Iterator<String> iterator = Splitter.fixedLength(16).split((CharSequence)s).iterator();
        registerNewTeam.setPrefix((String)iterator.next());
        return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, iterator.next());
    }
    
    public void updateEnemy() {
    	Arena round = GameManager.searchMatch();
    	for(String a : round.players.keySet()) {
    		Player p = Bukkit.getPlayer(a);
    		if(p == null) continue;
    		tabObjective.getScore(p.getName()).setScore(round.players.get(a).kills.get());
    		if(p == player)continue;
    		if(gameEnemy.hasEntry(p.getName())) continue;
    		
    		gameEnemy.addEntry(p.getName());
    	}
    }
    
    public void build() {
        (this.obj = this.scoreboard.registerNewObjective(this.objName, "dummy")).setDisplayName(ChatColor.translateAlternateColorCodes('&', this.title));
        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        (this.nameHealthObj = this.scoreboard.registerNewObjective("namelifee", "health")).setDisplaySlot(DisplaySlot.BELOW_NAME);
        this.nameHealthObj.setDisplayName(ChatColor.RED + "\u2764");
        
        (this.tabObjective = this.scoreboard.registerNewObjective("PlayerGaming", "dummy")).setDisplaySlot(DisplaySlot.PLAYER_LIST);
        
        gameEnemy = this.scoreboard.registerNewTeam("2-PlayerGaming");
        Arena round = GameManager.searchMatch();
        if(round != null) {
        	gameEnemy.setPrefix("§c");
        	for(String a : round.players.keySet()) {
        		Player p = Bukkit.getPlayer(a);
        		if(p == null) continue;
        		if(p == player)continue;
        		tabObjective.getScore(p.getName()).setScore(round.players.get(a).kills.get());
        		gameEnemy.addEntry(p.getName());
        	}
        }
        
        gameTeams = this.scoreboard.registerNewTeam("1-TeamsFriends");
        this.gameTeams.setPrefix("§a");
        gameTeams.addPlayer(player);
        
        int n = this.scores.size() - 2 + 1;
        for (final Map.Entry<String, Integer> entry : this.scores.entrySet()) {
            final Map.Entry<Team, String> team = this.createTeam(entry.getKey());
            final int score = (entry.getValue() != null) ? entry.getValue() : n;
            final OfflinePlayerv2 offlinePlayerv2 = new OfflinePlayerv2(team.getValue());
            team.getKey().addPlayer((OfflinePlayer)offlinePlayerv2);
            this.obj.getScore((OfflinePlayer)offlinePlayerv2).setScore(score);
            this.teams.put(score, team.getKey());
            --n;
        }
    }
    
    public void setDisplayName(final String displayName) {
        this.obj.setDisplayName(displayName);
    }
    
    public void reset() {
        this.title = null;
        this.scores.clear();
        final Iterator<Team> iterator = this.teams.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().unregister();
        }
        this.teams.clear();
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public void send(final Player... array) {
        try {
            for (int length = array.length, i = 0; i < length; ++i) {
                array[i].setScoreboard(this.scoreboard);
            }
        }
        catch (NullPointerException ex) {}
    }
    
    public void update(String suffix, final int score) {
        if (this.teams.containsKey(score)) {
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            final Team team = this.teams.get(score);
            if (suffix.length() <= 16) {
                team.setPrefix("");
                team.setSuffix(suffix);
                return;
            }
            Map.Entry<Team, String> entry = this.makeTeam(suffix);
            if (score == 3) {
                entry = this.makeTeam("  §fOnline: §a" + Bukkit.getServer().getOnlinePlayers().size());
            }
            final OfflinePlayerv2 offlinePlayerv2 = new OfflinePlayerv2(entry.getValue());
            entry.getKey().addPlayer((OfflinePlayer)offlinePlayerv2);
            this.resetScore(score);
            this.obj.getScore((OfflinePlayer)offlinePlayerv2).setScore(score);
        }
        else {
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            this.add(suffix, score);
        }
    }
    
    public void resetScore(final int n) {
        for (final String s : this.obj.getScoreboard().getEntries()) {
            if (this.obj.getScore(s).getScore() == n) {
                this.getScoreboard().resetScores(s);
            }
        }
    }
    
    private Map.Entry<Team, String> makeTeam(final String s) {
        final Team registerNewTeam = this.scoreboard.registerNewTeam(new StringBuilder().append(this.nnn++).toString());
        if (s.length() <= 16) {
            return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, s);
        }
        final Iterator<String> iterator = Splitter.fixedLength(16).split((CharSequence)s).iterator();
        registerNewTeam.setPrefix((String)iterator.next());
        return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, iterator.next());
    }
    
    public class OfflinePlayerv2 implements OfflinePlayer
    {
        private String playerName;
        
        public OfflinePlayerv2(final String playerName) {
            this.playerName = playerName;
        }
        
        public boolean isOnline() {
            return false;
        }
        
        public String getName() {
            return this.playerName;
        }
        
        public UUID getUniqueId() {
            return UUID.nameUUIDFromBytes(this.playerName.getBytes(Charsets.UTF_8));
        }
        
        public void setName(final String playerName) {
            this.playerName = playerName;
        }
        
        public boolean isBanned() {
            return false;
        }
        
        public void setBanned(final boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public boolean isWhitelisted() {
            return false;
        }
        
        public void setWhitelisted(final boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public Player getPlayer() {
            throw new UnsupportedOperationException();
        }
        
        public long getFirstPlayed() {
            return System.currentTimeMillis();
        }
        
        public long getLastPlayed() {
            return System.currentTimeMillis();
        }
        
        public boolean hasPlayedBefore() {
            return false;
        }
        
        public Location getBedSpawnLocation() {
            throw new UnsupportedOperationException();
        }
        
        public boolean isOp() {
            return false;
        }
        
        public void setOp(final boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public Map<String, Object> serialize() {
            throw new UnsupportedOperationException();
        }
    }
}
