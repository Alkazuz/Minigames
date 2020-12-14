package br.alkazuz.arena.shop;

import org.bukkit.inventory.*;

public class ShopItem
{
    public ItemStack item;
    public double price;
    public int slot;
    
    public ShopItem(final ItemStack item, final double price, final int slot) {
        this.item = item;
        this.price = price;
        this.slot = slot;
    }
}
