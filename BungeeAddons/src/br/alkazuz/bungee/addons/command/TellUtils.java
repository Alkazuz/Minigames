package br.alkazuz.bungee.addons.command;

import java.util.HashMap;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TellUtils {
	
	public static HashMap<ProxiedPlayer, ProxiedPlayer> reply = new HashMap<ProxiedPlayer, ProxiedPlayer>();
	public static void send(ProxiedPlayer player, ProxiedPlayer target, String message) {
		String color = "§6";
		String color2 = "§e";
		if(target.hasPermission("minigames.color.admin") || player.hasPermission("minigames.color.admin")) {
			color = "§4";
			color2 = "§c";
		}
		target.sendMessage(String.format("%s[%s para Você]: %s%s", color, player.getName(), color2, message));
		player.sendMessage(String.format("%s[Você para %s]: %s%s", color, target.getName(), color2, message));
		reply.put(player, target);
		reply.put(target, player);
	}

}
