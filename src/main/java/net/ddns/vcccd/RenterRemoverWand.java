package net.ddns.vcccd;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenterRemoverWand implements CommandExecutor {
	
	private ItemStack renterRemover = new ItemStack(Material.BLAZE_ROD);
	
	public RenterRemoverWand() {
		// Setting item meta of the remover
		ItemMeta removerMeta = renterRemover.getItemMeta();
		ArrayList<String> itemLore = new ArrayList<String>();
		itemLore.add(ChatColor.GRAY + "Right-Click to remove Renter");
		removerMeta.setLore(itemLore);
		removerMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lRenter Remover"));
		this.renterRemover.setItemMeta(removerMeta);
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		// Checking if the command sender is a Player or not
		if(arg0 instanceof Player) {
			
			// Giving the player the renter remover
			Player player = (Player) arg0;
			player.getInventory().addItem(this.renterRemover);
			
		} else {
			arg0.sendMessage("You can only run this command as a Player...");
		}
		return true;
	}

}
