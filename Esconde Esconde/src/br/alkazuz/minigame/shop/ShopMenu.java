package br.alkazuz.minigame.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.utils.ItemBuilder;

public class ShopMenu
{
    public static ItemStack shop;
    public static ItemStack vip;
    
    static {
        ShopMenu.shop = new ItemBuilder(Material.BOOK_AND_QUILL).name("§aLoja").build();
        ShopMenu.vip = new ItemBuilder(Material.DIAMOND).name("§6[§6§lVIP§6]").build();
    }
    
    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 54, "Loja");
        for (Round r : Main.theInstance().rounds) {
            if (r.hasPlayer(p)) {
                if (p.hasPermission("minigames.vip")) {
                    
                }
                else {
                    for (ShopItem item : ShopItemManager.items) {
                        inv.setItem(item.slot, new ItemBuilder(item.item).listLore("§aPre\u00e7o: " + item.price).build());
                    }
                }
            }
        }
        p.openInventory(inv);
    }
}
