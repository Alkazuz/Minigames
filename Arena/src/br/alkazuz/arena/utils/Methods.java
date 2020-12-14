package br.alkazuz.arena.utils;

import java.text.*;
import java.util.*;
import org.bukkit.*;

public class Methods
{
    public static Random rand;
    public static NumberFormat formato;
    
    static {
        Methods.rand = new Random();
        Methods.formato = new DecimalFormat("###,###,###");
    }
    
    public static String getRemainingTime(final long seconds) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.setTimeInMillis(seconds);
        final int mYear = calendar.get(1);
        int mMonth = calendar.get(2);
        final int mDay = calendar.get(5);
        final int Hour = calendar.get(11);
        final int Minute = calendar.get(12);
        if (++mMonth > 12) {
            mMonth = 1;
        }
        return String.valueOf(String.valueOf((String.valueOf(mDay).length() == 1) ? "0" : "")) + mDay + "/" + ((String.valueOf(mMonth).length() == 1) ? "0" : "") + mMonth + "/" + mYear + " \u00e0s " + ((String.valueOf(Hour).length() == 1) ? "0" : "") + Hour + ":" + ((String.valueOf(Minute).length() == 1) ? "0" : "") + Minute;
    }
    
    public static String encodeLocation(final Location loc) {
        return String.valueOf(String.valueOf(loc.getWorld().getName())) + ";" + (int)loc.getX() + ";" + (int)loc.getY() + ";" + (int)loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
    
    public static Location decodeLocation(final String c) {
        final String[] coord = c.split(";");
        return new Location(Bukkit.getWorld(coord[0]), (double)Integer.valueOf(coord[1]), (double)Integer.valueOf(coord[2]), (double)Integer.valueOf(coord[3]), (float)Float.valueOf(coord[4]), (float)Float.valueOf(coord[5]));
    }
    
    public static String formatHHMMSS(int secondsCount) {
        final int seconds = secondsCount % 60;
        secondsCount -= seconds;
        long minutesCount = secondsCount / 60;
        final long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        String retur = "";
        retur = String.valueOf(retur) + ((String.valueOf(minutes).length() == 1) ? ("0" + minutes) : minutes) + ":";
        retur = String.valueOf(retur) + ((String.valueOf(seconds).length() == 1) ? ("0" + seconds) : seconds);
        return retur.trim();
    }
    
    public static String getRemainingTime(final String time, long l) {
        l = (System.currentTimeMillis() - l) / 1000L;
        final int seconds = (int)l % 60;
        l -= seconds;
        long minutesCount = l / 60L;
        final long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        final long hoursCount = minutesCount / 60L;
        return (String.valueOf(hoursCount) + "h " + minutes + "m " + seconds + "s").replace("-", "");
    }
}
