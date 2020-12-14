package br.alkazuz.minigame.api;

import org.bukkit.entity.Player;

import br.alkazuz.minigame.scoreboard.ScoreBoard;

public class NoNameTag {

    public static void showNametag(Player player) {
    	ScoreBoard.update(player);
    }
	
    public static void hideNametag(Player player) {
    	ScoreBoard.update(player);
    }
    
}
