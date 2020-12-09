package br.alkazuz.minigame.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.bukkit.Bukkit;

import com.google.gson.Gson;

import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.game.MinigameConfig;

public class SQLData
{
    public static Connection con;
    public static Statement statement;
    
    public static void CriarTabela() {
        try {
            PreparedStatement ps = SQLData.con.prepareStatement("CREATE TABLE IF NOT EXISTS `"+MinigameConfig.NAME.toLowerCase()+"_data` (`nick` VARCHAR(35) NULL,`object` TEXT NOT NULL);");
            ps.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static String encodePlayerData(PlayerData PlayerData) {
        Gson gson = new Gson();
        String json = "";
        try {
            json = gson.toJson((Object)PlayerData);
        }
        catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("§c[PlayerData] Erro ao fazer o encode do PlayerData");
        }
        return json;
    }
    
    public static PlayerData decodePlayerData(String s) {
        Gson gson = new Gson();
        PlayerData altar = null;
        try {
            altar = (PlayerData)gson.fromJson(s, PlayerData.class);
        }
        catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("§4[PlayerData] Erro ao decifrar a sequ\u00eancia do PlayerData: " + s);
            Bukkit.getConsoleSender().sendMessage("§c[PlayerData] Erro Principal: " + ex.getCause());
        }
        return altar;
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
