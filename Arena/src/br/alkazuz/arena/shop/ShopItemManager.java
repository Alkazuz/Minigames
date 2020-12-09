package br.alkazuz.arena.shop;

import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;

public class ShopItemManager
{
    public static List<ShopItem> items;
    
    static {
        ShopItemManager.items = new ArrayList<ShopItem>();
    }
    
    public ShopItemManager() {
        ShopItemManager.items.add(new ShopItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short)1), 5.0, 0));
    }
    
    public static ShopItem bySlot(final int slot) {
        for (final ShopItem item : ShopItemManager.items) {
            if (item.slot == slot) {
                return item;
            }
        }
        return null;
    }
}
