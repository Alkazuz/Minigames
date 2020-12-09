package br.alkazuz.minigame.data;

import java.sql.PreparedStatement;

import br.alkazuz.minigame.game.MinigameConfig;
import br.alkazuz.minigame.sql.SQLData;

public class PlayerData
{
    public String nick;
    public int partidas;
    public int winTotal;
    public int winAsVictim;
    public int winAsAssassin;
    public int losesTotal;
    public int losesAsVictim;
    public int losesAsAssassin;
    
    public PlayerData() {
        this.partidas = 0;
        this.winTotal = 0;
        this.winAsVictim = 0;
        this.winAsAssassin = 0;
        this.losesTotal = 0;
        this.losesAsVictim = 0;
        this.losesAsAssassin = 0;
    }
    
    public void save() {
        try {
            SQLData.execute("DELETE FROM `"+MinigameConfig.NAME.toLowerCase().replace(" ", "_")+"_data` WHERE nick='" + this.nick + "'");
            PreparedStatement prepareStatement2 = SQLData.con.prepareStatement("INSERT INTO `"+MinigameConfig.NAME.toLowerCase().replace(" ", "_").toLowerCase()+"_data` (`nick`, `object`) VALUES (?,?) ");
            prepareStatement2.setString(1, this.nick);
            prepareStatement2.setString(2, SQLData.encodePlayerData(this));
            prepareStatement2.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public int getRank() {
        if (RankingUpdater.ranking.containsKey(this.nick)) {
            return RankingUpdater.ranking.get(this.nick);
        }
        return 0;
    }
}
