package br.alkazuz.minigame.shop;

import org.bukkit.event.player.*;
import br.alkazuz.minigame.main.*;
import br.alkazuz.minigame.game.*;
import org.bukkit.potion.*;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

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
                if (game.state != RoundState.AVAILABLE && item != null && item.isSimilar(ShopItemManager.bySlot(0).item)) {
                    p.sendMessage("§aVoc\u00ea ativou o efeito de velocidade.");
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    this.removeItem(p);
                }
                if (game.state == RoundState.AVAILABLE || item == null || !item.isSimilar(ShopItemManager.bySlot(1).item)) {
                    continue;
                }
                p.sendMessage("§aVoc\u00ea ativou o efeito de super pulo.");
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
            }
        }
    }
    
    private void removeItem(Player p) {
        if (p.getItemInHand().getAmount() < 2) {
            p.setItemInHand(new ItemStack(Material.AIR));
        }
        else {
            ItemStack item = p.getItemInHand();
            item.setAmount(item.getAmount() - 1);
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
