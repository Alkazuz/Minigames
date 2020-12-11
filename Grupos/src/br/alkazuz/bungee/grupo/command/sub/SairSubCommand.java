package br.alkazuz.bungee.grupo.command.sub;

import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import br.alkazuz.bungee.grupo.command.SubCommand;

public class SairSubCommand extends SubCommand
{
    public SairSubCommand() {
        super("Sair", "sair", "");
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void run(final String[] args, final CommandSender s) {
        if (args.length != 1) {
            s.sendMessage("§cUso correto do comando: §4/grupo sair");
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer)s;
        Grupo grupo = Grupos.getGrupo(player);
        if (grupo == null) {
            player.sendMessage("§cVoc\u00ea n\u00e3o pertence a nenhum grupo.");
            return;
        }
        if (grupo.leader.contentEquals(player.getName())) {
            grupo.broadcast("§c" + Grupos.getDisplayName(player) + " desfez o grupo.");
            Grupos.data.remove(grupo.leader);
        }
        else {
            grupo.broadcast("§c" + Grupos.getDisplayName(player) + " saiu do grupo.");
            grupo.members.remove(player.getName());
        }
    }
    
    @Override
    public String getDescription() {
        return "sai de um grupo, o grupo ser\u00e1 deletado se for o l\u00edder.";
    }
}
