package br.alkazuz.bungee.grupo.command.sub;

import br.alkazuz.bungee.grupo.command.SubCommand;
import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InfoSubCommand extends SubCommand
{
    public InfoSubCommand() {
        super("Info", "info", "");
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void run(final String[] args, final CommandSender s) {
        final ProxiedPlayer player = (ProxiedPlayer)s;
        Grupo grupo = Grupos.getGrupo(player);
        if (grupo == null) {
            player.sendMessage("§cVoc\u00ea n\u00e3o pertence a nenhum grupo.");
            return;
        }
        final StringBuilder sb = new StringBuilder();
        ProxiedPlayer leader =  BungeeCord.getInstance().getPlayer(grupo.leader);
        sb.append("\n§aDono(a): §f" + (leader == null ? grupo.leader : Grupos.getDisplayName(leader))+ "\n");
        sb.append("§aMembros (§f" + grupo.members.size() + "/"+grupo.maxMember+"§a): §f");
        String membros = "";
        if(grupo.members.size() > 1) {
        	
        	for (final String n : grupo.members) {
        		String input = "§c• "+n;
        		if(n.equals(grupo.leader)) continue;
        		ProxiedPlayer p = BungeeCord.getInstance().getPlayer(n);
            	if(p!= null) {
            		input = "§a• "+Grupos.getDisplayName(p);
            	}
                membros = String.valueOf(membros) + input + "§f, ";
            }
            
            membros = membros.trim().substring(0, membros.trim().length() - 1);
        }
        
        sb.append(String.valueOf(membros) + "\n");
        player.sendMessage(sb.toString());
    }
    
    @Override
    public String getDescription() {
        return "ver informa\u00e7\u00f5es do grupo.";
    }
}
