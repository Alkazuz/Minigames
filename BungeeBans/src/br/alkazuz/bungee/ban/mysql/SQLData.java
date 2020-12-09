package br.alkazuz.bungee.ban.mysql;

import java.sql.PreparedStatement;

import com.google.gson.Gson;

import br.alkazuz.bungee.ban.manager.Ban;
import br.alkazuz.bungee.ban.manager.Bans;

public class SQLData {
	
	public static void CriarTabela() {
        try {
            PreparedStatement ps = MySQLConnection.con.prepareStatement("CREATE TABLE IF NOT EXISTS `minigames_bans` (`nick` VARCHAR(35) NULL,`object` TEXT NOT NULL);");
            ps.executeUpdate();
            Bans.load();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	public static String encodeBan(Ban ban) {
        Gson gson = new Gson();
        String json = "";
        try {
            json = gson.toJson((Object)ban);
        }
        catch (Exception ex) {
           System.out.println("§c[Ban] Erro ao fazer o encode do Ban");
        }
        return json;
    }
	
	public static Ban decodePlayerData(String s) {
        Gson gson = new Gson();
        Ban ban = null;
        try {
        	ban = (Ban)gson.fromJson(s, Ban.class);
        }
        catch (Exception ex) {
        	System.out.println("§4[Ban] Erro ao decifrar a sequ\u00eancia do Ban: " + s);
            System.out.println("§c[Ban] Erro Principal: " + ex.getCause());
        }
        return ban;
    }
	
	public static void execute(String cmd) {
        try {
            PreparedStatement ps = MySQLConnection.con.prepareStatement(cmd);
            ps.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	
}
