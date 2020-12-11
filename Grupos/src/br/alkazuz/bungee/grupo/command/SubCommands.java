package br.alkazuz.bungee.grupo.command;

import java.util.Iterator;
import br.alkazuz.bungee.grupo.command.sub.SairSubCommand;
import br.alkazuz.bungee.grupo.command.sub.ProcurarSubCommand;
import br.alkazuz.bungee.grupo.command.sub.ExpulsarSubCommand;
import br.alkazuz.bungee.grupo.command.sub.InfoSubCommand;
import br.alkazuz.bungee.grupo.command.sub.AceitarSubCommand;
import br.alkazuz.bungee.grupo.command.sub.ConvidarSubCommand;
import java.util.HashSet;

public class SubCommands
{
    private static HashSet<SubCommand> subs;
    
    static {
        SubCommands.subs = new HashSet<SubCommand>();
    }
    
    public SubCommands() {
        SubCommands.subs.add(new ConvidarSubCommand());
        SubCommands.subs.add(new AceitarSubCommand());
        SubCommands.subs.add(new InfoSubCommand());
        SubCommands.subs.add(new ExpulsarSubCommand());
        SubCommands.subs.add(new ProcurarSubCommand());
        SubCommands.subs.add(new SairSubCommand());
    }
    
    public static HashSet<SubCommand> all() {
        return SubCommands.subs;
    }
    
    public static SubCommand get(final String sub) {
        for (final SubCommand sc : all()) {
            if (sc.getSubCommand().equalsIgnoreCase(sub)) {
                return sc;
            }
        }
        return null;
    }
}
