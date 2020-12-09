package br.alkazuz.bungee.ban.command;

import java.util.ArrayList;
import java.util.List;

import br.alkazuz.bungee.ban.main.BungeeConfig;
import br.alkazuz.bungee.ban.manager.Ban;
import br.alkazuz.bungee.ban.manager.Bans;
import br.alkazuz.bungee.ban.manager.Punicao;
import br.alkazuz.bungee.ban.manager.Ban.BanType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandPunir extends Command
{
    public CommandPunir() {
    	super("Punir", "bungee.command.punir", "punir");
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender Sender, final String[] args) {
        if(args.length != 3 && args.length != 2) {
        	Sender.sendMessage("§cUso correto do comando: /punir <jogador> <punição> [prova].");
        	for(Punicao p : Punicao.all) {
        		if(!Sender.hasPermission("punir."+p.name)) continue;
        		Sender.sendMessage(String.format("§cPunição: §e%s§c, §cTítulo: §e%s§c, Tipo: §e%s§c, Tempo: §e%s§c.", p.name, p.title, p.type.toString(), p.time));
        	}
        	return;
        }
        String ban = args[1].toLowerCase();
        if(Punicao.all.stream().filter(b -> b.name.equals(ban)).count() == 0) {
        	Sender.sendMessage("§cTipo de punição inexistente.");
        	return;
        }
        List<Punicao> list = new ArrayList<Punicao>();
        Punicao.all.stream().filter(b -> b.name.equals(ban)).forEach(list::add);
        Punicao p = list.get(0);
        if(!Sender.hasPermission("punir."+p.name)) {
        	Sender.sendMessage("§cVocê não tem permissão para usar essa punição.");
        	return;
        }
        
        String user = args[0].toLowerCase();
        if(Bans.bans.containsKey(user)) {
        	Sender.sendMessage("§cO jogador já está com alguma punição aplicada.");
        	return;
        }
        String proof = "---";
        if(args.length == 3) {
        	proof = args[2];
        }
        long newTime = -1;
        if(!p.time.equals("---")) {
        	 long total = 0L;
        	String[] time = p.time.split(",");
            for (int i = 0; i < time.length; ++i) {
                if (time[i].replaceAll("[0-9]", "").length() != 1) {
                    return;
                }
                if (!time[i].replaceAll("[0-9]", "").contains("d") && !time[i].replaceAll("[0-9]", "").contains("h") && !time[i].replaceAll("[0-9]", "").contains("m") && !time[i].replaceAll("[0-9]", "").contains("s")) {
                    return;
                }
                long day = 0L;
                long hour = 0L;
                long minute = 0L;
                long second = 0L;
                if (time[i].replaceAll("[0-9]", "").contains("d")) {
                    day = Integer.parseInt(time[i].replaceAll("[A-Za-z]", "")) * 86400;
                }
                if (time[i].replaceAll("[0-9]", "").contains("h")) {
                    hour = Integer.parseInt(time[i].replaceAll("[A-Za-z]", "")) * 3600;
                }
                if (time[i].replaceAll("[0-9]", "").contains("m")) {
                    minute = Integer.parseInt(time[i].replaceAll("[A-Za-z]", "")) * 60;
                }
                if (time[i].replaceAll("[0-9]", "").contains("s")) {
                    second = Integer.parseInt(time[i].replaceAll("[A-Za-z]", ""));
                }
                total += day + hour + minute + second;
            }
            newTime = total * 1000L + System.currentTimeMillis();
        }
       
        Ban banmeant = new Ban();
        banmeant.nick = user;
        banmeant.title = p.title;
        banmeant.proof = proof;
        banmeant.desban = newTime;
        banmeant.applied = System.currentTimeMillis();
        banmeant.author = Sender.getName();
        banmeant.type = p.type;
        banmeant.save();
        Bans.bans.put(user, banmeant);
        
        ProxiedPlayer player = BungeeCord.getInstance().getPlayer(user);
        if(player != null) {
        	if(p.type == BanType.BAN) {
        		StringBuilder sb = new StringBuilder();
        		if(p.time.equals("---")) {
        			for(String a : BungeeConfig.BAN) {
        				sb.append(a.replace("{0}", banmeant.applied())
        						.replace("{1}", banmeant.title)
        						.replace("{2}", banmeant.proof)
        						.replace("{3}", banmeant.author));
        				sb.append("\n");
        			}
        		}else {
        			for(String a : BungeeConfig.MUTE_TEMP) {
        				sb.append(a.replace("{0}", banmeant.applied())
        						.replace("{1}", banmeant.title)
        						.replace("{2}", banmeant.proof)
        						.replace("{3}", banmeant.author)
        						.replace("{4}", banmeant.debanTime().trim()));
        				sb.append("\n");
        			}
        		}
        		player.disconnect(sb.toString());
        	}else {
        		StringBuilder sb = new StringBuilder();
        		if(p.time.equals("---")) {
        			for(String a : BungeeConfig.BAN) {
        				sb.append(a.replace("{0}", banmeant.applied())
        						.replace("{1}", banmeant.title)
        						.replace("{2}", banmeant.proof)
        						.replace("{3}", banmeant.author));
        				sb.append("\n");
        			}
        		}else {
        			for(String a : BungeeConfig.MUTE_TEMP) {
        				sb.append(a.replace("{0}", banmeant.applied())
        						.replace("{1}", banmeant.title)
        						.replace("{2}", banmeant.proof)
        						.replace("{3}", banmeant.author)
        						.replace("{4}", banmeant.debanTime().trim()));
        				sb.append("\n");
        			}
        		}
        		player.sendMessage(sb.toString());
        		
        	}
        }
        Sender.sendMessage("§aPunição aplicada com sucesso.");
    }
}
