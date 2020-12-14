package br.alkazuz.lobby.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.bukkit.Bukkit;

import com.google.gson.Gson;

public class SQLData
{
    public static Connection con;
    public static Statement statement;
    
    public static Object decodePlayerData(String s) {
        Gson gson = new Gson();
        Object data = null;
        try {
        	data = (Object)gson.fromJson(s, Object.class);
        }
        catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("§4[PlayerData] Erro ao decifrar a sequ\u00eancia do PlayerData: " + s);
            Bukkit.getConsoleSender().sendMessage("§c[PlayerData] Erro Principal: " + ex.getCause());
        }
        return data;
    }
    
    public static void execute(String cmd) {
        try {
            PreparedStatement ps = SQLData.con.prepareStatement(cmd);
            ps.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
