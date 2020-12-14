package br.alkazuz.arena.shop;

import br.alkazuz.arena.utils.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import br.alkazuz.arena.game.*;
import org.bukkit.inventory.*;
import java.util.*;

public class ShopMenu
{
    public static ItemStack shop;
    public static ItemStack vip;
    
    static {
        ShopMenu.shop = new ItemBuilder(Material.BOOK_AND_QUILL).name("§aLoja").build();
        ShopMenu.vip = new ItemBuilder(Material.DIAMOND).name("§6[§6§lVIP§6]").build();
    }
    
    public static void open(final Player p) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)p, 54, "Loja");
        final Arena game = GameManager.playing.get(p.getName());
        if (p.hasPermission("minigames.vip")) {
            if (game.vipShop.get(p.getName()) < 3) {
                for (final ShopItem item : ShopItemManager.items) {
                    inv.setItem(item.slot, new ItemBuilder(item.item.clone()).listLore("§aPre\u00e7o: 0.0").build());
                }
            }
            else {
                for (final ShopItem item : ShopItemManager.items) {
                    inv.setItem(item.slot, new ItemBuilder(item.item.clone()).listLore("§aPre\u00e7o: " + item.price / 2.0).build());
                }
            }
        }
        else {
            for (final ShopItem item : ShopItemManager.items) {
                inv.setItem(item.slot, new ItemBuilder(item.item.clone()).listLore("§aPre\u00e7o: " + item.price).build());
            }
        }
        p.openInventory(inv);
    }
}
