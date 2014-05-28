package me.cpanda.UHC;

import java.util.*;

import me.cpanda.UHC.commands.GameCommands;
import me.cpanda.UHC.commands.UtilCommands;
import me.cpanda.UHC.enums.GameState;
import me.cpanda.UHC.listeners.MessageListener;
import me.cpanda.UHC.listeners.MobDeathListener;
import me.cpanda.UHC.listeners.PlayerConnectionListener;
import me.cpanda.UHC.listeners.PlayerDeathListener;
import me.cpanda.UHC.theGame.GameControl;
import me.cpanda.UHC.theGame.TeamController;
import me.cpanda.UHCWorldGen.UHCWorld;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/* Overall ToDos:
 * TODO: Spawn area
 * TODO: Spectator mode
 */

/**
 * 
 * @author CacklingPanda
 * 
 * The main UHC class that starts up the whole plugin and sets up 
 * initial conditions. 
 *
 */
public class UHC extends JavaPlugin {
	
	// The controller! Used to control things.
	private static GameControl controller;
	
	
	/**
	 * Check to see if this plugin has been run before and whether there are already
	 * conditions for game. Also check to see if a game is currently in process or
	 * not. Then basically setup the whole system and get that running.
	 */
	@Override
	public void onEnable() {
		Server server = this.getServer();
		
		// Show console that we are loading the UHC plugin
		server.getLogger().info("--- LOADING CiCi's UHC PLUGIN ---");
		server.getLogger().info("--- Edit the config.yml to change settings or do so in-game ---");
		server.getLogger().info("--- A tutorial is available at cpanda.me if needed ---");
		
		// Remove default crafting recipe for golden apple and for glistering melon
		for (Iterator<Recipe> recipeIterator = server.recipeIterator(); recipeIterator.hasNext();) {
			Recipe recipe = recipeIterator.next();
			if (recipe.getResult().getType() == Material.GOLDEN_APPLE) {
				//recipeIterator.remove();
			} else if (recipe.getResult().getType() == Material.SPECKLED_MELON) {
				recipeIterator.remove();
			}
		}
		
		// Set new crafting recipe for golden apple
		ShapedRecipe goldApple = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE));
		goldApple.shape(
				"GGG",
				"GAG",
				"GGG");
		goldApple.setIngredient('G', Material.GOLD_INGOT);
		goldApple.setIngredient('A', Material.APPLE);
		
		// Set new crafting recipe for glistering melon
		ShapelessRecipe glisMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
		glisMelon.addIngredient(1, Material.MELON);
		glisMelon.addIngredient(1, Material.GOLD_BLOCK);
		
		// Add the goldApple recipe and the glisMelon recipe
		server.addRecipe(goldApple);
		server.addRecipe(glisMelon);
		
		// Save default config file if one doesn't exist
		this.saveDefaultConfig();
		
		// Get main variables from the config file
		FileConfiguration config = this.getConfig();
		
		// Get the world variables from the config file
		int worldRadius = config.getInt("GeneralOptions.worldRadius");
		int eternalDay = config.getInt("GeneralOptions.eternalDay");
		boolean generateBedrock = config.getBoolean("GeneralOptions.generateBedrock");
		boolean boundNether = config.getBoolean("GeneralOptions.boundNether");
		boolean pregenChunks = config.getBoolean("GeneralOptions.pregenChunks");
		
		// The the world into a uhcWorld
		UHCWorld uhcWorld = UHCWorld.loadWorld(worldRadius,generateBedrock, pregenChunks, boundNether, eternalDay, server);
		
		// Initiate GameControl and TeamControl class
		int numTeams = config.getInt("TeamOptions.numberOfTeams");
		int teamSizes= config.getInt("TeamOptions.teamSizes");

		TeamController teamController = new TeamController(this, teamSizes, numTeams);
		
		GameState gameState = GameState.intToGameState(config.getInt("GeneralOptions.gameState"));
		if(gameState.equals(GameState.ENDING)) gameState = GameState.STARTING;
		
		// Set the world to the desired state
		if(gameState.equals(GameState.STARTING))
			uhcWorld.setPreUHCRules();
		else
			uhcWorld.setUHCRules();
		
		int timePassed = config.getInt("GeneralOptions.timePassed");
		int clockSpeed = config.getInt("GeneralOptions.clockSpeed");
		boolean spectate = config.getBoolean("GeneralOptions.spectate");
		
		controller = new GameControl(this, teamController, uhcWorld, timePassed, clockSpeed, spectate, gameState);
		
		// Register all events	
		server.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
		server.getPluginManager().registerEvents(new MessageListener(this), this);
		server.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		server.getPluginManager().registerEvents(new MobDeathListener(this), this);
		
		// Register all commands
		UtilCommands utilCmdListener = new UtilCommands(this);
		this.getCommand("freeze").setExecutor(utilCmdListener);
		this.getCommand("heal").setExecutor(utilCmdListener);
		
		GameCommands gameCmdListener = new GameCommands(this);
		this.getCommand("start").setExecutor(gameCmdListener);
		this.getCommand("end").setExecutor(gameCmdListener);
		this.getCommand("join").setExecutor(gameCmdListener);
		this.getCommand("leave").setExecutor(gameCmdListener);
		this.getCommand("teams").setExecutor(gameCmdListener);
		this.getCommand("fixbedrock").setExecutor(gameCmdListener);
		this.getCommand("g").setExecutor(gameCmdListener);
		this.getCommand("reset").setExecutor(gameCmdListener);
		this.getCommand("cancel").setExecutor(gameCmdListener);
		this.getCommand("randomize").setExecutor(gameCmdListener);
		this.getCommand("teamf").setExecutor(gameCmdListener);
		this.getCommand("clearteams").setExecutor(gameCmdListener);
		this.getCommand("set").setExecutor(gameCmdListener);
	}
	
	/**
	 * Get the game controller!
	 * @return
	 */
	public static GameControl getController() {
		return controller;
	}
}
