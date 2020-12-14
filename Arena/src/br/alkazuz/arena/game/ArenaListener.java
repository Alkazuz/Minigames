package br.alkazuz.arena.game;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import br.alkazuz.arena.api.InvencibleAPI;
import br.alkazuz.arena.main.Main;
import br.alkazuz.arena.shop.ShopMenu;
import br.alkazuz.arena.utils.ItemBuilder;

public class ArenaListener implements Listener
{
    @EventHandler
    public void onMotd(final ServerListPingEvent event) {
        if (Bukkit.hasWhitelist()) {
            event.setMotd("§cManuten\u00e7\u00e3o");
        }
        else if (GameManager.searchMatch() != null) {
            event.setMotd("§aSala disponivel");
            
        }else {
        	event.setMotd("indisponivel");
        }
    }
    
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
    	 Player p = event.getPlayer();
    	Arena arena = GameManager.searchMatch();
    	if (arena == null) {
    		p.sendMessage("§cNenhuma partida encontrada.");
    	}else {
    		arena.joinPlayer(p);
    	}
    }
    
    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        final Player p = event.getPlayer();
        final Arena arena = GameManager.searchMatch();
        if (arena == null && !p.hasPermission("arena.admin")) {
            event.setKickMessage("§cNenhuma partida encontrada.");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }else {
        	p.sendMessage("§cNenhuma partida encontrada.");
        }
        arena.joinPlayer(p);
    }
    
    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        if (cmd.contains(" ")) {
            cmd = cmd.split(" ")[0];
        }
        if (cmd.equalsIgnoreCase("/spawn") || cmd.equalsIgnoreCase("/home") || cmd.equalsIgnoreCase("/lobby")) {
            final Player p = event.getPlayer();
            event.setCancelled(true);
            if (GameManager.playing.containsKey(p.getName())) {
                final ByteArrayOutputStream b = new ByteArrayOutputStream();
                final DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                p.sendPluginMessage((Plugin)Main.theInstance(), "BungeeCord", b.toByteArray());
            }
            else {
                p.sendMessage("§cServidor indispon\u00edvel, tente novamente mais tarde.");
            }
        }
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player p = event.getEntity();
        event.setDeathMessage((String)null);
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        if (GameManager.playing.containsKey(p.getName())) {
            final Arena game = GameManager.playing.get(p.getName());
            final FileConfiguration config = Main.theInstance().config;
            game.broadcast(config.getString("messages.killed").replace("&", "§").replace("{1}", p.getKiller().getName()).replace("{0}", p.getName()));
            InvencibleAPI.addPlayer(p, 5L);
            game.getPlayer(p.getKiller().getName()).kills.getAndIncrement();
            game.updateScoreboard();
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    p.spigot().respawn();
                    p.teleport(Main.level.startSpawn);
                    ItemStack[] contents;
                    for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
                        final ItemStack items = contents[i];
                        if (items != null && items.getType() != Material.GOLDEN_APPLE && items.getDurability() != 1) {
                            p.getInventory().removeItem(new ItemStack[] { items });
                        }
                    }
                    p.getInventory().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build());
                    p.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).addUnsafeEnchantment(Enchantment.DURABILITY, 4).build());
                    p.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build());
                    p.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build());
                    p.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.DIAMOND_SWORD).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5).build() });
                    p.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.BOW).addUnsafeEnchantment(Enchantment.DURABILITY, 4).addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5).addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1).build() });
                    p.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.ARROW).build() });
                    p.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.GOLDEN_APPLE, 15).build() });
                    p.getInventory().addItem(new ItemStack[] { new ItemBuilder(Material.COOKED_BEEF, 64).build() });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 8, (short)8193) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 8, (short)8194) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 8, (short)8297) });
                    p.getInventory().setItem(8, ShopMenu.shop);
                }
            }, 5L);
        }
    }
    
    @EventHandler
    public void onchat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player p = event.getPlayer();
        String msg = event.getMessage();
        String format = "§e[L] §f{0}§e: §e" + msg;
        String send = format.replace("{0}", p.getDisplayName()).replace("{1}", msg.trim());
        p.sendMessage(send);
        for (Entity e : p.getNearbyEntities(30.0, 30.0, 30.0)) {
            if (e instanceof Player) {
                Player d = (Player)e;
                d.sendMessage(send);
            }
        }
    }
    
    @EventHandler
    public void onSpawn(final CreatureSpawnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        if (GameManager.playing.containsKey(p.getName())) {
            final Arena game = GameManager.playing.get(p.getName());
            game.players.remove(p.getName());
        }
    }
    
    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }
}
