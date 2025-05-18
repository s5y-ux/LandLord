package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Renter implements Listener{
	
	private final Main main;
	
	public Renter(Main main) {
		this.main = main;
	}
	
	public void RenterEgg(Player player) {
		ItemStack renterEgg = new ItemStack(Material.EGG);
		ItemMeta renterMeta = renterEgg.getItemMeta();
		ArrayList<String> ItemLore = new ArrayList<String>();
		ItemLore.add(ChatColor.GRAY + "Throw at the ground in a property");
		ItemLore.add(ChatColor.GRAY + "to place your renter...");
		renterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aRenter Spawn Egg"));
		renterMeta.setLore(ItemLore);
		renterEgg.setItemMeta(renterMeta);
		player.getInventory().addItem(renterEgg);
	}
	
	private boolean renterInProperty(Villager renter) {
    	List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
    	Location renterLocation = renter.getLocation(); 
    	for(propertyJSON playerProperty: playerProperties) {
		boolean inRangeX = !(renterLocation.getX() < playerProperty.xRange.get(0) || renterLocation.getX() > playerProperty.xRange.get(1));
        boolean inRangeY = !(renterLocation.getY() < playerProperty.yRange.get(0) || renterLocation.getY() > playerProperty.yRange.get(1));
        boolean inRangeZ = !(renterLocation.getZ() < playerProperty.zRange.get(0) || renterLocation.getZ() > playerProperty.zRange.get(1));
		if(inRangeX && inRangeY && inRangeZ) {
			return true;
		}
    	}
    	return false;
    }
	
	@EventHandler
	public void renterSpawn(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		Location eggLocation = event.getEgg().getLocation();
		World world = event.getEgg().getWorld();
		event.setHatching(false);
		
		Villager renter = (Villager) world.spawnEntity(eggLocation, EntityType.VILLAGER);
		if(!renterInProperty(renter)) {
			player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Renter must be spawned in a property...");
			RenterEgg(player);
			renter.setHealth(0);
			return;
		}
		renter.setAI(false);
		renter.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a&lRenter"));
		renter.setCustomNameVisible(true);

		player.sendMessage(main.getPluginPrefix() + "Renter is now tied to this property...");
		
	}
	
}
