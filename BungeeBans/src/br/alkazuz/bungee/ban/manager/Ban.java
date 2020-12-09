package br.alkazuz.bungee.ban.manager;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import br.alkazuz.bungee.ban.mysql.MySQLConnection;
import br.alkazuz.bungee.ban.mysql.SQLData;

public class Ban {
	
	public String nick;
	public String title;
	public BanType type;
	public Long applied;
	public Long desban;
	public String proof;
	public String author;
	
	public void unban() {
		SQLData.execute("DELETE FROM `minigames_bans` WHERE nick='" + this.nick + "'");
	}
	
	public String debanTime() {
		int seconds = (int) ((desban - System.currentTimeMillis()) / 1000L);
		int day = (int)TimeUnit.SECONDS.toDays(seconds);        
		 long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
		 long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
		 long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
	    
	    //Build the String
	    String retur = "";
	    if(day != 0) {
	    	retur += day + "d ";
	    }
	    if(hours != 0) {
	    	retur += hours + "h ";
	    }
	    if(minute != 0) {
	    	retur += minute + "m ";
	    }
	    if(second != 0) {
	    	retur += second + "s ";
	    }
	    return retur.trim().replace("-", "");
	}
	
	public String applied() {
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
	    Calendar today = Calendar.getInstance();
        calendar.setTimeInMillis(applied);

        Date data = calendar.getTime();
        Locale local = new Locale("pt", "BR");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", local);
        
        int dayOfYearToday = today.get(Calendar.DAY_OF_YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        if(today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && dayOfYearToday == dayOfYear) {
        	return "Hoje às " + sdf.format(data);
        }else if(today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && (dayOfYearToday - 1) == dayOfYear) {
        	return "Ontem às " + sdf.format(data);
        }
        else if(today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && (dayOfYearToday - dayOfYear) <= 5) {
        	return (dayOfYearToday - dayOfYear) + " dias atrás às "+ sdf.format(data);
        }
        sdf = new SimpleDateFormat("dd/MM/yyyy,  HH:mm.", local);
        return sdf.format(data);
	}
	
	public void save() {
		try {
            PreparedStatement prepareStatement2 = MySQLConnection.con.prepareStatement("INSERT INTO `minigames_bans` (`nick`, `object`) VALUES (?,?) ");
            prepareStatement2.setString(1, this.nick);
            prepareStatement2.setString(2, SQLData.encodeBan(this));
            prepareStatement2.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	public enum BanType{
		MUTE, BAN;
	}

}
