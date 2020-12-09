package br.alkazuz.minigame.shop;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.main.Main;

public class ShopListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        for (Round r : Main.theInstance().rounds) {
            if (r.hasPlayer(p)) {
                Round game = r;
                if (item != null && item.isSimilar(ShopMenu.shop)) {
                    event.setCancelled(true);
                    ShopMenu.open(p);
                }
            }
        }
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player)event.getWhoClicked();
        int slot = event.getSlot();
        if (event.getInventory().getTitle().equals("Loja")) {
            for (Round r : Main.theInstance().rounds) {
                if (r.hasPlayer(p)) {
                    event.setCancelled(true);
                    Round game = r;
                    FileConfiguration config = Main.theInstance().config;
                    ShopItem item = ShopItemManager.bySlot(slot);
                    if (item == null) {
                        continue;
                    }
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
}
