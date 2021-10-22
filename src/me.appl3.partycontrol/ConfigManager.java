package me.appl3.partycontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ConfigManager {
	private PartyControl plugin = PartyControl.getPlugin(PartyControl.class);
	
	// File Configurations
	public FileConfiguration backpackConfig;
	
	// Files
	public File backpackFile;
	
	// File Names
	public String backpack = "backpacks.yml";
	
	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir(); // Create plugin directory if not found.
		}
		
		backpackFile = new File(plugin.getDataFolder(), backpack);
		
		checkFile(backpackFile, backpack);
		
		backpackConfig = YamlConfiguration.loadConfiguration(backpackFile);
	}
	
	public void checkFile(File file, String fileName) {
		if (!file.exists()) {
			try {
				file.createNewFile();
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&aThe " + fileName + " file has been created!"));
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&cCould not create the " + fileName + " file!"));
			}
		}
	}
	
	// Getting Configurations
	
	public FileConfiguration getBackpacks() {
		return backpackConfig;
	}
	
	// Saving Configurations
	
	public void saveBackpacks() {
		try {
			backpackConfig.save(backpackFile);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&aThe " + backpack + " file has been saved!"));
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cCould not save the " + backpack + " file!"));
		}
	}
	
	// Reloading Configurations
	
	public void reloadBackpacks() {
		backpackConfig = YamlConfiguration.loadConfiguration(backpackFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&aThe " + backpack + " file has been reloaded!"));
	}
	
	// Modifying Configurations

	public int getNumberOfBackpacks() {
		try {
			Set<String> backpacks = getBackpacks().getConfigurationSection("backpacks").getKeys(false);
			return backpacks.size();
		} catch (Exception e) { }
		return 0;
	}

	public boolean doesExist(int id) { return getBackpacks().isSet("backpacks." + id); }

	public void addToBackpack(ItemStack item, int index, int id) { getBackpacks().set("backpacks." + id + "." + index, item); }
	
	// Other Functions
	
	// Converts ampersands into symbols used for color coding.
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
