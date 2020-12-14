package br.alkazuz.minigame.game;

import org.bukkit.entity.Player;

public class EscPlayer
{
    public boolean seek;
    public Player player;
    
    public EscPlayer(Player player) {
        this.seek = false;
        this.player = player;
    }
    
    public Player getPlayer() {
        return player;
    }
}
