package br.alkazuz.minigame.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import br.alkazuz.minigame.game.MinigameConfig;
import br.alkazuz.minigame.sql.SQLData;

public class PlayerManager
{
    public static HashMap<String, PlayerData> data;
    
    static {
        PlayerManager.data = new HashMap<String, PlayerData>();
    }
    
    public PlayerManager() {
        try {
            PreparedStatement ps = SQLData.con.prepareStatement("SELECT * FROM `"+MinigameConfig.NAME.toLowerCase()+"_data`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nick = rs.getString("nick");
                PlayerData pd = SQLData.decodePlayerData(rs.getString("object"));
                PlayerManager.data.put(nick.toLowerCase(), pd);
            }
        }
        catch (Exception ex) {}
    }
    
    public static PlayerData fromNick(String nick) {
        if (!PlayerManager.data.containsKey(nick.toLowerCase())) {
            PlayerData pd = new PlayerData();
            pd.nick = nick;
            PlayerManager.data.put(nick.toLowerCase(), pd);
        }
        return PlayerManager.data.get(nick.toLowerCase());
    }
}
