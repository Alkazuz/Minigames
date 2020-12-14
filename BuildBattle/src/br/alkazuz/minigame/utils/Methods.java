package br.alkazuz.minigame.utils;

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
    
    public static String getRemainingTime(long seconds) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendar.setTimeInMillis(seconds);
        int mYear = calendar.get(1);
        int mMonth = calendar.get(2);
        int mDay = calendar.get(5);
        int Hour = calendar.get(11);
        int Minute = calendar.get(12);
        if (++mMonth > 12) {
            mMonth = 1;
        }
        return String.valueOf(String.valueOf((String.valueOf(mDay).length() == 1) ? "0" : "")) + mDay + "/" + ((String.valueOf(mMonth).length() == 1) ? "0" : "") + mMonth + "/" + mYear + " \u00e0s " + ((String.valueOf(Hour).length() == 1) ? "0" : "") + Hour + ":" + ((String.valueOf(Minute).length() == 1) ? "0" : "") + Minute;
    }
    
    public static String encodeLocation(Location loc) {
        return String.valueOf(String.valueOf(loc.getWorld().getName())) + ";" + (int)loc.getX() + ";" + (int)loc.getY() + ";" + (int)loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
    
    public static Location decodeLocation(String c) {
        String[] coord = c.split(";");
        return new Location(Bukkit.getWorld(coord[0]), (double)Integer.valueOf(coord[1]), (double)Integer.valueOf(coord[2]), (double)Integer.valueOf(coord[3]), (float)Float.valueOf(coord[4]), (float)Float.valueOf(coord[5]));
    }
    
    public static String getRemainingTime2(String time, int l) {
        String ret = "";
        int seconds = l % 60;
        l -= seconds;
        long minutesCount = l / 60;
        long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        long hoursCount = minutesCount / 60L;
        if (hoursCount != 0L) {
            ret = String.valueOf(ret) + hoursCount + " h ";
        }
        if (minutes != 0L) {
            ret = String.valueOf(ret) + minutes + " m ";
        }
        if (seconds != 0L) {
            ret = String.valueOf(ret) + seconds + " s ";
        }
        return ret.replace("-", "").trim();
    }
    
    public static String getRemainingTime(String time, int l) {
        String ret = "";
        int seconds = l % 60;
        l -= seconds;
        long minutesCount = l / 60;
        long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        long hoursCount = minutesCount / 60L;
        if (hoursCount != 0L) {
            ret = String.valueOf(ret) + hoursCount + " hora(s) ";
        }
        if (minutes != 0L) {
            ret = String.valueOf(ret) + minutes + " minuto(s) ";
        }
        if (seconds != 0L) {
            ret = String.valueOf(ret) + seconds + " segundo(s) ";
        }
        return ret.replace("-", "").trim();
    }
    
    public static String formatHHMMSS(int secondsCount) {
        int seconds = secondsCount % 60;
        secondsCount -= seconds;
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        String retur = "";
        retur = String.valueOf(retur) + ((String.valueOf(minutes).length() == 1) ? ("0" + minutes) : Long.valueOf(minutes)) + ":";
        retur = String.valueOf(retur) + ((String.valueOf(seconds).length() == 1) ? ("0" + seconds) : Integer.valueOf(seconds));
        return retur.trim();
    }
}
