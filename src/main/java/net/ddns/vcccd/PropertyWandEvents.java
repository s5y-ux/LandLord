package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class PropertyWandEvents implements Listener {
    
    private final Main main;
    private final SelectionManager selectionManager;
    private CoolDownManager coolDown; 

    public PropertyWandEvents(Main main) {
        this.main = main;
        this.selectionManager = new SelectionManager();
        this.coolDown = new CoolDownManager();
    }

    
    @EventHandler
    public void useWandEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        double playerBalance = main.getEconomy().getBalance(player);
        UUID playerId = player.getUniqueId();
        World world = player.getWorld();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Block selectedBlock = event.getClickedBlock();
        boolean playerRightClickedWithStick = event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            && heldItem.getType().equals(Material.STICK)
            && heldItem.hasItemMeta()
            && heldItem.getItemMeta().hasDisplayName()
            && heldItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aProperty Selector"));



        // Cooldown check
        // ----
        if (coolDown.isOnCooldown(player)) {
            return;
        }
        coolDown.add(player, 500);
        // ----


        
        if (playerRightClickedWithStick) {

            selectionManager.incrementPosition(playerId);
            selectionManager.addBlock(playerId, selectedBlock);

            player.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', main.getPluginPrefix() + "Position %d set"), selectionManager.getPosition(playerId)));
            player.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', main.getPluginPrefix() + "Block - X: &f%d&7, Y: &f%d&7, Z: &f%d&7"),
                selectedBlock.getX(), selectedBlock.getY(), selectedBlock.getZ()));

            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            // If we have made all the selections
            if (selectionManager.getBlocks(playerId).size() >= 2) {
                int[][] refPoints = selectionManager.getRefPoints(playerId);

                // TODO: Complete
                int highestBlock = findHighestBlock(refPoints, world);

                // Gather all blocks in the cubic area
                // TODO: We can simplyfy this to O(n^2) by using highest block
                for (int Z = selectionManager.lazyMin(refPoints[2]); Z <= selectionManager.lazyMax(refPoints[2]); Z++) {
                    for (int Y = selectionManager.lazyMin(refPoints[1]) - 2; Y <= highestBlock + 2; Y++) {
                        for (int X = selectionManager.lazyMin(refPoints[0]); X <= selectionManager.lazyMax(refPoints[0]); X++) {
                            selectionManager.addMaterial(playerId, world.getBlockAt(X, Y, Z).getType());
                        }
                    }
                }

                int selectionSize = selectionManager.getSelectionSize(playerId);
                boolean selectionSizeTooLarge = selectionSize > main.getConfig().getInt("MaxPropertySize");
                boolean selectionContainsAllRequiredMaterials = selectionManager.getMaterials(playerId).containsAll(Arrays.asList(
                    Material.OAK_DOOR, Material.WHITE_BED, Material.CRAFTING_TABLE));
                boolean playerHasEnoughMoneyForHouse = playerBalance >= main.getConfig().getInt("HomeRegistrationFee");      


                // Logic checks to make sure the player can purchase a home
                // ===================================================================================================================
                if (playerHasTooManyHomes(player)) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You have too many homes...");
                    selectionManager.clearAll(playerId);
                    return;
                }

                if (selectionSizeTooLarge) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You are " +
                        ChatColor.YELLOW + (selectionSize - main.getConfig().getInt("MaxPropertySize")) +
                        ChatColor.RED + " blocks over the maximum property size of " +
                        ChatColor.YELLOW + main.getConfig().getInt("MaxPropertySize"));
                    particles(player, Particle.SMOKE);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    selectionManager.clearAll(playerId);
                    return;
                }

                if(!selectionContainsAllRequiredMaterials) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Missing requirements to be considered a home...");
                    player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Items needed for a home: Oak door, White bed, Crafting table");
                    particles(player, Particle.SMOKE);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    selectionManager.clearAll(playerId);
                    return;
                }

                if(!playerHasEnoughMoneyForHouse) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Insufficient funds, must have at least: " +
                        ChatColor.GREEN + "$" + main.getConfig().getInt("HomeRegistrationFee"));
                    particles(player, Particle.SMOKE);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    selectionManager.clearAll(playerId);
                    return;
                }

                if(main.getConfig().getBoolean("GriefPreventionBridge")) {
                    try{
                    if(!selectionOnPlayerProperty(player, player.getLocation())){
                        player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You must be on your claim to purchase a home...");
                        particles(player, Particle.SMOKE);
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                        selectionManager.clearAll(playerId);
                        return;
                    }   
                } catch (NoClassDefFoundError e) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "GriefPrevention plugin is not enabled on this server.");
                    player.sendMessage(main.getPluginPrefix() + "Landlord can still work, just change the config.yml to set 'GriefPreventionBridge' to false.");
                    particles(player, Particle.SMOKE);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    selectionManager.clearAll(playerId);
                    return;
                }
                }

                if(selectionOverlapsProperty(refPoints, highestBlock)) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Selection overlaps another property...");
                    particles(player, Particle.SMOKE);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    selectionManager.clearAll(playerId);
                    return;
                }
                // ===================================================================================================================

                purchaseSuccess(player, refPoints);
                selectionManager.clearAll(playerId);
            }
        }
    }

    // Utility methods
    // ===================================================================================================================
    private int lazyMin(int[] arr) {
        return (Arrays.stream(arr).min().getAsInt());
    }

    
    private int lazyMax(int[] arr) {
        return (Arrays.stream(arr).max().getAsInt());
    }

    
    private void particles(Player player, Particle particle) {
    	World world = player.getWorld();
    	for(int i = 0; i < 360; i+=20) {
    		Location refLocal = player.getLocation();
    		refLocal.setX(refLocal.getX() + Math.cos(Math.toRadians(i)));
    		refLocal.setY(refLocal.getY() + Math.sin(Math.toRadians(i)));
    		world.spawnParticle(particle, refLocal, 10);
    	}
    }

    private void purchaseSuccess(Player player, int refPoints[][]) {
        UUID playerId = player.getUniqueId();
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        int highestBlock = findHighestBlock(refPoints, world);
    	main.getEconomy().withdrawPlayer(player, main.getConfig().getDouble("HomeRegistrationFee"));
    	player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Deed purchased sucessfully!");
    	player.sendMessage(main.getPluginPrefix() + String.format(ChatColor.translateAlternateColorCodes('&', "Your balance is now: &f[&a$%.2f&f]"), main.getEconomy().getBalance(player)));
    	particles(player, Particle.CLOUD);
    	player.playSound(playerLocation, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
    	new Renter(main).RenterEgg(player);
    	player.sendTitle(ChatColor.GREEN + "Congratulations!", ChatColor.GRAY + "You now own a Home!", 5, 40, 5);
    	player.playSound(playerLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    	ArrayList<Integer> xRange = new ArrayList<Integer>(java.util.List.of(lazyMin(refPoints[0]), lazyMax(refPoints[0])));
    	ArrayList<Integer> yRange = new ArrayList<Integer>(java.util.List.of(lazyMin(refPoints[1]), highestBlock));
    	ArrayList<Integer> zRange = new ArrayList<Integer>(java.util.List.of(lazyMin(refPoints[2]), lazyMax(refPoints[2])));
    	this.main.getPlayerPropertiesFile().saveJson(new propertyJSON(world.getName(), playerId, xRange, yRange, zRange));
    }
    
    private boolean selectionOverlapsProperty(int[][] refPoints, int highestBlock) {
    List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
    if (playerProperties == null) {
        return false;
    }
    for (propertyJSON playerProperty : playerProperties) {
        boolean inRangeX = !(lazyMax(refPoints[0]) < playerProperty.xRange.get(0) || lazyMin(refPoints[0]) > playerProperty.xRange.get(1));
        boolean inRangeY = !(highestBlock < playerProperty.yRange.get(0) || lazyMin(refPoints[1]) > playerProperty.yRange.get(1));
        boolean inRangeZ = !(lazyMax(refPoints[2]) < playerProperty.zRange.get(0) || lazyMin(refPoints[2]) > playerProperty.zRange.get(1));
        if (inRangeX && inRangeY && inRangeZ) {
            return true;
        }
    }
    return false;
}

    private boolean selectionOnPlayerProperty(Player player, Location location){
        Claim checkedClaim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        return checkedClaim != null && checkedClaim.getOwnerName().equals(player.getName());
    }

    private boolean playerHasTooManyHomes(Player player) {
    	List<propertyJSON> playerProperties = main.getPlayerPropertiesFile().loadJson(propertyJSON.class);
    	if(playerProperties == null) {
    		return false;
    	}
    	int homeCount = 0;
    	for(propertyJSON property: playerProperties) {
    		if(property.playerUUID.equals(player.getUniqueId())) {
    			homeCount++;
    		}
    	}
    	return(homeCount >= 9);
    }

    private int findHighestBlock(int[][] selectionPoints, World world){

        int highestBlock = -70;

        for (int Z = selectionManager.lazyMin(selectionPoints[2]); Z <= selectionManager.lazyMax(selectionPoints[2]); Z++) {
                        for (int X = selectionManager.lazyMin(selectionPoints[0]); X <= selectionManager.lazyMax(selectionPoints[0]); X++) {
                            Block highestBlockInWorld = world.getHighestBlockAt(X, Z);
                            if(highestBlockInWorld.getY() > highestBlock){
                                highestBlock = highestBlockInWorld.getY();
                            }
                        }
                    
                }

                return highestBlock;
    }
    // ===================================================================================================================
}

