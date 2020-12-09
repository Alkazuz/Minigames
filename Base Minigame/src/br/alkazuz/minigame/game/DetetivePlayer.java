package br.alkazuz.minigame.game;

import org.bukkit.entity.*;
import org.bukkit.*;

public class DetetivePlayer
{
    public boolean assassin;
    public boolean kill;
    public boolean voted;
    public String nick;
    
    public DetetivePlayer(String nick) {
        this.assassin = false;
        this.kill = false;
        this.voted = false;
        this.nick = nick;
    }
    
    public Player thePlayer() {
        return Bukkit.getPlayer(this.nick);
    }
}
