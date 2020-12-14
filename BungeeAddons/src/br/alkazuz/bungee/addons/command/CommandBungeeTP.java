package br.alkazuz.bungee.addons.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.alkazuz.bungee.addons.main.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandBungeeTP extends Command
{
    public CommandBungeeTP() {
    	super("BungeeTP", "bungee.command.bungeetp", "bungeetp", "btp");
    }
    
    public static List<String> vanishList = new ArrayList<String>();
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        ProxiedPlayer player = (ProxiedPlayer)Sender;
        if(Args.length <= 0) {
        	player.sendMessage("§cUso correto do comando: /btp <jogador>.");
        	return;
        }
        ProxiedPlayer target = BungeeCord.getInstance().getPlayer(Args[0]);
        if(target == null) {
        	player.sendMessage("§cO jogador não está conectado no servidor.");
        	return;
        }
        sendPlayer(player, target);
    }
    
    public void sendPlayer(ProxiedPlayer player, ProxiedPlayer target) {
    	player.sendMessage("§aVocê estará invisível para os jogadores.");
    	
    	ByteArrayDataOutput out = ByteStreams.newDataOutput();
    	//out.writeUTF( "BungeeCord" ); 
        try {
        	out.writeUTF( Main.channel ); 
            out.writeUTF( player.getName() ); 
            out.writeUTF( target.getName() ); 
            out.writeBoolean(true);
        }catch (Exception e) {
			e.printStackTrace();
		}
        //target.getServer().sendData("Vanish", bb.toByteArray());
        target.getServer().getInfo().sendData("Vanish", out.toByteArray());
        player.connect(target.getServer().getInfo());
        vanishList.add(player.getName());
    }
    
}
