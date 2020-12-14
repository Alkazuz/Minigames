package br.alkazuz.minigame.game;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.alkazuz.minigame.main.Main;
import xyz.xenondevs.particle.ParticleEffect;

public class EffectWin {
	
    private SkywarsPlayer gamePlayer;
    
    public EffectWin(final SkywarsPlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    
    public void playWinEffect() {
    	final Player p = this.gamePlayer.player;
    	p.setAllowFlight(true);
    	p.setFlying(true);
    	p.setFlySpeed(1);
        for (int n = 0; 10 > n; ++n) {
            new BukkitRunnable() {
                public void run() {
                    final Chicken c = spawnChicken(p.getLocation(), random(-0.5, 0.5), random(-0.5, 0.5));
                    c.getLocation().getWorld().playSound(c.getLocation(), Sound.FIREWORK_LAUNCH, 1.0f, 1.0f);
                    new BukkitRunnable() {
                        int time = 20;
                        
                        public void run() {
                            if (this.time == 0) {
                                if (c.isDead()) {
                                    ParticleEffect.NOTE.display(c.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 152, null);
                                    c.getLocation().getWorld().playSound(c.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                                    this.cancel();
                                }
                                else {
                                    ParticleEffect.NOTE.display(c.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 152, null);
                                    c.getLocation().getWorld().playSound(c.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                                    c.remove();
                                }
                            }
                            else {
                                --this.time;
                                if (c.isDead()) {
                                    ParticleEffect.NOTE.display(c.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 152, null);
                                    c.getLocation().getWorld().playSound(c.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                                    this.cancel();
                                }
                                else {
                                    ParticleEffect.NOTE.display(c.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 152, null);
                                }
                            }
                        }
                    }.runTaskTimer((Plugin)Main.theInstance(), 0L, 10L);
                }
            }.runTaskLater((Plugin)Main.theInstance(), (long)(n * 10));
        }
    }
    
    public static double random(final double n, final double n2) {
        return n + ThreadLocalRandom.current().nextDouble() * (n2 - n);
    }
    
    private static Chicken spawnChicken(final Location loc, final double n, final double n3) {
        final Chicken chicken = (Chicken)loc.getWorld().spawn(loc, Chicken.class);
        chicken.setVelocity(new Vector(n, 1.5, n3));
        return chicken;
    }

}
