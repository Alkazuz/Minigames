package br.alkazuz.vip.gadgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.alkazuz.vip.api.ItemBuilder;
import br.alkazuz.vip.gadget.Gadget;
import br.alkazuz.vip.main.Main;
import br.alkazuz.vip.util.EntityUtil;
import br.alkazuz.vip.util.MathUtils;
import br.alkazuz.vip.util.Particles;
import br.alkazuz.vip.util.SoundUtil;
import br.alkazuz.vip.util.Sounds;
import br.alkazuz.vip.util.UtilParticles;

public class GadgetExplosiveSheep extends Gadget implements Listener{
	
    private ArrayList<Sheep> sheepArrayList = new ArrayList<Sheep>();
    public static List<GadgetExplosiveSheep> EXPLOSIVE_SHEEP = new ArrayList<GadgetExplosiveSheep>();
    public HashMap<Player, Long> coolDown = new HashMap<Player, Long>();
    
	public GadgetExplosiveSheep() {
		super("Ovelha Explosiva", 5.0, new ItemBuilder(Material.SHEARS).name("§aOvelha explosiva").build(), 20);
		Bukkit.getPluginManager().registerEvents(GadgetExplosiveSheep.this, (Plugin)Main.theInstance());
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
		Location location = player.getLocation().add(player.getEyeLocation().getDirection().multiply(0.5));
		location.setY((double)(player.getLocation().getBlockY() + 1));
        Sheep s = (Sheep)player.getWorld().spawn(location, Sheep.class);
        s.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
        s.setNoDamageTicks(100000);
        this.sheepArrayList.add(s);
        EntityUtil.clearPathfinders((Entity)s);
        new SheepColorRunnable(player, 7.0, true, s, this);
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
		Location location = player.getLocation().add(player.getEyeLocation().getDirection().multiply(0.5));
		location.setY((double)(player.getLocation().getBlockY() + 1));
        Sheep s = (Sheep)player.getWorld().spawn(location, Sheep.class);
        s.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
        s.setNoDamageTicks(100000);
        this.sheepArrayList.add(s);
        EntityUtil.clearPathfinders((Entity)s);
        new SheepColorRunnable(player, 7.0, true, s, this);
	}
	
	@EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (this.sheepArrayList.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onShear(EntityDamageEvent event) {
        if (this.sheepArrayList.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }
	
    class SheepColorRunnable extends BukkitRunnable
    {
        private boolean red;
        private double time;
        private Sheep s;
        private GadgetExplosiveSheep gadgetExplosiveSheep;
        private Player player;
        
        public SheepColorRunnable(Player player, double time, boolean red, Sheep s, GadgetExplosiveSheep gadgetExplosiveSheep) {
            this.red = red;
            this.time = time;
            this.s = s;
            this.player = player;
            this.runTaskLater(Main.theInstance(), (long)(int)time);
            this.gadgetExplosiveSheep = gadgetExplosiveSheep;
        }
        
        public void run() {
            if (player == null || !player.isOnline()) {
                this.cancel();
                return;
            }
            if (this.red) {
                this.s.setColor(DyeColor.RED);
            }
            else {
                this.s.setColor(DyeColor.WHITE);
            }
            SoundUtil.playSound(this.s.getLocation(), Sounds.NOTE_STICKS, 1.4f, 1.5f);
            this.red = !this.red;
            this.time -= 0.2;
            if (this.time < 0.5) {
                SoundUtil.playSound(this.s.getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
                UtilParticles.display(Particles.EXPLOSION_HUGE, this.s.getLocation());
                for (int i = 0; i < 50; i++) {
                	if (player == null || !player.isOnline()) {
                        return;
                    }
                    Sheep sheep = player.getWorld().spawn(s.getLocation(), Sheep.class);
                    try {
                        sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    sheep.setMetadata("custom", new FixedMetadataValue(Main.theInstance(),true));
                    Random r = new Random();
                    MathUtils.applyVelocity(sheep, new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                    sheep.setBaby();
                    sheep.setAgeLock(true);
                    sheep.setNoDamageTicks(120);
                    sheepArrayList.add(sheep);
                    EntityUtil.clearPathfinders(sheep);
                    EntityUtil.makePanic(sheep);
                    Bukkit.getScheduler().runTaskLater(Main.theInstance(), () -> {
                        UtilParticles.display(Particles.LAVA, sheep.getLocation(), 5);
                        sheep.remove();
                        EXPLOSIVE_SHEEP.remove(gadgetExplosiveSheep);
                    }, 110);
                }
                sheepArrayList.remove(this.s);
                this.s.remove();
                this.cancel();
            }
            else {
                Bukkit.getScheduler().cancelTask(this.getTaskId());
                new SheepColorRunnable(player,this.time, this.red, this.s, this.gadgetExplosiveSheep);
            }
        }
    }
    
}
