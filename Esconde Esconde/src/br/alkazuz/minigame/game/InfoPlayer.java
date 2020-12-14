package br.alkazuz.minigame.game;

import org.bukkit.entity.*;
import org.bukkit.*;

public class InfoPlayer
{
    public boolean seek;
    public String nick;
    
    public InfoPlayer(String nick) {
        this.seek = false;
        this.nick = nick;
    }
    
    public Player thePlayer() {
        return Bukkit.getPlayer(this.nick);
    }
}
