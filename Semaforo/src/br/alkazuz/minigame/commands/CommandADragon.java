package br.alkazuz.minigame.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import br.alkazuz.minigame.main.*;
import br.alkazuz.minigame.game.*;
import java.util.*;

public class CommandADragon implements CommandExecutor
{
    public boolean onCommand(CommandSender Sender, Command Cmd, String Label, String[] Args) {
        if (Cmd.getName().equalsIgnoreCase("adragon")) {
            if (!Sender.hasPermission("adragon.admin")) {
                return true;
            }
            if (Args.length == 0) {
                Sender.sendMessage("§c/adragon forcestart");
                Sender.sendMessage("§c/adragon tostring");
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
        }
        return true;
    }
}
