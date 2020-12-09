package br.alkazuz.lobby.api;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AutoRestart implements Runnable{
	
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	List<String> horarios = new ArrayList<String>();
	public static boolean restarting = false;
	public static Long restartTime = 0L;
	public static boolean started = false;
	
	public AutoRestart() {
		sdf.setLenient(false);
		horarios.add("06:00");
		horarios.add("12:00");
		horarios.add("18:00");
		horarios.add("01:00");
	}
	
	@Override
	public void run() {
		if(restarting) {
			if(System.currentTimeMillis() - restartTime >= 5000 && System.currentTimeMillis() - restartTime <= 10000) {
				Bukkit.broadcastMessage("§e[Reinicio] O Servidor será reiniciado em breve...");
				
			}
			if(System.currentTimeMillis() - restartTime >= 10000 && System.currentTimeMillis() - restartTime <= 12000) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.kickPlayer("§cServidor reiniciando...");
				}
				Bukkit.shutdown();
			}
			return;
		}
		Date data = new Date();
		data.setHours(data.getHours() + -1);
		String currentHour = sdf.format(data);
		if(horarios.contains(currentHour)) {
			restarting = true;
			restartTime = System.currentTimeMillis();
		}
	}

	private void startDownServer() {
		String killScreen = String.format("screen -ls \"%s\" | grep -E '\\s+[0-9]+\\.' | awk -F ' ' '{print $1}' | while read s; do screen -XS $s kill; done", "downServer");
        CommandExecutor.run(String.format("screen -X -S %s kill", "downServer"));
        CommandExecutor.run(killScreen);
        
        ProcessBuilder pb = new ProcessBuilder(new String[] { "sh", "start.sh" });
        pb.directory(new File("/root/Servidor/Minigames/Wait/lobby"));
        try {
            pb.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
