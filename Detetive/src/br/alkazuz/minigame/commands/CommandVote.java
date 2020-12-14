package br.alkazuz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.alkazuz.minigame.game.DetetivePlayer;
import br.alkazuz.minigame.game.MinigameConfig;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.game.RoundState;
import br.alkazuz.minigame.main.Main;

public class CommandVote implements CommandExecutor
{
    public boolean onCommand(CommandSender Sender, Command Cmd, String Label, String[] Args) {
        if (Cmd.getName().equalsIgnoreCase("votar")) {
            if (Args.length != 1) {
                Sender.sendMessage("§cComando correto /votar <jogador>!");
                return true;
            }
            Player p = (Player)Sender;
            for (Round r : Main.theInstance().rounds) {
                if (r.hasPlayer(p)) {
                    DetetivePlayer dp = r.players.get(p);
                    if (dp.voted) {
                        p.sendMessage(MinigameConfig.ALREADY_VOTED);
                        return true;
                    }
                    Player t = Bukkit.getPlayer(Args[0]);
                    if (t == null) {
                        p.sendMessage("§cJogador n\u00e3o encontrado");
                        return true;
                    }
                    DetetivePlayer target = r.players.get(t);
                    if (target == null || target.thePlayer() == null || r.state != RoundState.IN_PROGRESS) {
                        p.sendMessage("§cJogador n\u00e3o encontrado");
                        return true;
                    }
                    int vote = 0;
                    if (!r.votes.containsKey(target)) {
                        r.votes.put(target, 0);
                    }
                    dp.voted = true;
                    vote = r.votes.get(target) + 1;
                    r.votes.put(target, vote);
                    p.sendMessage(MinigameConfig.VOTE.replace("{0}", target.nick));
                    r.updateScoreboard();
                }
            }
        }
        return true;
    }
}
