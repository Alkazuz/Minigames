package br.alkazuz.arena.api;

import org.bukkit.entity.*;
import java.io.*;
import br.alkazuz.arena.main.*;
import org.bukkit.plugin.*;

public class ServerAPI
{
    public static void sendPlayer(final Player player, final String server) {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
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
