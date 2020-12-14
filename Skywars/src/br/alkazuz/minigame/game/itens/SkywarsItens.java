package br.alkazuz.minigame.game.itens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.alkazuz.minigame.main.Main;

public class SkywarsItens {
	
	public static HashMap<String, ItemInfoSky> ITEMS = new HashMap<String, ItemInfoSky>();
	public static HashMap<String, ItemInfoSky> FEAST = new HashMap<String, ItemInfoSky>();
	public static HashMap<String, ItemInfoSky> MINI_FEAST = new HashMap<String, ItemInfoSky>();
	
	public static void load(Main main) {
		for(String n : main.chest.getConfigurationSection("chests.normal").getKeys(false)) {
			List<SkywarsItem> list = new ArrayList<SkywarsItem>();
			ItemInfoSky info = new ItemInfoSky();
			info.max = main.chest.getInt("chests.normal."+n+".max");
			info.min = main.chest.getInt("chests.normal."+n+".min");
			info.repeat = main.chest.getBoolean("chests.normal."+n+".repetir_itens");
			
			for(String b : main.chest.getStringList("chests.normal."+n+".itens")) {
				SkywarsItem item = SkywarsItem.deserialize(b);
				if(item != null) {
					list.add(item);
				}
			}
			info.itens = list;
			ITEMS.put(n, info);
		}
		
		for(String n : main.chest.getConfigurationSection("chests.mini-feast").getKeys(false)) {
			List<SkywarsItem> list = new ArrayList<SkywarsItem>();
			ItemInfoSky info = new ItemInfoSky();
			info.max = main.chest.getInt("chests.mini-feast."+n+".max");
			info.min = main.chest.getInt("chests.mini-feast."+n+".min");
			info.repeat = main.chest.getBoolean("chests.mini-feast."+n+".repetir_itens");
			
			for(String b : main.chest.getStringList("chests.mini-feast."+n+".itens")) {
				SkywarsItem item = SkywarsItem.deserialize(b);
				if(item != null) {
					list.add(item);
				}
			}
			info.itens = list;
			MINI_FEAST.put(n, info);
		}
		
		for(String n : main.chest.getConfigurationSection("chests.feast").getKeys(false)) {
			List<SkywarsItem> list = new ArrayList<SkywarsItem>();
			ItemInfoSky info = new ItemInfoSky();
			info.max = main.chest.getInt("chests.feast."+n+".max");
			info.min = main.chest.getInt("chests.feast."+n+".min");
			info.repeat = main.chest.getBoolean("chests.feast."+n+".repetir_itens");
			
			for(String b : main.chest.getStringList("chests.feast."+n+".itens")) {
				SkywarsItem item = SkywarsItem.deserialize(b);
				if(item != null) {
					list.add(item);
				}
			}
			info.itens = list;
			FEAST.put(n, info);
		}
		
	}
	
	
}
