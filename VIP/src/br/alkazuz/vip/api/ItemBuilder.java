package br.alkazuz.vip.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class ItemBuilder
{
    private ItemStack is;
    
    public ItemBuilder(Material m) {
        this(m, 1);
    }
    
    public ItemBuilder(ItemStack is) {
        this.is = is;
    }
    
    public ItemBuilder(Material m, int quantia) {
        this.is = new ItemStack(m, quantia);
    }
    
    public ItemBuilder(Material m, int quantia, byte durabilidade) {
        this.is = new ItemStack(m, quantia, (short)durabilidade);
    }
    
    public ItemBuilder(Material m, int quantia, int durabilidade) {
        this.is = new ItemStack(m, quantia, (short)durabilidade);
    }
    
    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }
    
    public ItemBuilder setAmount(int amount) {
        this.is.setAmount(amount);
        ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setName(String nome) {
        ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(nome);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder name(String nome) {
        ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(nome);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        this.is.addUnsafeEnchantment(ench, level);
        return this;
    }
    
    public ItemBuilder setSkullOwner(String dono) {
        try {
            SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(dono);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
    
    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(new ItemFlag[] { flag });
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLore(String... lore) {
        ItemMeta im = this.is.getItemMeta();
        im.setLore((List)Arrays.asList(lore));
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = this.is.getItemMeta();
        im.setLore((List)lore);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder listLore(List<String> lista, String[] lore) {
        ItemMeta im = this.is.getItemMeta();
        im.setLore((List)Arrays.asList(lore));
        im.getLore().addAll(lista);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder listLore(String... lore) {
        ItemMeta im = this.is.getItemMeta();
        im.setLore((List)Arrays.asList(lore));
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder listLore(List<String> lore) {
        ItemMeta im = this.is.getItemMeta();
        im.setLore((List)lore);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLore(String string1, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, List<String> lista1, String string10, String string11, String string12, String string13, String string14, List<String> lista2) {
        ItemMeta im = this.is.getItemMeta();
        List<String> l = new ArrayList<String>();
        l.add(string1);
        l.add(string2);
        l.add(string3);
        l.add(string4);
        l.add(string5);
        l.add(string6);
        l.add(string7);
        l.add(string8);
        l.add(string9);
        l.addAll(lista1);
        l.add(string10);
        l.add(string11);
        l.add(string12);
        l.add(string13);
        l.add(string14);
        l.addAll(lista2);
        im.setLore((List)l);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLeatherArmorColor(Color cor) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta)this.is.getItemMeta();
            im.setColor(cor);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
    
    public ItemStack toItemStack() {
        return this.is;
    }
    
    public static boolean RefSet(Class<?> sourceClass, Object instance, String fieldName, Object value) {
        try {
            Field field = sourceClass.getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            int modifiers = modifiersField.getModifiers();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if ((modifiers & 0x10) == 0x10) {
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & 0xFFFFFFEF);
            }
            try {
                field.set(instance, value);
            }
            finally {
                if ((modifiers & 0x10) == 0x10) {
                    modifiersField.setInt(field, modifiers | 0x10);
                }
                if (!field.isAccessible()) {
                    field.setAccessible(false);
                }
            }
            if ((modifiers & 0x10) == 0x10) {
                modifiersField.setInt(field, modifiers | 0x10);
            }
            if (!field.isAccessible()) {
                field.setAccessible(false);
            }
            if ((modifiers & 0x10) == 0x10) {
                modifiersField.setInt(field, modifiers | 0x10);
            }
            if (!field.isAccessible()) {
                field.setAccessible(false);
            }
            if ((modifiers & 0x10) == 0x10) {
                modifiersField.setInt(field, modifiers | 0x10);
            }
            if (!field.isAccessible()) {
                field.setAccessible(false);
            }
            return true;
        }
        catch (Exception var11) {
            Bukkit.getLogger().log(Level.WARNING, "Unable to inject Gameprofile", var11);
            return false;
        }
    }
    
    public static GameProfile createGameProfile(String texture, UUID id) {
        GameProfile profile = new GameProfile(id, (String)null);
        PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));
        return profile;
    }
    
    public static ItemStack createHead(String displayName, String lore, String texture) {
        GameProfile profile = createGameProfile(texture, UUID.randomUUID());
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        RefSet(headMetaClass, headMeta, "profile", profile);
        head.setItemMeta(headMeta);
        SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
        skullMeta.setDisplayName(displayName);
        String[] lines = lore.split("\n");
        ArrayList<String> Lore = new ArrayList<String>();
        for (int i = 0; i < lines.length; ++i) {
            Lore.add(lines[i]);
        }
        skullMeta.setLore((List)Lore);
        head.setItemMeta((ItemMeta)skullMeta);
        return head;
    }
    
    public ItemBuilder removeAttributes() {
        ItemMeta meta = this.is.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemStack build() {
        return this.is;
    }
    
    public ItemBuilder durability(int dur) {
        this.is.setDurability((short)dur);
        return this;
    }
    
    public ItemBuilder owner(String owner) {
        try {
            SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(owner);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
}
