package br.alkazuz.spigot.addons.main;

public class SpigotConfig {
	
	
	public static String GROUP_TABLE;
	public static String GROUP_DEFAULT;
	public static Main main;
	public static void load(Main main) {
		SpigotConfig.main = main;
		
		GROUP_TABLE = main.config.getString("SQL.tags-table");
		GROUP_DEFAULT = main.config.getString("groups.default-group");
	}

}
