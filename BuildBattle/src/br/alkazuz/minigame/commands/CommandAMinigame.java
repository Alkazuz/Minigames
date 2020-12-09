package br.alkazuz.minigame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.main.Main;

public class CommandAMinigame implements CommandExecutor
{
    public boolean onCommand(CommandSender Sender, Command Cmd, String Label, String[] Args) {
    	if (!Sender.hasPermission("abuildbattle.admin")) {
            return true;
        }
        if (Args.length == 0) {
            Sender.sendMessage("§c/abuildbattle forcestart");
            Sender.sendMessage("§c/abuildbattle tostring");
            return true;
        }
        Player p = (Player)Sender;
        if (Args[0].equalsIgnoreCase("tostring")) {
            for (Round r : Main.theInstance().rounds) {
                if (r.hasPlayer(p)) {
                    p.sendMessage(r.toString());
                }
            }
        }
        if (Args[0].equalsIgnoreCase("forcestart")) {
            for (Round r : Main.theInstance().rounds) {
                if (r.hasPlayer(p)) {
                    r.start();
                    break;
                }
            }
        }
        return true;
    }
}
