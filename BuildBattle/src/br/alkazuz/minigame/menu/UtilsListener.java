package br.alkazuz.minigame.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import br.alkazuz.minigame.game.Arena;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.main.Main;

public class UtilsListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        for (Round r : Main.theInstance().rounds) {
            if (r.hasPlayer(p)) {
                if (item != null && item.isSimilar(UtilsMenu.util)) {
                    event.setCancelled(true);
                    UtilsMenu.openMain(p);
                }
            }
        }
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player)event.getWhoClicked();
        int slot = event.getSlot();
        if (event.getInventory().getTitle().equals("Utilidades")) {
        	if(event.getClickedInventory() instanceof PlayerInventory) return;
        	event.setCancelled(true);
            for (Round r : Main.theInstance().rounds) {
                if (r.hasPlayer(p)) {
                    Arena arena = r.players.get(p);
                    if(slot == 0) {
                    	ItemStack itemCursor = event.getCursor();
                    	if(itemCursor == null || itemCursor.getType() == Material.AIR) return;
                    	if(!itemCursor.getType().isBlock()) {
                    		p.sendMessage("§cVocê só pode alterar o chão com blocos.");
                    		return;
                    	}
                    	arena.setFloorBlock(itemCursor.getType(), (byte) itemCursor.getDurability());
                    	p.sendMessage("§aChão alterado com sucesso!");
                    	itemCursor.setType(Material.AIR);
                    }else if(slot == 1) {
                    	me.arcaniax.hdb.Main.hdbm.openMainInventory(p);
                    }
                    
                }
            }
        }
    }
}
