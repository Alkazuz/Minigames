package br.alkazuz.minigame.game;

import java.util.LinkedHashMap;
import java.util.List;

import br.alkazuz.minigame.main.Main;

public class Themes
{
	public static LinkedHashMap<String, List<String>> themes = new LinkedHashMap<String, List<String>>();
    public static void load(Main main) {
    	for(String category : main.config.getConfigurationSection("temas").getKeys(false)) {
    		themes.put(category, main.config.getStringList("temas."+category));
    	}
    }
}
