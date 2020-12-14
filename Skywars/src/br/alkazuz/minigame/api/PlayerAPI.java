package br.alkazuz.minigame.api;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerAPI
{
    public static void clearInventory(Player p) {
        p.getInventory().clear();
        p.getInventory().setBoots((ItemStack)null);
        p.getInventory().setChestplate((ItemStack)null);
        p.getInventory().setLeggings((ItemStack)null);
        p.getInventory().setHelmet((ItemStack)null);
    }
    
    public static void clearPotions(Player player) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
    }
    
    public static void resetPlayer(Player player) {
        clearInventory(player);
        clearPotions(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
    }
}
