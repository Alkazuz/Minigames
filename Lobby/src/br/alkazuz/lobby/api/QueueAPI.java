package br.alkazuz.lobby.api;

import br.alkazuz.lobby.object.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import br.alkazuz.lobby.main.*;
import org.bukkit.plugin.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueueAPI
{
    public static Map<String, Servidor> lobby = Collections.synchronizedMap(new HashMap<String, Servidor>());
    public static Map<Player, Servidor> queue = Collections.synchronizedMap(new HashMap<Player, Servidor>());
    public static Map<Player, Long> queueDelay = Collections.synchronizedMap(new HashMap<Player, Long>());
    
    
    public static void addToQueue(Player player, Servidor servidor) {
        if (!player.hasPermission("minigames.vip")) {
            player.sendMessage(" ");
            player.sendMessage("§aVoc\u00ea entrou na fila para jogar §b" + servidor.flatName + "§a. Assim que houver uma " + "partida disponivel voc\u00ea ser\u00e1 teletransportado(a) automaticamente.");
            player.sendMessage(" ");
        }
        else {
            player.sendMessage(" ");
            player.sendMessage("§aVoc\u00ea entrou na fila §6VIP §ade alta prioridade para jogar §b" + servidor.flatName + "§a. Assim que houver uma " + "partida disponivel voc\u00ea ser\u00e1 teletransportado(a) automaticamente.");
            player.sendMessage(" ");
        }
        QueueAPI.queue.put(player, servidor);
        queueDelay.put(player, System.currentTimeMillis());
    }
    public static HashMap<Servidor, PingServer> status = new HashMap<Servidor, PingServer>();
    public QueueAPI() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                List<Player> q = new ArrayList<Player>(QueueAPI.queue.keySet());
                
                for (Servidor servidor : QueueAPI.queue.values()) {
                    PingServer ps = new PingServer(servidor.ip.split(":")[0], Integer.valueOf(servidor.ip.split(":")[1]));
                    status.put(servidor, ps);
                    
                    if (!ps.isOnline() && q.stream().filter(p -> queue.get(p) == servidor).count() > 0 && System.currentTimeMillis() - servidor.lastStart >= 60000L) {
                    	servidor.lastStart = System.currentTimeMillis();
                        System.out.println("Matando screen do servidor "+servidor.flatName);
                        String killScreen = String.format("sudo screen -ls \"%s\" | grep Detached | awk '{print $1}' | cut -f1 -d'.' | while read in; do screen -X -S $in quit; done ", servidor.flatName);
                        
                        try {
                        	Process pb = Runtime.getRuntime().exec(killScreen);
                            BufferedReader br = new BufferedReader(
                                new InputStreamReader(pb.getInputStream()));
                            String sd;
							while ((sd = br.readLine()) != null)
                                System.out.println("line: " + sd);
							pb.waitFor();
                            System.out.println ("exit: " +pb.exitValue());
                            pb.destroy();
                        } catch (Exception e) {e.printStackTrace();}
                        
                        servidor.start();
                    }
                }
                for (Player p : q) {
                    if (!p.isOnline()) {
                        QueueAPI.queue.remove(p);
                        queueDelay.remove(p);
                        continue;
                    }
                    if(System.currentTimeMillis() - queueDelay.get(p) < TimeUnit.SECONDS.toMillis(10L)) {
                    	p.sendMessage(" ");
                        p.sendMessage("§bProcurando por uma partida. Aguarde...");
                        p.sendMessage(" ");
                    }
                    
                    Servidor servidor2 = QueueAPI.queue.get(p);
                    PingServer s = status.get(servidor2);
                    if(!s.isOnline()) continue;
                    if (!s.getMotd().contains("§aSala disponivel")) {
                    	queueDelay.put(p, System.currentTimeMillis());
                        continue;
                    }
                    p.sendMessage("§aPartida encontrada, voc\u00ea foi enviado(a) para o " + servidor2.flatName);
                    ServerAPI.sendPlayer(p, servidor2.name);
                    servidor2.played.getAndIncrement();
                    QueueAPI.queue.remove(p);
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF("PortalPlayer");
                        out.writeUTF(servidor2.flatName);
                        p.sendPluginMessage((Plugin)Main.theInstance(), "BungeeCord", b.toByteArray());
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }, 0L, 60L);
    }
}
