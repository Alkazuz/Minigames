package br.alkazuz.minigame.game;

import org.bukkit.entity.*;
import org.bukkit.*;

public class SkywarsPlayer
{
    public int kills = 0;
    public int streak = 0;
    public Long lastKill = 0L;
    public String nick;
    public Arena arena;
    
    public SkywarsPlayer(String nick, Arena arena) {
        this.nick = nick;
        this.arena = arena;
    }
    
    public Player thePlayer() {
        return Bukkit.getPlayer(this.nick);
    }
}
