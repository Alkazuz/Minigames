package br.alkazuz.arena.data;

import java.sql.PreparedStatement;

import br.alkazuz.arena.game.MinigameConfig;
import br.alkazuz.arena.sql.SQLData;

public class PlayerData
{
    public String nick;
    public int partidas;
    public int winTotal;
    public int kills;
    public int deaths;
    
    public PlayerData() {
        this.partidas = 0;
        this.winTotal = 0;
        this.kills = 0;
        this.deaths = 0;
    }
    
    public void save() {
        try {
            SQLData.execute("DELETE FROM `"+MinigameConfig.NAME.toLowerCase()+"_data` WHERE nick='" + this.nick + "'");
            final PreparedStatement prepareStatement2 = SQLData.con.prepareStatement("INSERT INTO `arena_data` (`nick`, `object`) VALUES (?,?) ");
            prepareStatement2.setString(1, this.nick);
            prepareStatement2.setString(2, SQLData.encodePlayerData(this));
            prepareStatement2.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
