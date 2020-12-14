package br.alkazuz.arena.utils;

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
    
    public ItemBuilder(final Material m) {
        this(m, 1);
    }
    
    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }
    
    public ItemBuilder(final Material m, final int quantia) {
        this.is = new ItemStack(m, quantia);
    }
    
    public ItemBuilder(final Material m, final int quantia, final byte durabilidade) {
        this.is = new ItemStack(m, quantia, (short)durabilidade);
    }
    
    public ItemBuilder(final Material m, final int quantia, final int durabilidade) {
        this.is = new ItemStack(m, quantia, (short)durabilidade);
    }
    
    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }
    
    public ItemBuilder setAmount(final int amount) {
        this.is.setAmount(amount);
        final ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setName(final String nome) {
        final ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(nome);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder name(final String nome) {
        final ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(nome);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder addUnsafeEnchantment(final Enchantment ench, final int level) {
        this.is.addUnsafeEnchantment(ench, level);
        return this;
    }
    
    public ItemBuilder setSkullOwner(final String dono) {
        try {
            final SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(dono);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
    
    public ItemBuilder addItemFlag(final ItemFlag flag) {
        final ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(new ItemFlag[] { flag });
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLore(final String... lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore((List)Arrays.asList(lore));
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLore(final List<String> lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore((List)lore);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder listLore(final List<String> lista, final String[] lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore((List)Arrays.asList(lore));
        im.getLore().addAll(lista);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder listLore(final String... lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore((List)Arrays.asList(lore));
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder listLore(final List<String> lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore((List)lore);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setLore(final String string1, final String string2, final String string3, final String string4, final String string5, final String string6, final String string7, final String string8, final String string9, final List<String> lista1, final String string10, final String string11, final String string12, final String string13, final String string14, final List<String> lista2) {
        final ItemMeta im = this.is.getItemMeta();
        final List<String> l = new ArrayList<String>();
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
    
    public ItemBuilder setLeatherArmorColor(final Color cor) {
        try {
            final LeatherArmorMeta im = (LeatherArmorMeta)this.is.getItemMeta();
            im.setColor(cor);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
    
    public ItemStack toItemStack() {
        return this.is;
    }
    
    public static boolean RefSet(final Class<?> sourceClass, final Object instance, final String fieldName, final Object value) {
        try {
            final Field field = sourceClass.getDeclaredField(fieldName);
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            final int modifiers = modifiersField.getModifiers();
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
    
    public static GameProfile createGameProfile(final String texture, final UUID id) {
        final GameProfile profile = new GameProfile(id, (String)null);
        final PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));
        return profile;
    }
    
    public static ItemStack createHead(final String displayName, final String lore, final String texture) {
        final GameProfile profile = createGameProfile(texture, UUID.randomUUID());
        final ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        final ItemMeta headMeta = head.getItemMeta();
        final Class<?> headMetaClass = headMeta.getClass();
        RefSet(headMetaClass, headMeta, "profile", profile);
        head.setItemMeta(headMeta);
        final SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
        skullMeta.setDisplayName(displayName);
        final String[] lines = lore.split("\n");
        final ArrayList<String> Lore = new ArrayList<String>();
        for (int i = 0; i < lines.length; ++i) {
            Lore.add(lines[i]);
        }
        skullMeta.setLore((List)Lore);
        head.setItemMeta((ItemMeta)skullMeta);
        return head;
    }
    
    public ItemBuilder removeAttributes() {
        final ItemMeta meta = this.is.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemStack build() {
        return this.is;
    }
    
    public ItemBuilder durability(final int dur) {
        this.is.setDurability((short)dur);
        return this;
    }
    
    public ItemBuilder owner(final String owner) {
        try {
            final SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(owner);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
}
