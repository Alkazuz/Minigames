package br.alkazuz.minigame.shop;

import org.bukkit.inventory.*;

public class ShopItem
{
    public ItemStack item;
    public double price;
    public int slot;
    
    public ShopItem(ItemStack item, double price, int slot) {
        this.item = item;
        this.price = price;
        this.slot = slot;
    }
}
