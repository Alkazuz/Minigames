package br.alkazuz.minigame.game;

import org.bukkit.entity.Player;

public class SkywarsPlayer
{
    public int kills = 0;
    public int streak = 0;
    public Long lastKill = 0L;
    public Player player;
    public Arena arena;
    public boolean died = false;
    
    public SkywarsPlayer(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }
    
    public Player thePlayer() {
        return player;
    }
}
