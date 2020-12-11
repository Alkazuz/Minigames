package br.alkazuz.bungee.grupo.command.sub;

import java.util.ArrayList;

import br.alkazuz.bungee.grupo.command.SubCommand;
import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ConvidarSubCommand extends SubCommand
{
    public ConvidarSubCommand() {
        super("Convidar", "convidar", "<jogador>");
    }
    
    @Override
    public void run(String[] args, CommandSender s) {
        if (args.length != 2) {
            s.sendMessage("§cUso correto do comando: §4/grupo convidar <jogador>");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)s;
        String target = args[1];
        Grupo grupo = Grupos.getGrupo(player);
        if(grupo == null) {
        	Grupos.create(player);
        	grupo = Grupos.getGrupo(player);
        	grupo.maxMember = getMaxMembers(player);
        	Grupos.invites.put(grupo, new ArrayList<String>());
        }
        if(grupo.members.size() >= grupo.maxMember) {
        	 player.sendMessage("§cO grupo antingiu o limite máximo de membros.");
             return;
        }
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
        if(targetPlayer != null) {
        	target = targetPlayer.getName();
        }
        if (targetPlayer != null && Grupos.getGrupo(targetPlayer) != null) {
            player.sendMessage("§cO jogador j\u00e1 pertence a um grupo.");
            return;
        }
        grupo.broadcast("§e" + target + " foi convidado para o grupo.");
        Grupos.invites.get(grupo).add(target.toLowerCase());
        if (targetPlayer != null) {
            targetPlayer.sendMessage(" ");
            targetPlayer.sendMessage("§eVoc\u00ea foi convidado para o grupo de §f" + grupo.leader);
            targetPlayer.sendMessage("§eUse §f/grupo aceitar §f" + grupo.leader + " §epara entrar.");
            targetPlayer.sendMessage(" ");
        }
    }
    
    public int getMaxMembers(ProxiedPlayer pp) {
    	
    	for(int i = 40; i > 0; i--) {
    		if(pp.hasPermission("grupos."+i)) {
    			return i;
    		}
    	}
    	return 3;
    }
    
    @Override
    public String getDescription() {
        return "convide amigos para jogar junto com voc\u00ea.";
    }
}
