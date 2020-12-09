package br.alkazuz.lobby.command;

import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.*;

public class CommandGlobal implements CommandExecutor
{
    private HashMap<Player, Long> delay;
    
    public CommandGlobal() {
        this.delay = new HashMap<Player, Long>();
    }
    
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg3.length == 0) {
            arg0.sendMessage("§cUso correto do comando: /g <mensagem>");
            return true;
        }
        Player p = (Player)arg0;
        if (this.delay.containsKey(p)) {
            Long now = System.currentTimeMillis();
            if (this.delay.get(p) > now) {
                p.sendMessage("§cVoc\u00ea deve esperar §e" + (this.delay.get(p) - now) / 1000L + "s §cpara enviar mensagem no global novamente.");
                return true;
            }
        }
        if (p.hasPermission("minigames.vip")) {
            this.delay.put(p, System.currentTimeMillis() + 5000L);
        }
        else {
            this.delay.put(p, System.currentTimeMillis() + 15000L);
        }
        if (p.hasPermission("minigames.staff")) {
            this.delay.put(p, System.currentTimeMillis());
        }
        String msg = "";
        for (int i = 0; i < arg3.length; ++i) {
            msg = String.valueOf(msg) + arg3[i] + " ";
        }
        msg = msg.trim();
        if (!p.hasPermission("minigames.staff")) {
            msg = msg.toLowerCase();
        }
        if (p.hasPermission("minigames.staff")) {
            msg = msg.replace("&", "§");
        }
        else if (p.hasPermission("minigames.vip")) {
            msg = msg.toLowerCase().replace("&", "§").replace("§0", "").replace("§2", "").replace("§3", "").replace("§d", "").replace("§l", "").replace("§k", "").replace("§k", "").replace("§m", "").replace("§n", "").replace("§o", "");
        }
        String format = "§7[§aG§7] §f{0}§7: §7" + msg;
        if (p.hasPermission("minigames.staff")) {
            format = "§7[§aG§7] §4[STAFF] §f{0}§7: §d" + msg;
        }
        else if (p.hasPermission("minigames.vip")) {
            format = "§7[§aG§7] §6[VIP] §f{0}§7: §7" + msg;
        }
        else {
            msg = msg.toLowerCase();
        }
        String send = format.replace("{0}", p.getName()).replace("{1}", msg.trim());
        Bukkit.broadcastMessage(send);
        return true;
    }
}
