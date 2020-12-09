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
        ShopItemManager.items.add(new ShopItem(new ItemBuilder(Material.FEATHER).name("§bVelocidade").build(), 3.0, 0));
        ShopItemManager.items.add(new ShopItem(new ItemBuilder(Material.IRON_BOOTS).name("§bSuper Pulo").build(), 3.0, 1));
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
