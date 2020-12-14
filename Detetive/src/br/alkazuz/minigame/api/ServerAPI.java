package br.alkazuz.minigame.api;

import org.bukkit.entity.*;
import java.io.*;
import br.alkazuz.minigame.main.*;
import org.bukkit.plugin.*;

public class ServerAPI
{
    public static void sendPlayer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        player.sendPluginMessage((Plugin)Main.theInstance(), "BungeeCord", b.toByteArray());
    }
}
