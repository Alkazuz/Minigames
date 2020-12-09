package br.alkazuz.vip.gadgets;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.alkazuz.vip.api.ItemBuilder;
import br.alkazuz.vip.gadget.Gadget;
import br.alkazuz.vip.main.Main;
import br.alkazuz.vip.util.ItemFactory;
import br.alkazuz.vip.util.Particles;
import br.alkazuz.vip.util.UtilParticles;

public class GadgetGhostParty extends Gadget implements Listener{
	
    public HashMap<Player, Long> coolDown = new HashMap<Player, Long>();
    public Map<Bat, ArmorStand> bats = new HashMap<>();
    
	public GadgetGhostParty() {
		super("Festa dos Fantasmas", 5.0, new ItemBuilder(Material.PRISMARINE_SHARD).name("§aFesta dos Fantasmas").build(), 30);
		Bukkit.getPluginManager().registerEvents(GadgetGhostParty.this, (Plugin)Main.theInstance());
	}
	
	
	@Override
	public void onRightClickBlock(Location loc, Player player) {
		if(coolDown.containsKey(player)) {
			if(coolDown.get(player) > System.currentTimeMillis()) {
				player.sendMessage(String.format("§cVocê precisa esperar mais %ss para usar novamente."
						, String.valueOf((coolDown.get(player) - System.currentTimeMillis()) / 1000L)));
				return;
			}
		}
		coolDown.put(player, System.currentTimeMillis() + (long)(getCooldown() * 1000L));
		
		for (int i = 0; i < 20; i++) {
            Bat bat = player.getWorld().spawn(player.getLocation().add(0, 1, 0), Bat.class);
            ArmorStand ghost = bat.getWorld().spawn(bat.getLocation(), ArmorStand.class);
            ghost.setSmall(true);
            ghost.setGravity(false);
            ghost.setVisible(false);
            
            ghost.setHelmet(ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0", ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Ghost"));
            ghost.setChestplate(ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, 255, 255, 255));
            ghost.setItemInHand(new ItemStack(Material.DIAMOND_HOE));
            bat.setPassenger(ghost);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 1));
            ghost.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
            bat.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
            Bukkit.getScheduler().runTaskLater(Main.theInstance(), () -> {
            	ghost.remove();
            	bat.remove();
            }, 20L * 10L);
            
            bats.put(bat, ghost);
        }
		new GhostsRunnable(player, 40, this);
		
	}
	
	@Override
	public void onRightClick(Location loc, Player player) {
		if(coolDown.containsKey(player)) {
			if(coolDown.get(player) > System.currentTimeMillis()) {
				player.sendMessage(String.format("§cVocê precisa esperar mais %ss para usar novamente."
						, String.valueOf((coolDown.get(player) - System.currentTimeMillis()) / 1000L)));
				return;
			}
		}
		coolDown.put(player, System.currentTimeMillis() + (long)(getCooldown() * 1000L));
		
		for (int i = 0; i < 20; i++) {
            Bat bat = player.getWorld().spawn(player.getLocation().add(0, 1, 0), Bat.class);
            ArmorStand ghost = bat.getWorld().spawn(bat.getLocation(), ArmorStand.class);
            ghost.setSmall(true);
            ghost.setGravity(false);
            ghost.setVisible(false);
            ghost.setHelmet(ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0", ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Ghost"));
            ghost.setChestplate(ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, 255, 255, 255));
            ghost.setItemInHand(new ItemStack(Material.DIAMOND_HOE));
            bat.setPassenger(ghost);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 1));
            ghost.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
            bat.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
            Bukkit.getScheduler().runTaskLater(Main.theInstance(), () -> {
            	ghost.remove();
            	bat.remove();
            }, 20L * 10L);
            
            bats.put(bat, ghost);
        }
		new GhostsRunnable(player, 40, this);
	}
	
	class GhostsRunnable extends BukkitRunnable
    {
        private Player player;
        private int time;
        private GadgetGhostParty gadgetGhostParty;
        
        public GhostsRunnable(Player player, int time, GadgetGhostParty gadgetGhostParty) {
            this.player = player;
            this.runTaskLater(Main.theInstance(), 5L);
            this.gadgetGhostParty = gadgetGhostParty;
            this.time = time;
        }
        
        public void run() {
            if (player == null || !player.isOnline()) {
                this.cancel();
                return;
            }
            
            time--;
            if(time >= 0) {
            	
            	for (Bat bat : bats.keySet())
                    UtilParticles.display(Particles.CLOUD, 0.05f, 0.05f, 0.05f, bat.getLocation().add(0, 1.5, 0), 1);
            	
            	new GhostsRunnable(player, time ,gadgetGhostParty);
            	
            }
        }
    }
	
}
