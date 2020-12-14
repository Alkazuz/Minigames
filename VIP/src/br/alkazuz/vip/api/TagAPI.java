package br.alkazuz.vip.api;

import org.bukkit.entity.*;
import com.nametagedit.plugin.*;

public class TagAPI
{
    public static void apply(String suffixo, Player p) {
        String tag = "§7";
        if (p.hasPermission("minigames.tag.staff")) {
            tag = "§4[STAFF] ";
        }
        else if (p.hasPermission("minigames.tag.youtuber")) {
            tag = "§4[YT] ";
        }
        else if (p.hasPermission("minigames.tag.vip")) {
            tag = "§6[VIP] ";
        }
        p.setDisplayName(String.valueOf(tag) + p.getName() + suffixo);
        
        NametagEdit.getApi().setNametag(p, String.valueOf(tag), suffixo);
    }
}
