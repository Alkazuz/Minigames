package br.alkazuz.minigame.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.minigame.game.Arena;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.utils.ItemBuilder;

public class UtilsMenu
{
    public static ItemStack util = new ItemBuilder(Material.REDSTONE_COMPARATOR).name("§eUtilidades").build();
    
    public static void openMain(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 27, "Utilidades");
        for (Round r : Main.theInstance().rounds) {
            if (r.hasPlayer(p)) {
            	Arena arena = r.players.get(p);
            	inv.setItem(0, new ItemBuilder(arena.floorBlock.getType()).name("§aAlterar o chão").build());
            	inv.setItem(1, new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(p.getName()).name("§aCabeças").build());
            }
        }
        p.openInventory(inv);
    }
}
