package br.alkazuz.bungee.grupo.main;

import net.md_5.bungee.api.plugin.PluginManager;
import br.alkazuz.bungee.grupo.command.SubCommands;
import net.md_5.bungee.api.plugin.Listener;
import br.alkazuz.bungee.grupo.listener.PluginMessageReceiver;
import net.md_5.bungee.api.plugin.Command;
import br.alkazuz.bungee.grupo.command.CommandGrupo;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin
{
    public static Plugin plugin;
    public static Main instance;
    public String channel;
    
    public Main() {
        this.channel = "PortalPlayer";
    }
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public static Plugin thePlugin() {
        return Main.plugin;
    }
    
    public void sendConsoleMessage(final String msg) {
        this.getProxy().getConsole().sendMessage("§a[Grupo] " + msg);
    }
    
    public void onEnable() {
        Main.plugin = this;
        Main.instance = this;
        this.getProxy().registerChannel(this.channel);
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandGrupo());
        final PluginManager pluginManager = Main.plugin.getProxy().getPluginManager();
        pluginManager.registerListener(Main.plugin, (Listener)new PluginMessageReceiver(this));
        new SubCommands();
    }
}
