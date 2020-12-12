package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import br.alkazuz.minigame.game.itens.ItemInfoSky;
import br.alkazuz.minigame.game.itens.SkywarsItem;
import br.alkazuz.minigame.game.itens.SkywarsItens;
import br.alkazuz.minigame.utils.Utils;

public class Arena {
	
	public Player player;
	public Round round;
	public Location spawn;
	public Cage cage;
	public List<Chest> chests = new ArrayList<Chest>();
	
	public Arena(Player player,Location spawn, Round round) {
		this.spawn = spawn;
		this.round= round;
		this.player = player;
		this.spawn.setWorld(round.level.world);
		this.cage = new Cage(spawn);
		cage.build();
		loadChests();
	}
	
	private void loadChests() {
		System.out.println("carregando baús...");
		for(int y = -20; y <= 20; y++) {
			for(int x = -30; x < 30; x++) {
				for(int z = -30; z <= 30; z++) {
					if(chests.size() >= 3) break;
					Block block = spawn.getWorld().getBlockAt(spawn.clone().add(x, y, z));
					if( block != null && block.getType() == Material.CHEST) {
						chests.add((Chest) block.getState());
					}
				}
			}
		}
		System.out.println(chests.size() + " baús encontrados.");
		refill();
	}
	
	private int emptySlot(Inventory inv) {
		List<Integer> empts = new ArrayList<Integer>();
		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(0) == null) empts.add(i);
		}
		if(empts.size() > 0) {
			return empts.get(new Random().nextInt(empts.size()));
		}
		return 0;
	}
	
	public void refill() {
		for(String key : SkywarsItens.ITEMS.keySet()) {
			ItemInfoSky info = SkywarsItens.ITEMS.get(key);
			List<SkywarsItem> itens = new ArrayList<SkywarsItem>(info.itens);
			List<SkywarsItem> repetido = new ArrayList<SkywarsItem>();
			
			Iterator<SkywarsItem> iterator = itens.iterator();
			int count = Utils.randInt(info.min, info.max);
			int i = 0;
			while(iterator.hasNext() && i < count) {
				SkywarsItem skyItem = iterator.next();
				if(repetido.contains(skyItem) && !info.repeat)continue;
				if(Utils.percent(skyItem.chance)) {
					Chest chest = chests.get(new Random().nextInt(chests.size()));
					chest.getInventory().setItem(emptySlot(chest.getInventory()), skyItem.item);
					i++;
					repetido.add(skyItem);
				}
			}
		}
	}

	public class Cage {
		public Location loc;
		public Cage(Location loc) {
			this.loc = loc;
		}
		
		public void destroy() {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					for(int y = -1; y <= 2; y++) {
						loc.getWorld().getBlockAt(loc.clone().add(x, y, z)).setType(Material.AIR);
					}
				}
			}
		}
		
		public void build() {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					for(int y = -1; y <= 2; y++) {
						loc.getWorld().getBlockAt(loc.clone().add(x, y, z)).setType(Material.STAINED_GLASS);
						loc.getWorld().getBlockAt(loc.clone().add(x, y, z)).setData((byte) 0);
					}
				}
			}
			loc.getWorld().getBlockAt(loc.clone().add(0, 1, 0)).setType(Material.AIR);
			loc.getWorld().getBlockAt(loc.clone()).setType(Material.AIR);
			System.out.println("cage carregado.");
		}
		
	}
	
}
