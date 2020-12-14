package br.alkazuz.bungee.ban.manager;

import java.util.ArrayList;
import java.util.List;

import br.alkazuz.bungee.ban.main.Main;
import br.alkazuz.bungee.ban.manager.Ban.BanType;

public class Punicao {
	public String name;
	public String title;
	public BanType type;
	public String time;
	
	public Punicao(String name,String title, BanType type, String time) {
		this.time = time;
		this.title = title;
		this.type = type;
		this.name = name;
	}
	
	public static List<Punicao> all = new ArrayList<Punicao>();
	public static void load(Main main) {
		for(String ban : main.configuration.getSection("bans").getKeys()) {
			String title = main.configuration.getString("bans."+ban + ".title");
			BanType type = BanType.valueOf(main.configuration.getString("bans."+ban + ".type"));
			String time = main.configuration.getString("bans."+ban + ".time");
			all.add(new Punicao(ban.toLowerCase(), title, type, time));
		}
	}

}
