package br.alkazuz.bungee.ban.main;

import br.alkazuz.bungee.ban.command.CommandDespunir;
import br.alkazuz.bungee.ban.command.CommandPunir;
import br.alkazuz.bungee.ban.listener.PlayerListener;
import br.alkazuz.bungee.ban.manager.Punicao;
import br.alkazuz.bungee.ban.mysql.MySQLConnection;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

public class Main extends Plugin
{
    public static Plugin plugin;
    public static Main instance;
    public Configuration configuration;
    
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public static Plugin thePlugin() {
        return Main.plugin;
    }
    
    public void onEnable() {
        Main.plugin = this;
        Main.instance = this;
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandPunir());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandDespunir());
        final PluginManager pluginManager = Main.plugin.getProxy().getPluginManager();
        pluginManager.registerListener(Main.plugin, new PlayerListener());
        new Config(this);
        Punicao.load(this);
        BungeeConfig.load(this);
        new MySQLConnection();
    }
}
