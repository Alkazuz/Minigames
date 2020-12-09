package br.alkazuz.arena.game;

import java.util.concurrent.atomic.*;
import org.bukkit.entity.*;
import org.bukkit.*;

public class ArenaPlayer
{
    public AtomicInteger kills;
    public String nick;
    
    public ArenaPlayer() {
        this.kills = new AtomicInteger(0);
    }
    
    public Player thePlayer() {
        return Bukkit.getPlayer(this.nick);
    }
}
