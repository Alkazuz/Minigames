package br.alkazuz.minigame.scoreboard;

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
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import br.alkazuz.minigame.game.EscPlayer;
import br.alkazuz.minigame.game.Round;

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
    private EscPlayer escPlayer;
    private Round round;
    private static Team hiden;
    private static Team seek;
    
    public ScoreBoardAPI(EscPlayer escPlayer, Round round, String title, String objName) {
        this.objName = null;
        this.obj = null;
        this.nteams = 50;
        this.nnn = 100;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = title;
        this.objName = objName;
        this.scores = new HashMap<String, Integer>();
        this.teams = new HashMap<Integer, Team>();
        
        this.escPlayer = escPlayer;
        this.round = round;
        
        if(hiden == null) {
        	hiden = scoreboard.registerNewTeam("Team_Hiden");
        	hiden.setPrefix("§6");
        }
        
        if(seek == null) {
        	seek = scoreboard.registerNewTeam("Team_Seek");
        	seek.setPrefix("§3");
        }
    }
    
    public void updateplayer() {
    	if(hiden.hasEntry(escPlayer.player.getName())) {
    		hiden.removeEntry(escPlayer.player.getName());
    	}
    	if(seek.hasEntry(escPlayer.player.getName())) {
    		seek.removeEntry(escPlayer.player.getName());
    	}
    	if(escPlayer.seek) {
    		seek.addEntry(escPlayer.player.getName());
    	}else {
    		hiden.addEntry(escPlayer.player.getName());
    	}
    }
    
    public void updateTagHide() {
    	hiden.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
    }
    
    public void blankLine() {
        this.add("§c ");
    }
    
    public void blankLine(int n) {
        this.add("§c ", n);
    }
    
    public HashMap<Integer, Team> getTeams() {
        return this.teams;
    }
    
    public void add(String s) {
        this.add(s, null);
    }
    
    public void add(String s, Integer n) {
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
    
    private Map.Entry<Team, String> createTeam(String s) {
        Team registerNewTeam = this.scoreboard.registerNewTeam(new StringBuilder().append(this.nteams--).toString());
        if (s.length() <= 16) {
            return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, s);
        }
        Iterator<String> iterator = Splitter.fixedLength(16).split((CharSequence)s).iterator();
        registerNewTeam.setPrefix((String)iterator.next());
        return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, iterator.next());
    }
    
    public void hideNameTag() {
    	
    }
    
    public void build() {
        (this.obj = this.scoreboard.registerNewObjective(this.objName, "dummy")).setDisplayName(ChatColor.translateAlternateColorCodes('&', this.title));
        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        int n = this.scores.size() - 2 + 1;
        int n2 = 0;
        for (Map.Entry<String, Integer> entry : this.scores.entrySet()) {
            n2 += 2;
            Map.Entry<Team, String> team = this.createTeam(entry.getKey());
            int score = (entry.getValue() != null) ? entry.getValue() : n;
            OfflinePlayerv2 offlinePlayerv2 = new OfflinePlayerv2(team.getValue());
            team.getKey().addPlayer((OfflinePlayer)offlinePlayerv2);
            this.obj.getScore((OfflinePlayer)offlinePlayerv2).setScore(score);
            this.teams.put(score, team.getKey());
            --n;
        }
    }
    
    public void setDisplayName(String displayName) {
        this.obj.setDisplayName(displayName);
    }
    
    public void reset() {
        this.title = null;
        this.scores.clear();
        Iterator<Team> iterator = this.teams.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().unregister();
        }
        this.teams.clear();
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public void send(Player... array) {
        try {
            for (int length = array.length, i = 0; i < length; ++i) {
                array[i].setScoreboard(this.scoreboard);
            }
        }
        catch (NullPointerException ex) {}
    }
    
    public void update(String suffix, int score) {
        if (this.teams.containsKey(score)) {
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            Team team = this.teams.get(score);
            if (suffix.length() <= 16) {
                team.setPrefix("");
                team.setSuffix(suffix);
                return;
            }
            Map.Entry<Team, String> entry = this.makeTeam(suffix);
            if (score == 3) {
                entry = this.makeTeam("  §fOnline: §a" + Bukkit.getServer().getOnlinePlayers().size());
            }
            OfflinePlayerv2 offlinePlayerv2 = new OfflinePlayerv2(entry.getValue());
            entry.getKey().addPlayer((OfflinePlayer)offlinePlayerv2);
            this.resetScore(score);
            this.obj.getScore((OfflinePlayer)offlinePlayerv2).setScore(score);
        }
        else {
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            this.add(suffix, score);
        }
    }
    
    public void resetScore(int n) {
        for (String s : this.obj.getScoreboard().getEntries()) {
            if (this.obj.getScore(s).getScore() == n) {
                this.getScoreboard().resetScores(s);
            }
        }
    }
    
    private Map.Entry<Team, String> makeTeam(String s) {
        Team registerNewTeam = this.scoreboard.registerNewTeam(new StringBuilder().append(this.nnn++).toString());
        if (s.length() <= 16) {
            return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, s);
        }
        Iterator<String> iterator = Splitter.fixedLength(16).split((CharSequence)s).iterator();
        registerNewTeam.setPrefix((String)iterator.next());
        return new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, iterator.next());
    }
    
    public class OfflinePlayerv2 implements OfflinePlayer
    {
        private String playerName;
        
        public OfflinePlayerv2(String playerName) {
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
        
        public void setName(String playerName) {
            this.playerName = playerName;
        }
        
        public boolean isBanned() {
            return false;
        }
        
        public void setBanned(boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public boolean isWhitelisted() {
            return false;
        }
        
        public void setWhitelisted(boolean b) {
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
        
        public void setOp(boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public Map<String, Object> serialize() {
            throw new UnsupportedOperationException();
        }
    }
}
