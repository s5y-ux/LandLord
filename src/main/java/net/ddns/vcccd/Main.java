package net.ddns.vcccd;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin{
	
	private ConsoleCommandSender console = getServer().getConsoleSender();
	private String prefix = ChatColor.translateAlternateColorCodes('&', "&7[&aLandLord&7] - ");
	private static Economy econ = null;
    private static Permission perms = null;
    private File propertyLocations;
    private JSONManager playerPropertiesFile = new JSONManager(new File("plugins/LandLord/PropertyLocations.json"));
	
    public JSONManager getPlayerPropertiesFile() {
    	return this.playerPropertiesFile;
    }
    
	public ConsoleCommandSender getMainConsole() {
		return(console);
	}
	
	public Economy getEconomy() {
		return(econ);
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public String getPluginPrefix() {
    	return(prefix);
    }
    
    public File getPlayerPropertyLocationsFile() {
    	return this.propertyLocations;
    }
	
	@Override
	public void onEnable() {	
		new CollectRent(this).runTaskTimer(this, 0L, 24000L);
		FileConfiguration config = this.getConfig();

		config.addDefault("HomeRegistrationFee", 100.00);
		config.addDefault("DaysBeforeRent", 1);
		config.addDefault("RentMoney", 100.00);
		config.addDefault("SellAmount", 50.00);
        config.addDefault("MaxPropertySize", 1000);
		this.saveDefaultConfig();
		if (!setupEconomy()) {
            console.sendMessage(prefix + ChatColor.RED + "Vault not found! Disabling plugin...");
            console.sendMessage(prefix + ChatColor.YELLOW + "Please install Vault to use this plugin.");
            console.sendMessage(prefix + ChatColor.YELLOW + "https://www.spigotmc.org/resources/vault.34315/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        getServer().getPluginManager().registerEvents(new PropertyWandEvents(this), this);
        getServer().getPluginManager().registerEvents(new RenterRemoverWandEvents(this), this);
        getServer().getPluginManager().registerEvents(new Renter(this), this);
        getServer().getPluginManager().registerEvents(new RenterEvents(), this);
        getServer().getPluginManager().registerEvents(new DeedMenu(this), this);
        getServer().getPluginManager().registerEvents(new PropertyGriefPrevention(this), this);
        getServer().getPluginManager().registerEvents(new UpdateChecker(), this);
        this.getCommand("managehouses").setExecutor(new DeedMenu(this));
        this.getCommand("property").setExecutor(new PropertyWand(this));
        this.getCommand("removerenter").setExecutor(new RenterRemoverWand());
        
        this.propertyLocations = new File("plugins/LandLord/PropertyLocations.json");
        if(!propertyLocations.exists()) {
			try {
				if(propertyLocations.createNewFile()) {
					console.sendMessage(prefix + "File path created, no errors!");
				} else {
					console.sendMessage(prefix + ChatColor.RED + "An error in Main! Please report this issue!");
				}
			} catch (IOException e) {
				console.sendMessage(prefix + ChatColor.RED + "FATAL ERROR! May be operating system permissions?");
			}
		}
        
		console.sendMessage(getPluginPrefix() + "The Landlord Plugin has beed Loaded...");
		console.sendMessage(getPluginPrefix() + "Please note that this does not mean all features will work");
		console.sendMessage(getPluginPrefix() + "If issues are found with the plugin, report the issue to:");
		console.sendMessage(getPluginPrefix() + ChatColor.GREEN + "https://github.com/s5y-ux/LandLord/issues");
		console.sendMessage(getPluginPrefix() + "Trust me, I WILL see it");
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
}
