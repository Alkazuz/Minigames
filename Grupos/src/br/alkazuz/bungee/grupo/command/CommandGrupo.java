package br.alkazuz.bungee.grupo.command;

import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandGrupo extends Command
{
    public CommandGrupo() {
        super("grupo");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] Args) {
        final ProxiedPlayer player = (ProxiedPlayer)Sender;
        Grupo grupo = Grupos.getGroupPlayer(player.getName());
        
        if (grupo == null && Args.length == 0) {
            player.sendMessage("§a§lComandos dispon\u00edveis:");
            for (final SubCommand sb : SubCommands.all()) {
                Sender.sendMessage("§a/grupo " + sb.getSubCommand() + " " + sb.getUsage() + " §8- §7" + sb.getDescription());
            }
            return;
        }
        if (Args.length >= 1) {
            if (Args[0].equalsIgnoreCase("ajuda")) {
                player.sendMessage("§a§lComandos dispon\u00edveis:");
                Sender.sendMessage("§a/grupo <mensagem> §8- §7envia uma mensagem para o grupo.");
                for (final SubCommand sb : SubCommands.all()) {
                    Sender.sendMessage("§a/grupo " + sb.getSubCommand() + " " + sb.getUsage() + " §8- §7" + sb.getDescription());
                }
                return;
            }
            final SubCommand sub = SubCommands.get(Args[0]);
            if (sub != null) {
                sub.run(Args, Sender);
                return;
            }
            if (grupo == null) {
                player.sendMessage("§a§lComandos dispon\u00edveis:");
                for (final SubCommand sb2 : SubCommands.all()) {
                    Sender.sendMessage("§a/grupo " + sb2.getSubCommand() + " " + sb2.getUsage() + " §8- §7" + sb2.getDescription());
                }
                return;
            }
            String msg = "";
            for (int i = 0; i < Args.length; ++i) {
                msg = String.valueOf(msg) + Args[i] + " ";
            }
            msg = msg.trim();
            grupo.sendMessage(player, msg);
        }
    }
}
