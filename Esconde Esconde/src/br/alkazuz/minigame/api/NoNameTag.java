package br.alkazuz.minigame.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import br.alkazuz.minigame.api.entities.NametagEntity;
import br.alkazuz.minigame.main.Main;

public class NoNameTag {

	private static Map<String, NametagEntity> entities = new HashMap<String, NametagEntity>();
	
	public static void load() {
		
		/*
		 * addCustomEntity((Class<? extends
		 * net.minecraft.server.v1_8_R3.Entity>)NametagEntity.class, "NametagBat", 65);
		 * for (World world : Bukkit.getServer().getWorlds()) { for (Entity
		 * entity : world.getEntities()) { if (entity instanceof NametagEntity) {
		 * entity.remove(); } } }
		 */
	}
	
	public static void unload() {
	}
	
	public static void hideNametag(Player player) {
		ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setMetadata("HideNametag", new FixedMetadataValue(Main.theInstance(), true)); //Optional
        player.setPassenger(stand);
    }
    
    public static void showNametag(Player player) {
    	Entity entity = player.getPassenger();
        if (entity != null &&entity.hasMetadata("HideNametag")) {
            entity.remove();
        }
    }
    
    public static boolean isNametagHidden(Player player) {
         Entity entity = player.getPassenger();
        return (entity != null &&entity.hasMetadata("HideNametag"));
    }
	
}
