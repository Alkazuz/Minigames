package br.alkazuz.lobby.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.alkazuz.lobby.main.Main;
import br.alkazuz.lobby.object.NPCRank;
import br.alkazuz.lobby.object.PlayerData;
import br.alkazuz.lobby.object.Servidor;
import br.alkazuz.lobby.object.Servidores;
import br.alkazuz.lobby.sql.SQLData;

public class RankingMinigamesAPI
{
	public static HashMap<Servidor, List<PlayerData>> dataSeverInfo = new HashMap<Servidor, List<PlayerData>>();
    public RankingMinigamesAPI() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	
            	try {
            		
            		for(Servidor servidor : Servidores.servidores) {
            			try {
            				if(servidor.ranks.isEmpty()) continue;
                			String tableName = servidor.flatName.replace(" ", "_");
                			tableName = servidor.flatName.replace(" ", "_");
                			PreparedStatement ps = SQLData.con.prepareStatement("SELECT * FROM `"+tableName.toLowerCase()+"_data`");
                            ResultSet rs = ps.executeQuery();
                            List<PlayerData> listData = new ArrayList<PlayerData>();
                            while (rs.next()) {
                            	
                            	String myJSONString = rs.getString("object");
                            	JsonObject jobj = new Gson().fromJson(myJSONString, JsonObject.class);

                            	String nick = jobj.get("nick").toString();
                            	int wins = jobj.get("winTotal").getAsInt();

                            	PlayerData data = new PlayerData();
                            	data.nick= nick;
                            	data.winTotal = wins;
                            	listData.add(data);
                            }
                            Collections.sort(listData, new Comparator<PlayerData>() {
                                @Override
                                public int compare(PlayerData c1, PlayerData c2) {
    	                            Float o1 = (float)c1.winTotal;
    	                            Float o2 = (float)c2.winTotal;
    	                            return o2.compareTo(o1);
                                }
                            });
                            dataSeverInfo.put(servidor, listData);
            			}catch (Exception e) {
							e.printStackTrace();
						}
            			
                        
                        
                        
                        
            		}
            		
            		for(Servidor servidor : Servidores.servidores) {
                    	if(servidor.ranks != null) {
                    		
                    		List<PlayerData> list = new ArrayList<PlayerData>();
                            if(dataSeverInfo.containsKey(servidor)) {
                            	list = dataSeverInfo.get(servidor);
                            }
                    		
                    		for(Location loc : servidor.ranks.keySet()) {
                    			NPCRank npc = servidor.ranks.get(loc);
                    			if(npc != null) {
                    				npc.createAndUpdate(list);
                    			}
                    		}
                    	}
                    }
            		
                }
                catch (Exception ex) {ex.printStackTrace();}
            	
                
            }
        }, 0L, 20L * 120L);
    }
    
}
