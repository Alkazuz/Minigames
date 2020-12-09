package br.alkazuz.arena.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.arena.api.ActionBarAPI;
import br.alkazuz.arena.api.TagAPI;
import br.alkazuz.arena.data.PlayerData;
import br.alkazuz.arena.data.PlayerManager;
import br.alkazuz.arena.main.Main;
import br.alkazuz.arena.scoreboard.ScoreBoard;
import br.alkazuz.arena.shop.ShopMenu;
import br.alkazuz.arena.utils.ItemBuilder;
import br.alkazuz.arena.utils.Methods;

public class Arena
{
    public String name;
    public Integer task;
    public Map<String, Integer> vipShop;
    public Map<String, ArenaPlayer> players;
    public AtomicInteger rodada;
    public int coins;
    public int timer;
    public GameState state;
    public Location spawn;
    
    public Arena(int coins) {
        this.vipShop = Collections.synchronizedMap(new HashMap<String, Integer>());
        this.players = Collections.synchronizedMap(new HashMap<String, ArenaPlayer>());
        this.rodada = new AtomicInteger(0);
        this.coins = 15;
        this.timer = 900;
        this.state = GameState.WAITING;
        this.coins = coins;
        this.spawn = Main.level.startSpawn;
        spawn.setWorld(Bukkit.getWorld("arena_1"));
        final FileConfiguration config = Main.theInstance().config;
        this.timer = config.getInt("configuration.time");
        
    }
    
    public void joinPlayer(final Player player) {
        if (this.state == GameState.WAITING) {
            this.state = GameState.RUNNING;
            this.startGame();
            this.startTask();
        }
        TagAPI.apply("", player);
        final ArenaPlayer dp = new ArenaPlayer();
        dp.nick = player.getName();
        GameManager.playing.put(player.getName(), this);
        this.players.put(player.getName().toLowerCase(), dp);
        final FileConfiguration config = Main.theInstance().config;
        player.teleport(spawn);
        
        
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	player.teleport(spawn);
            	TagAPI.apply("", player);
            }
        }, 2L);
        
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).addUnsafeEnchantment(Enchantment.DURABILITY, 4).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build());
        player.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build());
        player.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.DIAMOND_SWORD).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.BOW).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5).addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.ARROW).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.GOLDEN_APPLE, 15).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.COOKED_BEEF, 64).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 8, (short)8193) });
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 8, (short)8194) });
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 8, (short)8297) });
        player.getInventory().setItem(8, ShopMenu.shop);
        player.getInventory().addItem(new ItemStack[] { ShopMenu.vip });
        for (final PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
        if (!this.vipShop.containsKey(player.getName())) {
            this.vipShop.put(player.getName(), 0);
        }
        for (String str : config.getStringList("messages.welcome")) {
            str = str.replace("&", "§").replace("{money}", String.valueOf(this.coins)).replace("{count-players}", String.valueOf(this.getPlayers().size()));
            player.sendMessage(str);
        }
        this.updateScoreboard();
    }
    
    public void startGame() {
        final FileConfiguration config = Main.theInstance().config;
        for (String str : config.getStringList("messages.game-start.message")) {
            str = str.replace("&", "§").replace("{0}", String.valueOf(this.coins));
            this.broadcast(str);
        }
        spawn.getWorld().setTime(20000L);
    }
    
    public void stop() {
        final FileConfiguration config = Main.theInstance().config;
        for (final Player p : this.getPlayers()) {
            p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
        for (final ArenaPlayer dp : this.players.values()) {
            this.remove(dp);
        }
        this.vipShop.clear();
        this.players.clear();
        this.timer = config.getInt("configuration.time");
    }
    
    public void remove(final ArenaPlayer dp) {
        this.players.remove(dp.nick.toLowerCase());
        GameManager.playing.remove(dp.nick);
        PlayerManager.fromNick(dp.nick).save();
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                final Player p = Bukkit.getPlayer(dp.nick);
                if (p != null) {
                    GameManager.sendToLobby(p);
                }
            }
        }, 40L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                Bukkit.spigot().restart();
            }
        }, 45L);
    }
    
    public ArenaPlayer getPlayer(final String p) {
        if (!this.players.containsKey(p.toLowerCase())) {
            return null;
        }
        return this.players.get(p.toLowerCase());
    }
    
    public void broadcast(final String str) {
        for (final Player p : this.getPlayers()) {
            p.sendMessage(str);
        }
    }
    
    public List<Player> getPlayers() {
        final List<Player> list = new ArrayList<Player>();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (this.players.containsKey(p.getName().toLowerCase())) {
                list.add(p);
            }
        }
        return list;
    }
    
    public void updateScoreboard() {
        for (final Player p : this.getPlayers()) {
            ScoreBoard.createScoreBoard(p, this);
        }
    }
    
    public void startTask() {
        final FileConfiguration config = Main.theInstance().config;
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                final List<Player> all = Arena.this.getPlayers();
                if (all.size() == 0) {
                    return;
                }
                final Arena this$0 = Arena.this;
                --this$0.timer;
                final String bossBarFormat =  MinigameConfig.BOSSBAR.replace("{0}", Methods.formatHHMMSS(Arena.this.timer)).replace("&", "§");
                //System.out.println(bossBarFormat);
                for (final Player p : all) {
                	ActionBarAPI.sendActionBar(p, bossBarFormat);
                }
                if (Arena.this.timer <= 0 && Arena.this.players.size() > 1) {
                    for (final Player p : all) {
                        final PlayerData fromNick;
                        final PlayerData pd = fromNick = PlayerManager.fromNick(p.getName());
                        ++fromNick.partidas;
                    }
                    final List<ArenaPlayer> keys = new ArrayList<ArenaPlayer>(Arena.this.players.values());
                    Collections.sort(keys, new Comparator<ArenaPlayer>() {
                        @Override
                        public int compare(final ArenaPlayer c1, final ArenaPlayer c2) {
                            final Double o1 = (double)c1.kills.get();
                            final Double o2 = (double)c2.kills.get();
                            return o2.compareTo(o1);
                        }
                    });
                    final ArenaPlayer winner = keys.get(0);
                    final PlayerData fromNick2;
                    final PlayerData pd = fromNick2 = PlayerManager.fromNick(winner.nick);
                    ++fromNick2.winTotal;
                    final Player wP = winner.thePlayer();
                    final int premio = config.getInt("configuration.money-on-win");
                    Main.theInstance().economy.depositPlayer((OfflinePlayer)wP, (double)premio);
                    if (wP != null) {
                        wP.sendMessage(config.getString("messages.win.other").replace("&", "§").replace("{0}", String.valueOf(premio)));
                    }
                    Arena.this.broadcast(config.getString("messages.win.other").replace("&", "§").replace("{0}", pd.nick));
                    for (final Player p2 : all) {
                        Arena.this.remove(Arena.this.getPlayer(p2.getName()));
                    }
                    Arena.this.stop();
                }
            }
        }, 0L, 20L);
    }
}
