package br.alkazuz.lobby.command;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.alkazuz.lobby.api.Methods;
import br.alkazuz.lobby.main.DataManager;
import br.alkazuz.lobby.main.Main;
import br.alkazuz.lobby.object.Servidor;
import br.alkazuz.lobby.object.Servidores;

public class CommandSetLobby implements CommandExecutor
{
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (!arg0.hasPermission("minigames.staff")) {
            arg0.sendMessage("§cComando inexistente ou voc\u00ea n\u00e3o tem acesso.");
            return true;
        }
        if (arg3.length == 0) {
            arg0.sendMessage("§cUso correto do comando: /setlobby <minigame>");
            return true;
        }
        String mg = arg3[0];
        Servidor sv = null;
        for (Servidor d : Servidores.servidores) {
            if (d.name.equals(mg)) {
                sv = d;
                break;
            }
        }
        if (sv == null) {
            arg0.sendMessage("§cMinigame inexistente");
        }
        arg0.sendMessage("§aLobby setado com sucesso");
        Player p = (Player)arg0;
        Main.theInstance().config.set("servers." + mg + ".lobby", (Object)Methods.encodeLocation(p.getLocation()));
        sv.lobby = p.getLocation();
        try {
            Main.theInstance().config.save(DataManager.getFile("config"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
