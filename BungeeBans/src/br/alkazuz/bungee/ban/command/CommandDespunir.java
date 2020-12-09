package br.alkazuz.bungee.ban.command;

import br.alkazuz.bungee.ban.manager.Bans;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandDespunir extends Command
{
    public CommandDespunir() {
    	super("Despunir", "bungee.command.despunir", "despunir");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] args) {
        if(args.length != 1) {
        	Sender.sendMessage("§cUso correto do comando: /despunir <jogador>.");
        	return;
        }
        String user = args[0].toLowerCase();
        if(!Bans.bans.containsKey(user)) {
        	Sender.sendMessage("§cO jogador não está com alguma punição aplicada.");
        	return;
        }
        Bans.bans.get(user).unban();
        Bans.bans.remove(user);
        Sender.sendMessage("§aPunição retirada com sucesso.");
    	return;
    }
}
