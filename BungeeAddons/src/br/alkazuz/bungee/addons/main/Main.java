package br.alkazuz.bungee.addons.main;

import br.alkazuz.bungee.addons.command.CommandAnunciar;
import br.alkazuz.bungee.addons.command.CommandManutencao;
import br.alkazuz.bungee.addons.command.CommandReply;
import br.alkazuz.bungee.addons.command.CommandTell;
import br.alkazuz.bungee.addons.listener.PlayerListener;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

public class Main extends Plugin
{
    public static Plugin plugin;
    public static Main instance;
    public String channel;
    public Configuration configuration;
    
    public Main() {
        this.channel = "PortalPlayer";
    }
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public static Plugin thePlugin() {
        return Main.plugin;
    }
    
    public void onEnable() {
        Main.plugin = this;
        Main.instance = this;
        this.getProxy().registerChannel(this.channel);
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandTell());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandReply());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandAnunciar());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandManutencao());
        final PluginManager pluginManager = Main.plugin.getProxy().getPluginManager();
        pluginManager.registerListener(Main.plugin, new PlayerListener());
        new Config(this);
        BungeeConfig.load(this);
    }
}
