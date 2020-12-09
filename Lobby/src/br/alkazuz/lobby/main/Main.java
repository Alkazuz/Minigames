package br.alkazuz.lobby.main;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.huskehhh.mysql.mysql.MySQL;

import br.alkazuz.lobby.api.AutoRestart;
import br.alkazuz.lobby.api.QueueAPI;
import br.alkazuz.lobby.api.RankingMinigamesAPI;
import br.alkazuz.lobby.command.CommandGlobal;
import br.alkazuz.lobby.command.CommandReiniciar;
import br.alkazuz.lobby.command.CommandSetLobby;
import br.alkazuz.lobby.command.CommandSetNPC;
import br.alkazuz.lobby.listener.CommandListener;
import br.alkazuz.lobby.listener.PlayerListener;
import br.alkazuz.lobby.object.NPCRank;
import br.alkazuz.lobby.object.Servidor;
import br.alkazuz.lobby.object.Servidores;
import br.alkazuz.lobby.scoreboard.ScoreBoard;
import br.alkazuz.lobby.sql.SQLData;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.milkbowl.vault.chat.Chat;

public class Main extends JavaPlugin implements PluginMessageListener, Runnable
{
    private static Main instance;
    public FileConfiguration config;
    public static Map<String, Integer> players;
    
    static {
        Main.players = new HashMap<String, Integer>();
    }
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public void writeMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage("§e[Lobby] §7" + msg);
    }
    
    public String getPrefix(final String jogador) {
        final String world = "world";
        String prefix = "";
        if (this.chat != null) {
            prefix = this.chat.getPlayerPrefix(world, jogador).replace("&", "§");
        }
        return prefix;
    }
    
    @Override
    public void onDisable() {
    	
    	for(Servidor servidor : Servidores.servidores) {
    		if(servidor.ranks.isEmpty()) continue;
    		for(Location loc : servidor.ranks.keySet()) {
    			NPCRank npc = servidor.ranks.get(loc);
    			if(npc != null) {
    				npc.delete();
    			}
    		}
    	}
    }
    
    private Chat chat;
    public boolean setupChat() {
        final RegisteredServiceProvider<Chat> service = (RegisteredServiceProvider<Chat>)Bukkit.getServicesManager().getRegistration((Class)Chat.class);
        if (service != null) {
            this.chat = (Chat)service.getProvider();
        }
        return this.chat != null;
    }
    
    public void onEnable() {
        Main.instance = this;
        this.config = ConfigManager.getConfig("config");
        this.getCommand("setlobby").setExecutor(new CommandSetLobby());
        this.getCommand("setnpc").setExecutor(new CommandSetNPC());
        this.getCommand("g").setExecutor(new CommandGlobal());
        this.getCommand("reini").setExecutor(new CommandReiniciar());
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new CommandListener(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, this, 20L, 20L);
        new QueueAPI();
        setupChat();
        try {
            MySQL msql = new MySQL(this.config.getString("SQL.host"), this.config.getString("SQL.port"), this.config.getString("SQL.database"), this.config.getString("SQL.user"), this.config.getString("SQL.pass"));
            SQLData.con = msql.openConnection();
            SQLData.statement = SQLData.con.createStatement();
            this.writeMessage("§aO Servidor est\u00e1 usando MySQL");
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        
        new Servidores();
        new RankingMinigamesAPI();
        
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, new AutoRestart(), 20L, 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
            	AutoRestart.started = true;
            }
        });
        while(CitizensAPI.getNPCRegistry().iterator().hasNext()) {
    		NPC dd = CitizensAPI.getNPCRegistry().iterator().next();
    		dd.despawn();
    		dd.destroy();
    	}
    }
    
    public void run() {
    	for(Player p : Bukkit.getOnlinePlayers()) {
    		if (QueueAPI.lobby.containsKey(p.getName())) {
                Servidor servidor = QueueAPI.lobby.get(p.getName());
                ScoreBoard.updateScoreboardMinigame(p, servidor);
            }
    	}
    }
    
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("PlayerCount")) {
            String server = in.readUTF();
            int playercount = in.readInt();
            Main.players.put(server, playercount);
        }
    }
}
