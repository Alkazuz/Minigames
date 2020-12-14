package br.alkazuz.minigame.game.itens;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import br.alkazuz.minigame.utils.ItemBuilder;

public class SkywarsItem {
	
	public ItemStack item;
	public int chance;
	
	public SkywarsItem(ItemStack item, int chance) {
		this.item = item;
		this.chance = chance;
	}
	
	public static SkywarsItem deserialize(String txt) {
		if(txt.contains(";")) {
			String[] split = txt.split(";");
			// id:data;quantia;enchant:enchantLevel;chance
			if(split.length == 4) {
				ItemStack item = null;
				if(split[0].contains(":")) {
					int id = Integer.valueOf(split[0].split(":")[0]);
					int data = Integer.valueOf(split[0].split(":")[0]);
					item = new ItemStack(id, 1, (short) data);
				}else {
					int id = Integer.valueOf(split[0]);
					item = new ItemStack(id, 1);
				}
				int quantia = Integer.valueOf(split[1]);
				Enchantment enchant = Enchantment.getByName(split[2].split(":")[0]);
				int enchantLevel = Integer.valueOf(split[2].split(":")[1]);
				int chance = Integer.valueOf(split[3]);
				
				return new SkywarsItem(new ItemBuilder(item).setQuantia(quantia).addUnsafeEnchantment(enchant, enchantLevel).build(), chance);
			}
			// id:data;quantia;chance
			if(split.length == 3) {
				ItemStack item = null;
				if(split[0].contains(":")) {
					int id = Integer.valueOf(split[0].split(":")[0]);
					int data = Integer.valueOf(split[0].split(":")[0]);
					item = new ItemStack(id, 1, (short) data);
				}else {
					int id = Integer.valueOf(split[0]);
					item = new ItemStack(id, 1);
				}
				int quantia = Integer.valueOf(split[1]);;
				int chance = Integer.valueOf(split[2]);
				
				return new SkywarsItem(new ItemBuilder(item).setQuantia(quantia).build(), chance);
			}
			if(split.length == 5) {
				int quantia = Integer.valueOf(split[1]);
				PotionType type = PotionType.getByEffect(PotionEffectType.getByName(split[2]));
				int level = Integer.valueOf(split[3]);
				int chance = Integer.valueOf(split[4]);
				
				ItemStack item = new ItemStack(Material.POTION, quantia);
				Potion pot = new Potion(1); //The constructor calls for an (int name), but I'm not sure what that is... I tried 1 and it works fine.
				pot.setType(type);
				try {
					pot.setLevel(level);
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				//pot.setHasExtendedDuration(true);
				pot.setSplash(true);
				pot.apply(item);
				
				return new SkywarsItem(item, chance);
			}
		}
		return null;
	}
	
}
