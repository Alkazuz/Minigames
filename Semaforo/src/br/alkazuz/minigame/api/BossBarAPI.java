package br.alkazuz.minigame.api;

import org.bukkit.entity.*;

public class BossBarAPI
{
    public static void setBar(Player p, String text, float healthPercent) {
        ActionBarAPI.sendActionBar(p, text);
    }
}
