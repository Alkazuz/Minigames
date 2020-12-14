package br.alkazuz.minigame.shop;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.minigame.utils.*;
import java.util.*;

public class ShopItemManager
{
    public static List<ShopItem> items;
    
    static {
        ShopItemManager.items = new ArrayList<ShopItem>();
    }
    
    public ShopItemManager() {
        ShopItemManager.items.add(new ShopItem(new ItemBuilder(Material.MILK_BUCKET).build(), 3.0, 0));
        ShopItemManager.items.add(new ShopItem(new ItemStack(Material.POTION, 1, (short)8198), 1.0, 9));
        ShopItemManager.items.add(new ShopItem(new ItemStack(Material.POTION, 1, (short)8262), 2.0, 10));
    }
    
    public static ShopItem bySlot(int slot) {
        for (ShopItem item : ShopItemManager.items) {
            if (item.slot == slot) {
                return item;
            }
        }
        return null;
    }
}
