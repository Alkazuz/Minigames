package br.alkazuz.spigot.addons.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.alkazuz.spigot.addons.object.SpigotGroupManager;

public class PlayerListener implements Listener
{
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String displayername = String.format("%s%s%s", SpigotGroupManager.getPlayerGroup(player).getPrefix(),
				player.getName(), SpigotGroupManager.getPlayerGroup(player).getSuffix());
		player.setDisplayName(displayername);
	}
}
