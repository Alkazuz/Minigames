package br.alkazuz.bungee.grupo.command.sub;

import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import br.alkazuz.bungee.grupo.command.SubCommand;

public class AceitarSubCommand extends SubCommand
{
    public AceitarSubCommand() {
        super("Aceitar", "aceitar", "<nick>");
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void run(String[] args, CommandSender s) {
        if (args.length != 2) {
            s.sendMessage("§cUso correto do comando: §4/grupo aceitar <jogador>");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)s;
        String user = args[1];
        Grupo grupo = Grupos.getGrupo(player);
        if (grupo != null) {
            player.sendMessage("§cVoc\u00ea j\u00e1 possui um grupo.");
            return;
        }
        grupo = Grupos.getInvite(player.getName(), user);
        if (grupo == null) {
            player.sendMessage("§cVoc\u00ea n\u00e3o recebeu nenhum convite deste grupo ou ele foi deletado.");
            return;
        }
        grupo.members.add(player.getName());
        Grupos.invites.get(grupo).remove(player.getName());
        grupo.broadcast("§a" + player.getName() + " entrou no grupo.");
    }
    
    @Override
    public String getDescription() {
        return "aceita um pedido de grupo.";
    }
}
