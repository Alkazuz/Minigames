package br.alkazuz.bungee.grupo.command.sub;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import br.alkazuz.bungee.grupo.command.SubCommand;
import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProcurarSubCommand extends SubCommand
{
    public HashMap<String, Long> delay;
    
    public ProcurarSubCommand() {
        super("Procurar", "procurar", "");
        this.delay = new HashMap<String, Long>();
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void run(final String[] args, final CommandSender s) {
        final ProxiedPlayer player = (ProxiedPlayer)s;
        Grupo grupo = Grupos.getGrupo(player);
        if (grupo != null) {
            player.sendMessage("§cVoc\u00ea j\u00e1 est\u00e1 em um grupo.");
            return;
        }
        if (this.delay.containsKey(player.getName()) && this.delay.get(player.getName()) - System.currentTimeMillis() < TimeUnit.MINUTES.toMillis(5L)) {
            player.sendMessage("§cVoc\u00ea deve esperar cerca de 5 minutos para fazer isso novamente.");
            return;
        }
        this.delay.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
        ProxyServer.getInstance().broadcast("§3» §b" + Grupos.getDisplayName(player) + " §3est\u00e1 procurando por um grupo.");
    }
    
    @Override
    public String getDescription() {
        return "avisa a todos que voc\u00ea est\u00e1 a procura de um grupo.";
    }
}
