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

public class CommandSetNPCServer implements CommandExecutor
{
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (!arg0.hasPermission("minigames.staff")) {
            arg0.sendMessage("§cComando inexistente ou voc\u00ea n\u00e3o tem acesso.");
            return true;
        }
        if (arg3.length == 0) {
            arg0.sendMessage("§cUso correto do comando: /setnpcserver <servidor>");
            return true;
        }
        Player p = (Player)arg0;
        Servidor sv = null;
        for (Servidor servidor : Servidores.servidores) {
            if (servidor.name.equalsIgnoreCase(arg3[0])) {
            	sv = servidor;
            }
        }
        
        if (sv == null) {
            arg0.sendMessage("§cMinigame inexistente");
            return true;
        }
        
        Main.theInstance().config.set("servers." + sv.name + ".npc", Methods.encodeLocation(p.getLocation()));
        arg0.sendMessage("§aNPC do servidor setado com sucesso.");
        try {
            Main.theInstance().config.save(DataManager.getFile("config"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
