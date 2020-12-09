package br.alkazuz.lobby.menu;

import org.bukkit.entity.*;
import org.bukkit.*;
import br.alkazuz.lobby.object.*;
import org.bukkit.inventory.*;
import java.util.*;

public class Menus
{
    public static void openGames(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)p, 45, "Lista de Minigames");
        for (Servidor servidor : Servidores.servidores) {
            inv.addItem(new ItemStack[] { servidor.getIcon() });
        }
        p.openInventory(inv);
    }
}
