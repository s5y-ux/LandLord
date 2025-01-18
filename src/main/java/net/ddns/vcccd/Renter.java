package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

import net.milkbowl.vault.economy.Economy;

public class Renter implements Listener{
	
	private final Main main;
	
	public Renter(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void renterSpawn(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		Economy econ = main.getEconomy();
		Location eggLocation = event.getEgg().getLocation();
		World world = event.getEgg().getWorld();
		event.setHatching(false);
		
		Villager renter = (Villager) world.spawnEntity(eggLocation, EntityType.VILLAGER);
		renter.setAI(false);
		renter.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a&lRenter"));
		renter.setCustomNameVisible(true);
		
		econ.depositPlayer(player, 100);
		player.sendMessage(main.getPluginPrefix() + String.format(ChatColor.translateAlternateColorCodes('&', "Rent is due, you've collected: &a$%d&7"), main.getConfig().getInt("RentMoney")));
		player.sendMessage(main.getPluginPrefix() + String.format(ChatColor.translateAlternateColorCodes('&', "Your balance is now: &f[&a$%.2f&f]"), econ.getBalance(player)));
		
	}
	
}
