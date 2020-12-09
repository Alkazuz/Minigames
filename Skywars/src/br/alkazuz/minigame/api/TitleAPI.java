package br.alkazuz.minigame.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.alkazuz.minigame.utils.ReflectionUtils;

public class TitleAPI
{
    private static Method a;
    private static Object enumTIMES;
    private static Object enumTITLE;
    private static Object enumSUBTITLE;
    private static Constructor<?> timeTitleConstructor;
    private static Constructor<?> textTitleConstructor;
    
    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            Object chatTitle = TitleAPI.a.invoke(null, "{\"text\":\"" + title + "\"}");
            Object chatSubtitle = TitleAPI.a.invoke(null, "{\"text\":\"" + subtitle + "\"}");
            Object timeTitlePacket = TitleAPI.timeTitleConstructor.newInstance(TitleAPI.enumTIMES, null, fadeIn, stay, fadeOut);
            Object titlePacket = TitleAPI.textTitleConstructor.newInstance(TitleAPI.enumTITLE, chatTitle);
            Object subtitlePacket = TitleAPI.textTitleConstructor.newInstance(TitleAPI.enumSUBTITLE, chatSubtitle);
            ReflectionUtils.sendPacket(player, timeTitlePacket);
            ReflectionUtils.sendPacket(player, titlePacket);
            ReflectionUtils.sendPacket(player, subtitlePacket);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static void broadcastTitle(Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            Object chatTitle = TitleAPI.a.invoke(null, "{\"text\":\"" + title + "\"}");
            Object chatSubtitle = TitleAPI.a.invoke(null, "{\"text\":\"" + subtitle + "\"}");
            Object timeTitlePacket = TitleAPI.timeTitleConstructor.newInstance(TitleAPI.enumTIMES, null, fadeIn, stay, fadeOut);
            Object titlePacket = TitleAPI.textTitleConstructor.newInstance(TitleAPI.enumTITLE, chatTitle);
            Object subtitlePacket = TitleAPI.textTitleConstructor.newInstance(TitleAPI.enumSUBTITLE, chatSubtitle);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ReflectionUtils.sendPacket(player, timeTitlePacket);
                ReflectionUtils.sendPacket(player, titlePacket);
                ReflectionUtils.sendPacket(player, subtitlePacket);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static void load() {
        try {
            Class<?> icbc = ReflectionUtils.getNMSClass("IChatBaseComponent");
            Class<?> ppot = ReflectionUtils.getNMSClass("PacketPlayOutTitle");
            Class<?> enumClass;
            if (ppot.getDeclaredClasses().length > 0) {
                enumClass = ppot.getDeclaredClasses()[0];
            }
            else {
                enumClass = ReflectionUtils.getNMSClass("EnumTitleAction");
            }
            if (icbc.getDeclaredClasses().length > 0) {
                TitleAPI.a = icbc.getDeclaredClasses()[0].getMethod("a", String.class);
            }
            else {
                TitleAPI.a = ReflectionUtils.getNMSClass("ChatSerializer").getMethod("a", String.class);
            }
            TitleAPI.enumTIMES = enumClass.getField("TIMES").get(null);
            TitleAPI.enumTITLE = enumClass.getField("TITLE").get(null);
            TitleAPI.enumSUBTITLE = enumClass.getField("SUBTITLE").get(null);
            TitleAPI.timeTitleConstructor = ppot.getConstructor(enumClass, icbc, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            TitleAPI.textTitleConstructor = ppot.getConstructor(enumClass, icbc);
        }
        catch (Throwable t) {}
    }
}
