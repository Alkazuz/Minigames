package br.alkazuz.antiaura.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import br.alkazuz.antiaura.main.Main;
import br.alkazuz.antiaura.utils.AimUtils;

public class EntityAttackListener implements Listener{
	
	public HashMap<Entity, Integer> detections = new HashMap<Entity, Integer>();
	public HashMap<Entity, Rotation> lastrotation = new HashMap<Entity, Rotation>();
	public HashMap<Entity, Location> lastLocation = new HashMap<Entity, Location>();
	public HashMap<Entity, Long> lastHit = new HashMap<Entity, Long>();
	public HashMap<Entity, Long> lastHeadshot = new HashMap<Entity, Long>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAttack(EntityDamageByEntityEvent event) {
		if(event.isCancelled())return;
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			if(lastHit.containsKey(event.getDamager()) && System.currentTimeMillis() - lastHit.get(event.getDamager()) < 500) {
				event.setCancelled(true);
			}
			if(lastHit.containsKey(event.getDamager()) && System.currentTimeMillis() - lastHit.get(event.getDamager()) < 700) {
				
				if(lastHeadshot.containsKey(event.getDamager()) && System.currentTimeMillis() - lastHeadshot.get(event.getDamager()) <= 2000) {
					event.setCancelled(true);
				}
				
				int count = 0;
				if(detections.containsKey(event.getDamager())) {
					count = detections.get(event.getDamager());
				}
				
				if(count == 3) {
					if(!lastHeadshot.containsKey(event.getDamager())){
						lastHeadshot.put(event.getDamager(), System.currentTimeMillis());
					}else {
						if(System.currentTimeMillis() - lastHeadshot.get(event.getDamager()) > 2500) {
							lastHeadshot.put(event.getDamager(), System.currentTimeMillis());
						}
					}
					
				}
				
				if(AimUtils.isLookingHead(event.getDamager(), event.getEntity())) {
					
					if(lastLocation.containsKey(event.getEntity())) {
						Location loc = lastLocation.get(event.getEntity());
						Location currentLoc = event.getEntity().getLocation();
						int time = 8;
						double distance = loc.distance(currentLoc);
						if(distance >= 3D) {
							time = 12 * (int)(distance / 2);
						}
						if(loc.getBlockX() != currentLoc.getBlockX() &&
								loc.getBlockZ() != currentLoc.getBlockZ() &&
								(loc.getBlockY() != currentLoc.getBlockY() + 1 || loc.getBlockY() != currentLoc.getBlockY())) {
							
							count++;
							detections.put(event.getDamager(), count);
							
							//notify("§b[AntiAura]: §e"+((Player) event.getDamager()).getName()+ " §fKillAura violation. §c"+count+"VL §freached, ("+count+"/5) to kick  the player.");
							if(count >= 5) {
								event.setCancelled(true);
								((Player) event.getDamager()).kickPlayer("combat.killaura");
								notify("§b[AntiAura]: §e"+((Player) event.getDamager()).getName()+ " §fwas kicked for Killaura.");
								detections.put(event.getDamager(), 0);
								if(lastHeadshot.containsKey(event.getDamager())){
									lastHeadshot.remove(event.getDamager());
								}
							}
							
							Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
					            @Override
					            public void run() {
					            	int count = detections.get(event.getDamager());
					            	detections.put(event.getDamager(), count - 1);
					            }
					        }, 20L * 8);
						}	
					}
					
				}
				
			}
			
			lastHit.put(event.getDamager(), System.currentTimeMillis());
			lastLocation.put(event.getEntity(), event.getEntity().getLocation());
			lastrotation.put(event.getDamager(), new Rotation(event.getDamager().getLocation().getYaw(), event.getDamager().getLocation().getPitch(), System.currentTimeMillis()));
		}
	}

	public void notify(String msg) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(all.isOp() || all.hasPermission("aa.notify")) {
				all.sendMessage(msg);
			}
		}
	}
	
	public class Rotation{
		float yaw;
		float pitch;
		Long time;
		
		public Rotation(float yaw, float pitch, Long time) {
			this.yaw = yaw;
			this.pitch = pitch;
			this.time = time;
		}
	}
	
}
