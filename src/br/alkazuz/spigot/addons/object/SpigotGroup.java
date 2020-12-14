package br.alkazuz.spigot.addons.object;

public class SpigotGroup {
	
	private String group;
	private String prefix;
	private String suffix;
	private int priority;
	
	public SpigotGroup(String group, String prefix, String suffix, int priority){
		this.group = group;
		this.prefix = prefix;
		this.suffix = suffix;
		this.priority = priority;
	}
	
	public String getGroup() {
		return group;
	}
	public String getPrefix() {
		return prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public int getPriority() {
		return priority;
	}
	
}
