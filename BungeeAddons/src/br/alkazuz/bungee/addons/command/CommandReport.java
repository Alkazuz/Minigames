package br.alkazuz.bungee.addons.command;

import java.util.ArrayList;
import java.util.List;

import br.alkazuz.bungee.addons.object.BungeeGroup;
import br.alkazuz.bungee.addons.object.BungeeGroupManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.chat.ComponentSerializer;

public class CommandReport extends Command
{
    public CommandReport() {
    	super("Reportar", "bungee.command.report", "report", "report");
    }
    
    public static List<String> vanishList = new ArrayList<String>();
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        ProxiedPlayer player = (ProxiedPlayer)Sender;
        if(Args.length <= 1) {
        	player.sendMessage("§cUso correto do comando: /reportar <jogador> <motivo>.");
        	return;
        }
        ProxiedPlayer target = BungeeCord.getInstance().getPlayer(Args[0]);
        if(target == null) {
        	player.sendMessage("§cO jogador não está conectado no servidor.");
        	return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < Args.length; i++) {
        	sb.append(Args[i] + " ");
        }
        reportPlayer(player, target, sb.toString().trim());
        player.sendMessage("§aVocê reportou o jogador §a"+getDisplayName(target)+" §apelo motivo §7"+sb.toString().trim()+"§e.");
    }
    
    public void reportPlayer(ProxiedPlayer player, ProxiedPlayer target, String reason) {
    	StringBuilder sb = new StringBuilder();
    	
    	String json = "[\"\",{\"text\":\"Clique \",\"color\":\"yellow\"},{\"text\":\"aqui\",\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/btp %p%\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"Clique para teleportar até %p%.\",\"color\":\"green\"}}},{\"text\":\" para teleportar até o jogador.\",\"color\":\"yellow\"}]".
    			replaceAll("%p%", target.getName());
    	sb.append("§e\n§6[§lREPORT§6]\n");
    	sb.append(getDisplayName(player) + " §ereportou o jogador "+getDisplayName(target)+"§e!\n");
    	sb.append("§eMotivo: §7"+reason+"§e.\n");
    	sb.append("§eServidor: §b"+player.getServer().getInfo().getName()+"§e.");
    	for(ProxiedPlayer staff : BungeeCord.getInstance().getPlayers()) {
    		if(staff.hasPermission("minigames.report.staff")) {
    			staff.sendMessage(sb.toString());
    			staff.sendMessage(ComponentSerializer.parse(json));
    			staff.sendMessage(" ");
    		}
    	}
    }
    
    public static String getDisplayName(ProxiedPlayer player) {
		BungeeGroup group = BungeeGroupManager.getPlayerGroup(player);
		return group.getPrefix() + player.getName() + group.getSuffix();
	}
    
}
