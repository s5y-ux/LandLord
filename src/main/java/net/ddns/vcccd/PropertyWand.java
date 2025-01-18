package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PropertyWand implements CommandExecutor{
	
	private final Main main;
	
	public PropertyWand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		// Check if the sender is a player
		if(arg0 instanceof Player) {
			Player player = (Player) arg0;
			ItemStack wand = new ItemStack(Material.STICK);
			ItemMeta wandMeta = wand.getItemMeta();
			wandMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aProperty Selector"));
			wand.setItemMeta(wandMeta);
			player.getInventory().addItem(wand);
			player.sendMessage(main.getPluginPrefix() + "You have recieved a property selector");
			player.sendMessage(main.getPluginPrefix() + "Left-Click the bottom left and top right corners of a property to select property to purchase...");
			
		} else {
			assert true;
		}
		return true;
	}
}
