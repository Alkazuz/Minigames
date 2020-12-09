package br.alkazuz.arena.utils;

import org.bukkit.*;

public enum Version
{
    v1_14("v1_14", 0), 
    v1_13("v1_13", 1), 
    v1_12("v1_12", 2), 
    v1_11("v1_11", 3), 
    v1_10("v1_10", 4), 
    v1_9("v1_9", 5), 
    v1_8("v1_8", 6), 
    v1_7("v1_7", 7), 
    v1_6("v1_6", 8), 
    v1_5("v1_5", 9), 
    DESCONHECIDA("DESCONHECIDA", 10);
    
    private Version(final String s, final int n) {
    }
    
    public static Version getServerVersion() {
        final String ver = Bukkit.getVersion();
        if (ver.contains("1.14")) {
            return Version.v1_14;
        }
        if (ver.contains("1.13")) {
            return Version.v1_13;
        }
        if (ver.contains("1.12")) {
            return Version.v1_12;
        }
        if (ver.contains("1.11")) {
            return Version.v1_11;
        }
        if (ver.contains("1.10")) {
            return Version.v1_10;
        }
        if (ver.contains("1.9")) {
            return Version.v1_9;
        }
        if (ver.contains("1.8")) {
            return Version.v1_8;
        }
        if (ver.contains("1.7")) {
            return Version.v1_7;
        }
        if (ver.contains("1.6")) {
            return Version.v1_6;
        }
        if (ver.contains("1.5")) {
            return Version.v1_5;
        }
        return Version.DESCONHECIDA;
    }
}
