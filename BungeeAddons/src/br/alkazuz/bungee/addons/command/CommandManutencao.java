package br.alkazuz.bungee.addons.command;

import br.alkazuz.bungee.addons.main.BungeeConfig;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandManutencao extends Command
{
    public CommandManutencao() {
    	super("Manutencao", "bungee.command.manutencao", "manutencao");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        if(Args.length == 0) {
        	Sender.sendMessage("§cUso correto do comando: /manutencao <on/off>.");
        	Sender.sendMessage("§cUso correto do comando: /manutencao <add> <nick>.");
        	Sender.sendMessage("§cUso correto do comando: /manutencao <remove> <nick>.");
        	return;
        }
        if(Args[0].equalsIgnoreCase("on")) {
        	BungeeConfig.MAINTENANCE = true;
        	Sender.sendMessage("§aManutenção foi ativada.");
        	BungeeConfig.save();
        	return;
        }
        if(Args[0].equalsIgnoreCase("off")) {
        	BungeeConfig.MAINTENANCE = false;
        	Sender.sendMessage("§aManutenção foi desativada.");
        	BungeeConfig.save();
        	return;
        }
        if(Args[0].equalsIgnoreCase("add") && Args.length == 2) {
        	String nick = Args[1];
        	if(!BungeeConfig.MAINTENANCE_USERS.contains(nick.toLowerCase())) {
        		BungeeConfig.MAINTENANCE_USERS.add(nick.toLowerCase());
        		Sender.sendMessage("§aNick adicionado com sucesso.");
        		BungeeConfig.save();
        	}else {
        		Sender.sendMessage("§cNick já está na lista.");
        	}
        	return;
        }
        
        if(Args[0].equalsIgnoreCase("remove") && Args.length == 2) {
        	String nick = Args[1];
        	if(BungeeConfig.MAINTENANCE_USERS.contains(nick.toLowerCase())) {
        		BungeeConfig.MAINTENANCE_USERS.remove(nick.toLowerCase());
        		Sender.sendMessage("§aNick removido com sucesso.");
        		BungeeConfig.save();
        	}else {
        		Sender.sendMessage("§cNick não está na lista.");
        	}
        	return;
        }
    }
}
