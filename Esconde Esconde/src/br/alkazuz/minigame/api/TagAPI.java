package br.alkazuz.minigame.api;

import org.bukkit.entity.*;
import com.nametagedit.plugin.*;

public class TagAPI
{
    public static void apply(String prefixo, String suffixo, Player p, boolean update) {
        String tag = "";
        if(prefixo.equals("")) {
        	prefixo = "§7";
        }
        if (p.hasPermission("minigames.tag.staff")) {
            tag = "§4[STAFF] ";
        }
        else if (p.hasPermission("minigames.tag.youtuber")) {
            tag = "§4[YT] ";
        }
        else if (p.hasPermission("minigames.tag.vip")) {
            tag = "§6[VIP] ";
        }
        if(prefixo.equals("§7")) {
        	p.setDisplayName(String.valueOf(tag) + p.getName() + suffixo);
        }else {
        	p.setDisplayName(String.valueOf(tag) + prefixo + p.getName() + suffixo);
        }
        
        NametagEdit.getApi().setNametag(p, String.valueOf(tag) + prefixo, suffixo);
    }
}
