package br.alkazuz.vip.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemFactory
{
    public static ItemStack fillerItem;
    
    public static ItemStack createColored(String oldMaterialName, byte data, String displayName, String... lore) {
        ItemStack itemStack = new MaterialData(Material.valueOf(oldMaterialName), data).toItemStack(1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        if (lore != null) {
            List<String> finalLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
            for (String s : lore) {
                if (s != null) {
                    finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            itemMeta.setLore((List)finalLore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static ItemStack rename(ItemStack itemstack, String displayName) {
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(displayName);
        itemstack.setItemMeta(meta);
        return itemstack;
    }
    
    public static ItemStack rename(ItemStack itemstack, String displayName, String... lore) {
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            List<String> finalLore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
            for (String s : lore) {
                if (s != null) {
                    finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            meta.setLore((List)finalLore);
        }
        itemstack.setItemMeta(meta);
        return itemstack;
    }
    
    
    
    public static ItemStack createColouredLeather(Material armourPart, int red, int green, int blue) {
        ItemStack itemStack = new ItemStack(armourPart);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)itemStack.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(red, green, blue));
        itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        return itemStack;
    }
    
    
    public static boolean areSame(ItemStack a, ItemStack b) {
        if (a.getType() != b.getType()) {
            return false;
        }
        if (a.getData().getData() != b.getData().getData()) {
            return false;
        }
        if ((a.hasItemMeta() && !b.hasItemMeta()) || (!a.hasItemMeta() && b.hasItemMeta())) {
            return false;
        }
        if (!a.hasItemMeta() && !b.hasItemMeta()) {
            return true;
        }
        ItemMeta am = a.getItemMeta();
        ItemMeta bm = b.getItemMeta();
        return am.getDisplayName().equalsIgnoreCase(bm.getDisplayName());
    }
    
    public static boolean haveSameName(ItemStack a, ItemStack b) {
        return a.hasItemMeta() && b.hasItemMeta() && a.getItemMeta().hasDisplayName() && b.getItemMeta().hasDisplayName() && a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName());
    }

	public static ItemStack createSkull(String url, String name) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);

        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwner("Notch");
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
