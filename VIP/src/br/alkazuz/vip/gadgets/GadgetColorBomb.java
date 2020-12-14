package br.alkazuz.vip.gadgets;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.alkazuz.vip.api.ItemBuilder;
import br.alkazuz.vip.gadget.Gadget;
import br.alkazuz.vip.main.Main;
import br.alkazuz.vip.util.ItemFactory;
import br.alkazuz.vip.util.MathUtils;
import br.alkazuz.vip.util.Particles;
import br.alkazuz.vip.util.SoundUtil;
import br.alkazuz.vip.util.Sounds;
import br.alkazuz.vip.util.UtilParticles;

public class GadgetColorBomb extends Gadget implements Listener{
	
	private Item bomb;
    private ArrayList<Item> items = new ArrayList<Item>();
    public HashMap<Player, Long> coolDown = new HashMap<Player, Long>();
    
	public GadgetColorBomb() {
		super("Bomba de Cor", 2.5, new ItemBuilder(Material.FIREBALL).name("§aBomba de Cor").build(), 15);
		Bukkit.getPluginManager().registerEvents(GadgetColorBomb.this, (Plugin)Main.theInstance());
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
		ItemStack item = ItemFactory.createColored("WOOL", (byte)MathUtils.random.nextInt(15), Main.theInstance().getItemNoPickupString(), new String[0]);
        Item bomb = player.getWorld().dropItem(player.getEyeLocation(), item);
        bomb.setPickupDelay(50000);
        bomb.setVelocity(player.getEyeLocation().getDirection().multiply(0.7532));
        this.bomb = bomb;
        new BombColorRunnable(player, 14, bomb);
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
		ItemStack item = ItemFactory.createColored("WOOL", (byte)MathUtils.random.nextInt(15), Main.theInstance().getItemNoPickupString(), new String[0]);
        Item bomb = player.getWorld().dropItem(player.getEyeLocation(), item);
        bomb.setPickupDelay(50000);
        bomb.setVelocity(player.getEyeLocation().getDirection().multiply(0.7532));
        this.bomb = bomb;
        new BombColorRunnable(player, 28, bomb);
	}
	
	class BombColorRunnable extends BukkitRunnable
    {
        private Item bomb;
        private Player player;
        private int time;
        private ArrayList<Item> items = new ArrayList<Item>();;
        
        public BombColorRunnable(Player player, int time, Item bomb) {
            this.bomb = bomb;
            this.player = player;
            this.runTaskLater(Main.theInstance(), 5L);
            this.time = time;
        }
        
        public void run() {
            if (player == null || !player.isOnline()) {
                this.cancel();
                return;
            }
            
            Particles effect;
            switch (MathUtils.random.nextInt(5)) {
                default:
                    effect = Particles.FIREWORKS_SPARK;
                    break;
                case 1:
                    effect = Particles.FIREWORKS_SPARK;
                    break;
                case 4:
                    effect = Particles.FLAME;
                    break;
                case 5:
                    effect = Particles.SPELL_WITCH;
                    break;
            }
            
            time--;
            if(time >= 0) {
            	SoundUtil.playSound(bomb.getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
                try {
                UtilParticles.display(effect, bomb.getLocation(), 1, 0.2f);
                    Bukkit.getScheduler().runTask(Main.theInstance(), () -> {
                        if (bomb == null) {
                            return;
                        }
                        ItemStack item = ItemFactory.createColored("WOOL", (byte) MathUtils.random.nextInt(15), Main.theInstance().getItemNoPickupString());
                        Item i = bomb.getWorld().dropItem(bomb.getLocation().add(0, 0.15f, 0), item);
                        i.setPickupDelay(500000);
                        i.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));
                        items.add(i);
                        SoundUtil.playSound(i.getLocation(), Sounds.CHICKEN_EGG_POP, 2f, 1.0f);
                        Bukkit.getScheduler().runTaskLater(Main.theInstance(), () -> {
                            i.remove();
                            items.remove(i);
                            if(time <= 0) {
                            	UtilParticles.display(Particles.EXPLOSION_LARGE, bomb.getLocation(), 1, 0.2f);
                            	bomb.remove();
                            	
                            	this.cancel();
                            }
                        }, 15);
                        new BombColorRunnable(player, time ,bomb);
                    });
                    
                } catch (Exception exc) {
                	exc.printStackTrace();
                }
            }else {
            	
            }
        }
    }
	
}
