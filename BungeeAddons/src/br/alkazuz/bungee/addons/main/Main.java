package br.alkazuz.bungee.addons.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import br.alkazuz.bungee.addons.command.CommandAnunciar;
import br.alkazuz.bungee.addons.command.CommandBungeeTP;
import br.alkazuz.bungee.addons.command.CommandManutencao;
import br.alkazuz.bungee.addons.command.CommandReply;
import br.alkazuz.bungee.addons.command.CommandReport;
import br.alkazuz.bungee.addons.command.CommandTell;
import br.alkazuz.bungee.addons.listener.PlayerListener;
import br.alkazuz.bungee.addons.mysql.MySQLConnection;
import br.alkazuz.bungee.addons.object.BungeeGroup;
import br.alkazuz.bungee.addons.object.BungeeGroupManager;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

public class Main extends Plugin
{
    public static Plugin plugin;
    public static Main instance;
    public static String channel = "bungeeplayertp";
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
        this.getProxy().registerChannel("Vanish");
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandTell());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandReply());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandAnunciar());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandManutencao());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandBungeeTP());
        this.getProxy().getPluginManager().registerCommand(Main.plugin, (Command)new CommandReport());
        
        final PluginManager pluginManager = Main.plugin.getProxy().getPluginManager();
        pluginManager.registerListener(Main.plugin, new PlayerListener());
        new Config(this);
        BungeeConfig.load(this);
        new MySQLConnection();
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
    			BungeeGroupManager.groups.clear();
    			try {
    				PreparedStatement ps = MySQLConnection.con.prepareStatement("SELECT * FROM "+BungeeConfig.GROUP_TABLE+" ORDER BY priority DESC");
                    final ResultSet rs = ps.executeQuery();
                    while (rs.next())
                    {
                    	String group = rs.getString("name");
                    	String permission = rs.getString("permission");
                    	String prefix = rs.getString("prefix").replace("&", "§");
                    	String suffix = rs.getString("suffix").replace("&", "§");
                    	int priority = rs.getInt("priority");
                        BungeeGroup bungeeGroup = new BungeeGroup(group, prefix, suffix, priority);
                        BungeeGroupManager.groups.put(permission, bungeeGroup);
                        if(group.equals(BungeeConfig.GROUP_DEFAULT)) {
                        	BungeeGroupManager.defaultGroup = bungeeGroup;
                        }
                    }
                    
                    rs.close();
                    ps.close();
    			}catch (Exception e) {
					e.printStackTrace();
				}
            }
        }, 1, 2, TimeUnit.MINUTES);
    }
}
