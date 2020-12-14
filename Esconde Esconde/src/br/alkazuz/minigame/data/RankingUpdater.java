package br.alkazuz.minigame.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RankingUpdater
{
    public static HashMap<String, Integer> ranking;
    
    static {
        RankingUpdater.ranking = new HashMap<String, Integer>();
    }
    
    public RankingUpdater() {
        List<PlayerData> all = new ArrayList<PlayerData>(PlayerManager.data.values());
        Collections.sort(all, new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData c1, PlayerData c2) {
                Float o1 = (float)c1.winTotal;
                Float o2 = (float)c2.winTotal;
                return o2.compareTo(o1);
            }
        });
        RankingUpdater.ranking.clear();
        int index = 1;
        for (PlayerData a : all) {
            System.out.println(a.nick);
            RankingUpdater.ranking.put(a.nick, index);
            ++index;
        }
    }
}
