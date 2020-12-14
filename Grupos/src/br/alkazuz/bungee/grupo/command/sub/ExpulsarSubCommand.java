package br.alkazuz.bungee.grupo.command.sub;

import br.alkazuz.bungee.grupo.command.SubCommand;
import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ExpulsarSubCommand extends SubCommand
{
    public ExpulsarSubCommand() {
        super("Expulsar", "expulsar", "<nick>");
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void run(final String[] args, final CommandSender s) {
        if (args.length != 2) {
            s.sendMessage("§cUso correto do comando: §4/grupo expulsar <jogador>");
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer)s;
        String target = args[1];
        Grupo grupo = Grupos.getGrupo(player);
        if (grupo == null) {
            player.sendMessage("§cVoc\u00ea n\u00e3o pertence a nenhum grupo.");
            return;
        }
        if (!grupo.leader.equals(player.getName())) {
            player.sendMessage("§cSomente o l\u00edder pode expulsar jogadores.");
            return;
        }
        final ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
        if (targetPlayer != null) {
            target = targetPlayer.getName();
        }else{
        	target = grupo.getMemberName(target);
        }
        if (target == null || !grupo.members.contains(target)) {
            player.sendMessage("§cO jogador n\u00e3o est\u00e1 no seu grupo.");
            return;
        }
        grupo.members.remove(target);
        grupo.broadcast("§c" + target+ " foi expulso do grupo.");
        if (targetPlayer != null) {
            targetPlayer.sendMessage("§cVoc\u00ea foi expulso do grupo.");
        }
    }
    
    @Override
    public String getDescription() {
        return "expulsa um jogador do grupo.";
    }
}
