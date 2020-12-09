package br.alkazuz.bungee.ban.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.huskehhh.mysql.mysql.MySQL;

import br.alkazuz.bungee.ban.main.Main;
import br.alkazuz.bungee.ban.manager.Bans;

public class MySQLConnection {

	
	public static Connection con;
	public static Statement statement;
	
	public static String getString(String p, String inteiro) {
		try {
			if(!con.isClosed()) {
				MySQL msql = new MySQL(Main.theInstance().configuration.getString("SQL.host"), 
						Main.theInstance().configuration.getString("SQL.port")
						, Main.theInstance().configuration.getString("SQL.database")
						, Main.theInstance().configuration.getString("SQL.user")
						
						, Main.theInstance().configuration.getString("SQL.pass"));
				con = msql.openConnection();
				statement = con.createStatement();
			}
			
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM ips WHERE nick= ?");
            ps.setString(1, p.toLowerCase());
            final ResultSet rs = ps.executeQuery();
            String t = "";
            while (rs.next())
            {
            	t = rs.getString(inteiro);
            }
            
            rs.close();
            ps.close();
            return t;
        }
        catch (Exception ex) {
            return null;
        }
	}
	
	public MySQLConnection() {
		try {
			MySQL msql = new MySQL(Main.theInstance().configuration.getString("SQL.host"), 
					Main.theInstance().configuration.getString("SQL.port")
					, Main.theInstance().configuration.getString("SQL.database")
					, Main.theInstance().configuration.getString("SQL.user")
					
					, Main.theInstance().configuration.getString("SQL.pass"));
			con = msql.openConnection();
			statement = con.createStatement();
			SQLData.CriarTabela();
			
		} catch (Exception e1) {e1.printStackTrace();}
	}
	
}
