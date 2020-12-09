package br.alkazuz.bungee.addons.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandReply extends Command
{
    public CommandReply() {
    	super("Reply", "bungee.command.reply", "r", "reply");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        ProxiedPlayer player = (ProxiedPlayer)Sender;
        if(Args.length <= 0) {
        	player.sendMessage("§cUso correto do comando: /r <mensagem>.");
        	return;
        }
        if(!TellUtils.reply.containsKey(player)) {
        	player.sendMessage("§cVocê não tem ninguém para responder.");
        	return;
        }
        ProxiedPlayer pTarget = TellUtils.reply.get(player);
        if(!pTarget.isConnected()) {
        	player.sendMessage("§c" + pTarget.getName() + " não está mais online.");
        	return;
        }
        StringBuilder sb= new StringBuilder();
        for(int i = 0; i < Args.length; i++) {
        	sb.append(Args[i] + " ");
        }
        TellUtils.send(player, pTarget, sb.toString().trim());
    }
}
