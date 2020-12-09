package br.alkazuz.vip.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.vip.api.ItemBuilder;
import br.alkazuz.vip.api.MorphAPI;
import br.alkazuz.vip.api.PT_BR;
import br.alkazuz.vip.api.TagAPI;
import br.alkazuz.vip.gadget.Gadget;
import br.alkazuz.vip.gadget.Gadgets;
import br.alkazuz.vip.main.Main;

public class VIPMenu implements Listener
{
    public static ItemStack vip;
    
    static {
        VIPMenu.vip = new ItemBuilder(Material.DIAMOND).name("§6[§6§lVIP§6]").build();
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        if (item != null && item.isSimilar(VIPMenu.vip)) {
            event.setCancelled(true);
            if (!p.hasPermission("minigames.vip")) {
                p.sendMessage("§cEsta fun\u00e7\u00e3o \u00e9 exclusiva para VIPs.");
                return;
            }
            openMain(p);
        }
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player)event.getWhoClicked();
        int slot = event.getSlot();
        ItemStack item = event.getCurrentItem();
        if (event.getInventory().getTitle().equals("§6§lVIP") && item != null) {
            event.setCancelled(true);
            if (item.getType() == Material.MONSTER_EGG) {
                openMorph(p);
            }
            if (item.getType() == Material.GLASS) {
                openHat(p);
            }
            if (item.getType() == Material.NETHER_STAR) {
            	openGaets(p);
            }
        }
        
        if (event.getInventory().getTitle().equals("Selecione o Gadget")) {
            event.setCancelled(true);
            if(item != null) {
            	Gadget gadget = Gadgets.byName(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
            	if(gadget != null) {
            		if (Main.theInstance().economy.getBalance((OfflinePlayer)p) < gadget.getPrice()) {
                        p.sendMessage("§cVoc\u00ea n\u00e3o dinheiro suficiente.");
                        return;
                    }
            		p.sendMessage("§aAproveite seu novo gadget :)");
            		p.getInventory().addItem(gadget.getItem());
            		p.closeInventory();
            		 Main.theInstance().economy.withdrawPlayer((OfflinePlayer)p, gadget.getPrice());
            	}
            }
        }
        
        
        if (event.getInventory().getTitle().equals("Selecione o Mob")) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().equals("Selecione o Bloco") && item != null) {
            event.setCancelled(true);
            if (Main.theInstance().economy.getBalance((OfflinePlayer)p) < 1.0) {
                p.sendMessage("§cVoc\u00ea n\u00e3o dinheiro suficiente.");
                return;
            }
            p.sendMessage("§aAproveite seu novo chap\u00e9u :)");
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            p.getWorld().playEffect(p.getLocation(), Effect.EXPLOSION_LARGE, 100);
            p.getInventory().setHelmet(item);
            p.closeInventory();
            Main.theInstance().economy.withdrawPlayer((OfflinePlayer)p, 1.0);
        }
        if (event.getInventory().getTitle().equals("Selecione o Mob") && item != null) {
            event.setCancelled(true);
            List<EntityType> all = new ArrayList<EntityType>(MorphAPI.morphs.keySet());
            EntityType selected = all.get(slot);
            if (Main.theInstance().economy.getBalance((OfflinePlayer)p) < 5.0) {
                p.sendMessage("§cVoc\u00ea n\u00e3o dinheiro suficiente.");
                return;
            }
            MorphAPI.morphPlayer(p, selected);
            p.sendMessage("§3» §bAgora todos est\u00e3o te vendo como um §6" + PT_BR.get().translateMob(selected) + "§b.");
            Main.theInstance().economy.withdrawPlayer((OfflinePlayer)p, 5.0);
            TagAPI.apply("", p);
        }
    }
    
    public static void openMain(Player p) {
        FileConfiguration config = Main.theInstance().config;
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 27, "§6§lVIP");
        if (config.getBoolean("morph")) {
            ItemStack stack = new ItemStack(Material.MONSTER_EGG, 1, EntityType.WOLF.getTypeId());
            inv.addItem(new ItemStack[] { new ItemBuilder(stack).name("§eMorfar").listLore("§fSe transforme em v\u00e1rios mobs.").build() });
        }
        if (config.getBoolean("hat")) {
            ItemStack stack = new ItemStack(Material.GLASS);
            inv.addItem(new ItemStack[] { new ItemBuilder(stack).name("§eCapacete").listLore("§fSelecione um bloco para colcoar na cabe\u00e7a.").build() });
        }
        if (config.getBoolean("gadget")) {
        	inv.addItem(new ItemBuilder(Material.NETHER_STAR).name("§aGadgets").listLore("§fQue tal um pouco de diversão?").build());
        }
        p.openInventory(inv);
    }
    
    public static void openHat(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 27, "Selecione o Bloco");
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.GLASS)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.BEACON)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.BEDROCK)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.STONE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.COBBLESTONE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.TNT)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.LEAVES)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.LAPIS_BLOCK)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.LAPIS_ORE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.DIAMOND_BLOCK)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.DIAMOND_ORE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.IRON_ORE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.IRON_BLOCK)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.ENDER_STONE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE)).listLore("§aPre\u00e7o: 1.0").build() });
        inv.addItem(new ItemStack[] { new ItemBuilder(new ItemStack(Material.SLIME_BLOCK)).listLore("§aPre\u00e7o: 1.0").build() });
        p.openInventory(inv);
    }
    
    public static void openGaets(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 27, "Selecione o Gadget");
        for (Gadget g : Gadgets.gadgets) {
        	ItemStack item = g.getItem().clone();
            inv.addItem(new ItemStack[] { new ItemBuilder(item).listLore("§aPre\u00e7o: "+g.getPrice()).build() });
        }
        p.openInventory(inv);
    }
    
    
    public static void openMorph(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 27, "Selecione o Mob");
        for (EntityType all : MorphAPI.morphs.keySet()) {
            ItemStack stack = new ItemStack(Material.MONSTER_EGG, 1, all.getTypeId());
            inv.addItem(new ItemStack[] { new ItemBuilder(stack).listLore("§aPre\u00e7o: 5.0").name(PT_BR.get().translateMob(all)).build() });
        }
        p.openInventory(inv);
    }
}
