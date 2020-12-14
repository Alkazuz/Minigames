package br.alkazuz.arena.shop;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
import br.alkazuz.arena.game.*;
import br.alkazuz.arena.main.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

public class ShopListener implements Listener
{
    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final Player p = event.getPlayer();
        if (GameManager.playing.containsKey(p.getName())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        final Player p = event.getPlayer();
        if (GameManager.playing.containsKey(p.getName()) && item != null && item.isSimilar(ShopMenu.shop)) {
            event.setCancelled(true);
            ShopMenu.open(p);
        }
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player p = (Player)event.getWhoClicked();
        final int slot = event.getSlot();
        if (event.getInventory().getTitle().equals("Loja") && GameManager.playing.containsKey(p.getName())) {
            event.setCancelled(true);
            final Arena game = GameManager.playing.get(p.getName());
            final FileConfiguration config = Main.theInstance().config;
            final ShopItem item = ShopItemManager.bySlot(slot);
            if (item != null) {
                if (p.hasPermission("minigames.vip")) {
                    if (game.vipShop.get(p.getName()) < 3) {
                        game.broadcast(config.getString("messages.shop.vip").replace("{0}", p.getName()).replace("&", "§"));
                        p.getInventory().addItem(new ItemStack[] { item.item });
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                        game.vipShop.put(p.getName(), game.vipShop.get(p.getName()) + 1);
                        ShopMenu.open(p);
                    }
                    else {
                        if (Main.theInstance().economy.getBalance((OfflinePlayer)p) < item.price / 2.0) {
                            p.sendMessage("§cVoc\u00ea n\u00e3o tem dinheiro suficiente :(");
                            p.closeInventory();
                            return;
                        }
                        Main.theInstance().economy.withdrawPlayer((OfflinePlayer)p, item.price / 2.0);
                        game.broadcast(config.getString("messages.shop.normal").replace("{1}", String.valueOf(item.price / 2.0)).replace("{0}", p.getName()).replace("&", "§"));
                        p.getInventory().addItem(new ItemStack[] { item.item });
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                        ShopMenu.open(p);
                    }
                }
                else {
                    if (Main.theInstance().economy.getBalance((OfflinePlayer)p) < item.price) {
                        p.sendMessage("§cVoc\u00ea n\u00e3o tem dinheiro suficiente :(");
                        p.closeInventory();
                        return;
                    }
                    Main.theInstance().economy.withdrawPlayer((OfflinePlayer)p, item.price);
                    game.broadcast(config.getString("messages.shop.normal").replace("&", "§").replace("{1}", String.valueOf(item.price)).replace("{0}", p.getName()));
                    p.getInventory().addItem(new ItemStack[] { item.item });
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                    ShopMenu.open(p);
                }
            }
        }
    }
}
