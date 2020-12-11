package br.alkazuz.bungee.addons.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.huskehhh.mysql.mysql.MySQL;

import br.alkazuz.bungee.addons.main.BungeeConfig;
import br.alkazuz.bungee.addons.main.Main;
import br.alkazuz.bungee.addons.object.BungeeGroup;
import br.alkazuz.bungee.addons.object.BungeeGroupManager;

public class MySQLConnection {

	
	public static Connection con;
	public static Statement statement;
	
	public MySQLConnection() {
		try {
			MySQL msql = new MySQL(Main.theInstance().configuration.getString("SQL.host"), 
					Main.theInstance().configuration.getString("SQL.port")
					, Main.theInstance().configuration.getString("SQL.database")
					, Main.theInstance().configuration.getString("SQL.user")
					
					, Main.theInstance().configuration.getString("SQL.pass"));
			con = msql.openConnection();
			statement = con.createStatement();
			BungeeGroupManager.groups.clear();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM '"+BungeeConfig.GROUP_TABLE+"' ORDER BY priority DESC");
            final ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
            	String group = rs.getString("name");
            	String permission = rs.getString("permission");
            	String prefix = rs.getString("prefix").replace("&", "§");
            	String suffix = rs.getString("suffix").replace("&", "§");
            	int priority = rs.getInt("priority");
                BungeeGroup bungeeGroup = new BungeeGroup(group, prefix, suffix, priority);
                BungeeGroupManager.groups.put(permission, bungeeGroup);
                if(group.equals(BungeeConfig.GROUP_DEFAULT)) {
                	BungeeGroupManager.defaultGroup = bungeeGroup;
                }
            }
            
            rs.close();
            ps.close();
			
		} catch (Exception e1) {e1.printStackTrace();}
	}
	
}
