package br.alkazuz.lobby.scoreboard;

import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.scoreboard.*;

public class ScoreAPI
{
    Player recebidor;
    String titulo;
    private HashMap<Integer, String> cache;
    
    public ScoreAPI(Player recebidor, String titulo) {
        this.cache = new HashMap<Integer, String>();
        this.recebidor = null;
        this.titulo = ChatColor.GRAY + "Titulo indefinido.";
        this.definirRecebidor(recebidor);
        this.definirTitulo(titulo);
    }
    
    public void definirRecebidor(Player recebidor) {
        this.recebidor = recebidor;
    }
    
    public void definirTitulo(String titulo) {
        if (titulo.length() > 40) {
            this.titulo = ChatColor.translateAlternateColorCodes('&', titulo.substring(0, 40));
        }
        else {
            this.titulo = ChatColor.translateAlternateColorCodes('&', titulo);
        }
    }
    
    public void definirLinha(String texto, int linha) {
        this.cache.put(linha, texto);
        if (b(this.c(), linha)) {
            if (a(this.c(), linha).equalsIgnoreCase(texto)) {
                return;
            }
            if (!a(this.c(), linha).equalsIgnoreCase(texto)) {
                this.c().getScoreboard().resetScores(a(this.c(), linha));
            }
        }
        this.c().getScore(texto).setScore(linha);
    }
    
    public void enviar(Player p) {
        if (this.recebidor == null) {
            return;
        }
        if (this.recebidor.getScoreboard().equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) {
            this.recebidor.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
        }
        this.c().setDisplayName(this.titulo);
        if (this.c().getDisplaySlot() != DisplaySlot.SIDEBAR) {
            this.c().setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        this.recebidor.setScoreboard(this.recebidor.getScoreboard());
    }
    
    private static String a(Objective o, int score) {
        if (o == null) {
            return null;
        }
        if (!b(o, score)) {
            return null;
        }
        for (String s : o.getScoreboard().getEntries()) {
            if (o.getScore(s).getScore() == score) {
                return o.getScore(s).getEntry();
            }
        }
        return null;
    }
    
    private static boolean b(Objective o, int score) {
        for (String s : o.getScoreboard().getEntries()) {
            if (o.getScore(s).getScore() == score) {
                return true;
            }
        }
        return false;
    }
    
    private Objective c() {
        Scoreboard score = this.recebidor.getScoreboard();
        return (score.getObjective(this.recebidor.getName()) == null) ? score.registerNewObjective(this.recebidor.getName(), "dummy") : score.getObjective(this.recebidor.getName());
    }
    
    public void update(String string, int i) {
        this.definirLinha(String.valueOf(this.cache.get(i)) + string, i);
    }
}
