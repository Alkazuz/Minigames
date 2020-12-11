package br.alkazuz.minigame.shop;

import org.bukkit.*;
import br.alkazuz.minigame.utils.*;
import java.util.*;

public class ShopItemManager
{
    public static List<ShopItem> items;
    
    static {
        ShopItemManager.items = new ArrayList<ShopItem>();
    }
    
    public ShopItemManager() {
        ShopItemManager.items.add(new ShopItem(new ItemBuilder(Material.SLIME_BALL).name("§bSegunda chance").build(), 5.0, 0));
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
