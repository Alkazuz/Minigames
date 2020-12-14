package br.alkazuz.vip.gadget;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.vip.gadgets.GadgetColorBomb;
import br.alkazuz.vip.gadgets.GadgetExplosiveSheep;
import br.alkazuz.vip.gadgets.GadgetGhostParty;

public class Gadgets implements Listener{
	
	public static List<Gadget> gadgets = new ArrayList<Gadget>();
	
	public static void load() {
		gadgets.clear();
		gadgets.add(new GadgetExplosiveSheep());
		gadgets.add(new GadgetColorBomb());
		gadgets.add(new GadgetGhostParty());
	}
	
	public static Gadget byItem(ItemStack item) {
		for(Gadget gadget : gadgets) {
			if(gadget.getItem().isSimilar(item)) {
				return gadget;
			}
		}
		return null;
	}
	
	public static Gadget byName(String name) {
		for(Gadget gadget : gadgets) {
			if(ChatColor.stripColor(gadget.getName()).equalsIgnoreCase(name)) {
				return gadget;
			}
		}
		return null;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event) {
		if(event.isCancelled())return;
		ItemStack item = event.getItem();
		if(item == null || item.getType() == Material.AIR)return;
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Gadget gadget = byItem(item);
			if(gadget != null) {
				event.setCancelled(true);
				gadget.onLeftClickBlock(event.getClickedBlock().getLocation(), event.getPlayer());
			}
		}
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Gadget gadget = byItem(item);
			if(gadget != null) {
				event.setCancelled(true);
				gadget.onRightClickBlock(event.getClickedBlock().getLocation(), event.getPlayer());
			}
		}
		if(event.getAction() == Action.LEFT_CLICK_AIR) {
			Gadget gadget = byItem(item);
			if(gadget != null) {
				event.setCancelled(true);
				gadget.onLeftClick(event.getPlayer().getLocation(), event.getPlayer());
			}
		}
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Gadget gadget = byItem(item);
			if(gadget != null) {
				event.setCancelled(true);
				gadget.onRightClick(event.getPlayer().getLocation(), event.getPlayer());
			}
		}
	}
	
}
