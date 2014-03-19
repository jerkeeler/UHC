package me.cpanda.UHC;

import java.util.*;

import me.cpanda.UHC.commands.GameCommands;
import me.cpanda.UHC.commands.UtilCommands;
import me.cpanda.UHC.enums.GameState;
import me.cpanda.UHC.listeners.PlayerConnectionListener;
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
 * TODO: Pre-gen world and bedrock wall
 * TODO: Spawn area
 * TODO: Spectator mode
 * TODO: Freeze players
 * TODO: Timer!!!!
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
	/* 
	 * The most important variables for this plugin. All of them are stored in the
	 * config.yml file in case the server crashes during play.
	 * 
	 * gameState - a GameState representing whether the game is in the pre-game(STARTING), currently
	 * playing (ACTIVE), or in the post-game (ENDING) 
	 * 
	 * timePassed - the current amount of IRL time that has passed since the start of the
	 * game, the clock
	 */
	public static GameState gameState;
	public static int timePassed, eternalDay, teamSizes, clockSpeed;
	
	public static boolean spectate;
	
	public static UHCWorld uhcWorld;
	public static GameControl controller;
	public static UHC plugin;
	
	
	/**
	 * Check to see if this plugin has been run before and whether there are already
	 * conditions for game. Also check to see if a game is currently in process or
	 * not. Then basically setup the whole system and get that running.
	 */
	@Override
	public void onEnable() {
		Server server = this.getServer();
		UHC.plugin = this;
		
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
		gameState = GameState.intToGameState(config.getInt("GeneralOptions.gameState"));
		if(gameState.equals(GameState.ENDING)) gameState = GameState.STARTING;
		
		timePassed = config.getInt("GeneralOptions.timePassed");
		eternalDay = config.getInt("GeneralOptions.eternalDay");
		spectate = config.getBoolean("GeneralOptions.spectate");
		clockSpeed = config.getInt("GeneralOptions.clockSpeed");
		
		// Get the world variables from the config file
		String worldName = config.getString("GeneralOptions.worldName");
		int worldRadius = config.getInt("GeneralOptions.worldRadius");
		boolean generateBedrock = config.getBoolean("GeneralOptions.generateBedrock");
		
		// Get the world with worldName and if none exists create it and generate it
		if(server.getWorld(worldName) == null) {
			uhcWorld = UHCWorld.createWorld(worldName, worldRadius, server, generateBedrock);
		} else {
			uhcWorld = UHCWorld.loadWorld(worldName, worldRadius, server, generateBedrock);
		}
		
		// Initiate GameControl and TeamControl class
		int numTeams = config.getInt("TeamOptions.numberOfTeams");
		teamSizes= config.getInt("TeamOptions.teamSizes");

		TeamController teamController = new TeamController(this, teamSizes, numTeams);
		
		controller = new GameControl(this, teamController);
		
		// Register all events
		PlayerConnectionListener connectionListener = new PlayerConnectionListener(this);
		
		server.getPluginManager().registerEvents(connectionListener, this);
		server.getPluginManager().registerEvents(connectionListener, this);
		
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
	}
}
