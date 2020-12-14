package br.alkazuz.spigot.addons.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.huskehhh.mysql.mysql.MySQL;

import br.alkazuz.spigot.addons.main.Main;
import br.alkazuz.spigot.addons.main.SpigotConfig;
import br.alkazuz.spigot.addons.object.SpigotGroup;
import br.alkazuz.spigot.addons.object.SpigotGroupManager;

public class MySQLConnection {

	
	public static Connection con;
	public static Statement statement;
	
	public MySQLConnection() {
		try {
			MySQL msql = new MySQL(Main.theInstance().config.getString("SQL.host"), 
					Main.theInstance().config.getString("SQL.port")
					, Main.theInstance().config.getString("SQL.database")
					, Main.theInstance().config.getString("SQL.user")
					
					, Main.theInstance().config.getString("SQL.pass"));
			con = msql.openConnection();
			statement = con.createStatement();
			SpigotGroupManager.groups.clear();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SpigotConfig.GROUP_TABLE+" ORDER BY priority DESC");
            final ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
            	String group = rs.getString("name");
            	String permission = rs.getString("permission");
            	String prefix = rs.getString("prefix").replace("&", "§");
            	String suffix = rs.getString("suffix").replace("&", "§");
            	int priority = rs.getInt("priority");
                SpigotGroup bungeeGroup = new SpigotGroup(group, prefix, suffix, priority);
                SpigotGroupManager.groups.put(permission, bungeeGroup);
                if(group.equals(SpigotConfig.GROUP_DEFAULT)) {
                	SpigotGroupManager.defaultGroup = bungeeGroup;
                }
            }
            
            rs.close();
            ps.close();
			
		} catch (Exception e1) {e1.printStackTrace();}
	}
	
}
