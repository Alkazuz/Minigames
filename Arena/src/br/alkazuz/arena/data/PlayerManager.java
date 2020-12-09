package br.alkazuz.arena.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import br.alkazuz.arena.sql.SQLData;

public class PlayerManager
{
    public static HashMap<String, PlayerData> data;
    
    static {
        PlayerManager.data = new HashMap<String, PlayerData>();
    }
    
    public PlayerManager() {
        try {
            final PreparedStatement ps = SQLData.con.prepareStatement("SELECT * FROM `arena_data`");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String nick = rs.getString("nick");
                final PlayerData pd = SQLData.decodePlayerData(rs.getString("object"));
                PlayerManager.data.put(nick.toLowerCase(), pd);
            }
        }
        catch (Exception ex) {}
    }
    
    public static PlayerData fromNick(final String nick) {
        if (!PlayerManager.data.containsKey(nick.toLowerCase())) {
            final PlayerData pd = new PlayerData();
            pd.nick = nick;
            PlayerManager.data.put(nick.toLowerCase(), pd);
        }
        return PlayerManager.data.get(nick.toLowerCase());
    }
}
