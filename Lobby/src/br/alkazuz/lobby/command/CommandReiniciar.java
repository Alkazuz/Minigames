package br.alkazuz.lobby.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import br.alkazuz.lobby.api.AutoRestart;

public class CommandReiniciar implements CommandExecutor
{
    
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
    	if (!arg0.hasPermission("minigames.staff")) {
            arg0.sendMessage("§cComando inexistente ou voc\u00ea n\u00e3o tem acesso.");
            return true;
        }
    	AutoRestart.restarting = true;
    	AutoRestart.restartTime = System.currentTimeMillis();
        return true;
    }
}
