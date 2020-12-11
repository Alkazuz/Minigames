package br.alkazuz.bungee.grupo.listener;

import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import br.alkazuz.bungee.grupo.object.Grupo;
import br.alkazuz.bungee.grupo.object.Grupos;
import net.md_5.bungee.api.event.ServerConnectEvent;
import br.alkazuz.bungee.grupo.main.Main;
import net.md_5.bungee.api.plugin.Listener;

public class PluginMessageReceiver implements Listener
{
    public PluginMessageReceiver(Main main) {
    }
    
    @EventHandler
    public void onTeleport(ServerConnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        Grupo grupo = Grupos.getGrupo(p);
        if (grupo != null && grupo.leader.equals(p.getName())) {
            grupo.sendAll(event.getTarget().getName(), event.getTarget().getName());
        }
    }
}
