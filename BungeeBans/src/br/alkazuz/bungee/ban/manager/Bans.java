package br.alkazuz.bungee.ban.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import br.alkazuz.bungee.ban.mysql.MySQLConnection;
import br.alkazuz.bungee.ban.mysql.SQLData;

public class Bans {
	
	public static HashMap<String, Ban> bans = new HashMap<String, Ban>();
	
	public static void load() {
		try {
            PreparedStatement ps = MySQLConnection.con.prepareStatement("SELECT * FROM `minigames_bans`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nick = rs.getString("nick");
                Ban ban = SQLData.decodePlayerData(rs.getString("object"));
                bans.put(nick.toLowerCase(), ban);
            }
        }
        catch (Exception ex) {}
	}

}
