package br.alkazuz.bungee.grupo.command;

import net.md_5.bungee.api.CommandSender;

public class SubCommand
{
    private String Command;
    private String subCommand;
    private String usage;
    
    public SubCommand(final String command, final String sb, final String usage) {
        this.Command = command;
        this.subCommand = sb;
        this.usage = usage;
    }
    
    public String getUsage() {
        return this.usage;
    }
    
    public String getSubCommand() {
        return this.subCommand;
    }
    
    public void setSubCommand(final String subCommand) {
        this.subCommand = subCommand;
    }
    
    public String getCommand() {
        return this.Command;
    }
    
    public void setCommand(final String command) {
        this.Command = command;
    }
    
    public void setUsage(final String usage) {
        this.usage = usage;
    }
    
    public String getDescription() {
        return "";
    }
    
    public void run(final String[] args, final CommandSender s) {
    }
}
