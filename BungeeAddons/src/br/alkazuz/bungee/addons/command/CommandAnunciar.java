package br.alkazuz.bungee.addons.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandAnunciar extends Command
{
    public CommandAnunciar() {
    	super("Anunciar", "bungee.command.anunciar", "anunciar");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        ProxiedPlayer player = (ProxiedPlayer)Sender;
        if(Args.length <= 0) {
        	player.sendMessage("§cUso correto do comando: /anunciar <mensagem>.");
        	return;
        }
        
        StringBuilder sb= new StringBuilder();
        for(int i = 0; i < Args.length; i++) {
        	sb.append(Args[i] + " ");
        }
        for(ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
        	all.sendMessage(" ");
        	all.sendMessage("§d§l[Servidor]: §d"+sb.toString().trim().replace("&", "§"));
        	all.sendMessage(" ");
        }
    }
}
