package br.alkazuz.bungee.addons.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTell extends Command
{
    public CommandTell() {
    	super("Tell", "bungee.command.tell", "t", "tell", "msg");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        ProxiedPlayer player = (ProxiedPlayer)Sender;
        if(Args.length <= 1) {
        	player.sendMessage("§cUso correto do comando: /tell <jogador> <mensagem>.");
        	return;
        }
        String target = Args[0];
        ProxiedPlayer pTarget = BungeeCord.getInstance().getPlayer(target);
        if(pTarget == null) {
        	player.sendMessage("§cJogador offline ou não existe.");
        	return;
        }
        StringBuilder sb= new StringBuilder();
        for(int i = 1; i < Args.length; i++) {
        	sb.append(Args[i] + " ");
        }
        TellUtils.send(player, pTarget, sb.toString().trim());
    }
}
