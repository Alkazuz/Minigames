package br.alkazuz.vip.gadget;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gadget {
	
	private String name;
	private ItemStack item;
	private double price;
	private int cooldown;
	
	public Gadget(String name, double price, ItemStack item, int cooldown) {
		this.name = name;
		this.price = price;
		this.item = item;
		this.cooldown = cooldown;
	}
	
	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void onRightClickBlock(Location loc, Player player) {}
	public void onLeftClickBlock(Location loc, Player player) {}
	public void onRightClick(Location loc, Player player) {}
	public void onLeftClick(Location loc, Player player) {}
	public void onUpdate() {}

}
