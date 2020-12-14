package br.alkazuz.minigame.api;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NameTagHideAPI {

	public static void hide(Player player) {
		Team t = null;
		Scoreboard score = player.getScoreboard();
		if(score != null) {
			t = score.registerNewTeam("Team_" + player.getName());
			t.addEntry(player.getName());
			t.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		}
	}
	
}
