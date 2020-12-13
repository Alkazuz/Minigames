package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.alkazuz.minigame.main.Main;
import xyz.xenondevs.particle.ParticleEffect;

public class EffectWin {
	
	private static List<Chicken> chickens;
    private SkywarsPlayer gamePlayer;
    
    public EffectWin(final SkywarsPlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    
    public void playWinEffect() {
        final Player p = this.gamePlayer.player;
        final Location loc = p.getLocation();
        p.teleport(loc.clone().add(0.0, 15.0, 0.0));
        p.setVelocity(new Vector(0, 0, 0));
        for (int i = 0; i < 20; ++i) {
            final Chicken chicken = (Chicken)p.getWorld().spawnEntity(p.getLocation().add(randomDouble(), 3.0, randomDouble()), EntityType.CHICKEN);
            chickens.add(chicken);
            chicken.setLeashHolder((Entity)p);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)Main.theInstance(), () -> checkFalling(p), 5L);
    }
    
    public static void checkFalling(final Player p) {
        new BukkitRunnable() {
            public void run() {
                ParticleEffect.FLAME.display(p.getLocation().add(0.0, 0.5, 0.0), 0.0f, 0.0f, 0.0f, 0.0f, 10, null);
                if (!isNotOnAir(p) && p.getVelocity().getY() < -0.3) {
                    p.setVelocity(new Vector(0.0, 0.1, 0.0));
                }
                else {
                    ParticleEffect.EXPLOSION_HUGE.display(p.getLocation().add(0.0, 0.5, 0.0), 0.0f, 0.0f, 0.0f, 0.0f, 1, null);
                    p.setFallDistance(0.0f);
                    killParachute(p);
                    this.cancel();
                }
            }
        }.runTaskTimer((Plugin)Main.theInstance(), 0L, 80L);
    }
    
    private static void killParachute(final Player p) {
        chickens.forEach(chicken -> {
            chicken.setLeashHolder((Entity)null);
            chicken.remove();
            return;
        });
        p.setVelocity(new Vector(0.0, 0.15, 0.0));
        p.setFallDistance(0.0f);
    }
    
    private static boolean isNotOnAir(final Player p) {
        return p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR;
    }
    
    private static double randomDouble() {
        return (Math.random() < 0.5) ? ((1.0 - Math.random()) * 0.5 + 0.0) : (Math.random() * 0.5 + 0.0);
    }
    
    static {
        chickens = new ArrayList<Chicken>();
    }

}
