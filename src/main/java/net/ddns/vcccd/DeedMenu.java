package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class DeedMenu implements CommandExecutor, Listener {
	private final Main main;
	private HashMap<UUID, ItemStack> selectedPropety = new HashMap<UUID, ItemStack>();
	private Inventory deedMenu = Bukkit.createInventory(null, 9, "Owned Properties");
	private Inventory confirmMenu = Bukkit.createInventory(null, 9, "Confirm Sale");
	
	public DeedMenu(Main main) {
		this.main = main;
	}
	
	public void generateConfirmMenu(ItemStack parameter, Player player) {
		
		ItemStack cancelGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta cancelGlassMeta = cancelGlass.getItemMeta();
		cancelGlassMeta.setDisplayName(ChatColor.RED + "Cancel");
		cancelGlass.setItemMeta(cancelGlassMeta);
		
		ItemStack acceptGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemMeta acceptGlassMeta = acceptGlass.getItemMeta();
		acceptGlassMeta.setDisplayName(ChatColor.GREEN + "Accept");
		acceptGlass.setItemMeta(acceptGlassMeta);
		
		for(int i = 0; i < 4; i++) {
			this.confirmMenu.setItem(i, cancelGlass);
		}
		this.confirmMenu.setItem(4, parameter);
		for(int i = 5; i < 9; i++) {
			this.confirmMenu.setItem(i, acceptGlass);
		}
		
		player.openInventory(confirmMenu);
		
	}
	
	private ArrayList<Integer> convertStringToArrayList(String parameter){
		parameter = parameter.replaceAll("[\\[\\]\\s]", "");
		String[] rawNumbers = parameter.split(",");
		ArrayList<Integer> returnVal = new ArrayList<Integer>();
		for(String values: rawNumbers) {
			returnVal.add(Integer.parseInt(values));
		}
		return(returnVal);
	}
	
	private void removePropertyFromGui(ItemStack inGameDeed, Player player) {
		ItemMeta referenceMeta = inGameDeed.getItemMeta();
		List<String> dataValues = referenceMeta.getLore();
		String worldName = dataValues.get(0).substring(9);
		String xArray = dataValues.get(1).substring(5);
		String yArray = dataValues.get(2).substring(5);
		String zArray = dataValues.get(3).substring(5);
		ArrayList<Integer> parameterX = convertStringToArrayList(xArray);
		ArrayList<Integer> parameterY = convertStringToArrayList(yArray);
		ArrayList<Integer> parameterZ = convertStringToArrayList(zArray);
		this.main.getPlayerPropertiesFile().removeJson(new propertyJSON(worldName, player.getUniqueId(), parameterX, parameterY, parameterZ));
		
	}
	
	private boolean standingInProperty(Player player, propertyJSON playerProperty) {
		Location playerLocation = player.getLocation();
		boolean inRangeX = !(playerLocation.getX() < playerProperty.xRange.get(0) || playerLocation.getX() > playerProperty.xRange.get(1));
        boolean inRangeY = !(playerLocation.getY() < playerProperty.yRange.get(0) || playerLocation.getY() > playerProperty.yRange.get(1));
        boolean inRangeZ = !(playerLocation.getZ() < playerProperty.zRange.get(0) || playerLocation.getZ() > playerProperty.zRange.get(1));
		return(inRangeX && inRangeY && inRangeZ);
		
	}
	
	
	private ItemStack generateDeed(int propertyNumber, propertyJSON property, Player player) {
		ItemStack returnValue = new ItemStack(Material.PAPER);
		ItemMeta returnValueMeta = returnValue.getItemMeta();
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add(ChatColor.GRAY + "World: " + property.worldName);
		Lore.add(ChatColor.GRAY + "X: " + property.xRange.toString());
		Lore.add(ChatColor.GRAY + "Y: " + property.yRange.toString());
		Lore.add(ChatColor.GRAY + "Z: " + property.zRange.toString());
		if(standingInProperty(player, property)) {
			Lore.add(ChatColor.GREEN + "You are standing in this property!");
			returnValueMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lProperty ") + ChatColor.GREEN + Integer.toString(propertyNumber));
		} else {
			Lore.add(ChatColor.RED + "You are NOT standing in this property!");
			returnValueMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lProperty ") + ChatColor.RED + Integer.toString(propertyNumber));
		}
		returnValueMeta.setLore(Lore);
		returnValue.setItemMeta(returnValueMeta);
		return returnValue;
	}
	
	@EventHandler
	public void propertySelect(InventoryClickEvent event) {
	    Player player = (Player) event.getWhoClicked();
	    ItemStack referenceItem = event.getCurrentItem();

	    // Fix: Check if item is null or air (empty slot)
	    if (referenceItem == null || referenceItem.getType() == Material.AIR) return;

	    ItemMeta referenceItemMeta = referenceItem.getItemMeta();
	    if (referenceItemMeta == null) return;

	    boolean correctInventorySize = event.getInventory().getSize() == 9;

	    boolean correctItemType = referenceItem.getType() == Material.PAPER;
	    boolean isAcceptGlass = referenceItem.getType() == Material.LIME_STAINED_GLASS_PANE &&
	                            referenceItemMeta.getDisplayName().equals(ChatColor.GREEN + "Accept");
	    boolean isDeclineGlass = referenceItem.getType() == Material.RED_STAINED_GLASS_PANE &&
	                             referenceItemMeta.getDisplayName().equals(ChatColor.RED + "Cancel");

	    if (correctInventorySize && correctItemType) {
	        try {
	            boolean correctPaperName = referenceItemMeta.getDisplayName().substring(4, 12).equals("Property");
	            if (correctPaperName) {
	                player.closeInventory();
	                if(!selectedPropety.values().contains(referenceItem)) {
	                	selectedPropety.put(player.getUniqueId(), referenceItem);
	                } else {
	                	selectedPropety.replace(player.getUniqueId(), referenceItem);
	                }
	                generateConfirmMenu(referenceItem, player);
	            }
	        } catch (Exception e) {
	            player.closeInventory();
	        }
	    }

	    if (isAcceptGlass) {
	        try {
	            removePropertyFromGui(selectedPropety.get(player.getUniqueId()), player);
	            double saleAmount = main.getConfig().getDouble("SellAmount");
	            main.getEconomy().depositPlayer(player, saleAmount);
	            player.sendMessage(main.getPluginPrefix() + "Property has been sold! " + ChatColor.WHITE + "[" + ChatColor.GREEN + "+$" + String.valueOf(saleAmount) + ChatColor.WHITE +"]");
	            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
	            player.closeInventory();
	        } catch (Exception e) {
	            main.getMainConsole().sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error occurred while trying to sell property, please report:");
	            player.closeInventory();
	        }
	    }

	    if (isDeclineGlass) {
	        player.closeInventory();
	    }
	}


	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Player) {
			deedMenu.clear();
			Player player = (Player) arg0;
			try {
			List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
			int currentPropertyNumber = 0;
			for(propertyJSON playerProperty: playerProperties) {
				this.deedMenu.setItem(currentPropertyNumber, generateDeed(currentPropertyNumber, playerProperty, player));
				currentPropertyNumber++;
			}
			player.openInventory(deedMenu);
			} catch (Exception e) {
				player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You must have property in order to use this command...");
			}
		} else {
			arg0.sendMessage(main.getPluginPrefix() + "Only players can execute this command...");
		}
		return true;
	}
}
