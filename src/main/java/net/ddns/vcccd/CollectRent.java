package net.ddns.vcccd;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;

public class CollectRent extends BukkitRunnable{
	
	private final Main main;
	
	public CollectRent(Main main) {
		this.main = main;
	}
	
	private String T(String parameter) {
		return(ChatColor.translateAlternateColorCodes('&', parameter));
	}
	
	@Override
	public void run() {
		try {
		double rentAmount = main.getConfig().getDouble("RentMoney");
		List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
		Economy serverEconomy = main.getEconomy();
		for(Player player: Bukkit.getOnlinePlayers()) {
			for(propertyJSON property: playerProperties) {
				if(property.playerUUID.equals(player.getUniqueId())) {
					serverEconomy.depositPlayer(player, rentAmount);
					player.sendMessage(main.getPluginPrefix() + "Collected rent on a property: " + T("&f[&a+") + String.valueOf(rentAmount) + T("&f]"));
				}
			}
			player.sendMessage(main.getPluginPrefix() + "Your balance is now: " + T("&f[&a+") + String.valueOf(serverEconomy.getBalance(player)) + T("&f]"));
		}
	} catch(Exception e) {
		return;
	}

}
}
