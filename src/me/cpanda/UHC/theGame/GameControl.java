package me.cpanda.UHC.theGame;

import java.util.*;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.Utils;
import me.cpanda.UHC.enums.GameState;
import me.cpanda.UHC.timers.CountdownTimer;
import me.cpanda.UHCWorldGen.UHCWorld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * @author CacklingPanda
 * 
 * Controls everything related to the game!!!! MUAHAHAHAAHAHA
 *
 */
public class GameControl {	
	private String notOnTeamMessage = "You are " + ChatColor.DARK_RED + "not " + ChatColor.RESET + "on a team!";
	
	private UHC plugin;
	private GameState gameState;
	private TeamController teamControl;
	private UHCWorld uhcWorld;
	private int timerID, timePassed, clockSpeed;;	
	private boolean spectate;
	private ArrayList<Player> dead;
	
	/**
	 * Constructor 
	 * 
	 * @param plugin The plugin, to get the server and such
	 * @param teamController The thing that controls the teams!
	 */
	public GameControl(UHC plugin, TeamController teamController, UHCWorld uhcWorld, int timePassed, int clockSpeed, boolean spectate,
			GameState gameState) {
		this.plugin = plugin;
		this.teamControl = teamController;
		this.uhcWorld = uhcWorld;
		this.timePassed = timePassed;
		this.clockSpeed = clockSpeed;
		this.spectate = spectate;
		this.gameState = gameState;
		
		// Initialize timerID
		timerID = 0;
	}
	
	/**
	 * Stuff to run when the game starts
	 * 
	 * @return boolean true if game started, false otherwise
	 */
	public boolean startGame() {
		// If pre-game
		if(gameState.equals(GameState.STARTING)) {			
			// TODO: Write teams to config file
			FileConfiguration config = plugin.getConfig();
			// Insert countdown
			new CountdownTimer(30, plugin, "Match is starting in #{COUNTDOWN} seconds!");
			config.set("GeneralOptions.gameState", 0);
			plugin.saveConfig();
	
			return true;
		}
		
		return false;
	}
	
	/**
	 * Stuff to run when the game ends
	 * 
	 * @return boolean true if game ended, false otherwise 
	 */
	public boolean endGame() {
		// If pre-game
		if(gameState.equals(GameState.ACTIVE)) {
			// Set gamemodes
			for(Player player : plugin.getServer().getOnlinePlayers()) {
				player.setGameMode(GameMode.CREATIVE);
			}
			
			// Cancel game timer
			Bukkit.getScheduler().cancelTask(timerID);
			
			// Deny block placement/breakage while in creative
			plugin.getServer().broadcastMessage(ChatColor.AQUA + "GAME HAS " 
					+ ChatColor.DARK_RED + "ENDED" + ChatColor.AQUA + "!");
			
			// Set gamestate to ending
			gameState = GameState.ENDING;
			plugin.getConfig().set("GeneralOptions.gameState", 1);
			plugin.saveConfig();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add a player to a team, either random or specified
	 * 
	 * @return boolean true if added to a team
	 */
	public boolean joinTeam(Player player) {
		return joinTeam("RANDOM", player);
	}

	/**
	 * Add a player to a team, either random or specified
	 * 
	 * @return boolean true if added to a team
	 */
	public boolean joinTeam(String teamPref, Player player) {
		// If they want to join observers and the game isn't active
		if(teamPref.equalsIgnoreCase("obs")) {
			spectate(player);
			return true;
		}
		
		// Make sure it's actually possible to join a team first!
		if(gameState.equals(GameState.ACTIVE)) {
			player.sendMessage("The game is " + ChatColor.DARK_RED + "in progress" + ChatColor.RESET + "!");
			return true;
		} else if(teamPref.equalsIgnoreCase("RANDOM")) {
			return teamControl.joinRandomTeam(player);
		} else {
			return teamControl.joinTeam(teamPref, player);
		}
	}
	
	/**
	 * Takes the player and removes them from their team
	 * 
	 * @param player The player to remove
	 * @return boolean true if removed from team
	 */
	public boolean leaveTeam(Player player) {
		if(!teamControl.onTeam(player))
			player.sendMessage(notOnTeamMessage);
		// Can't leave a team if game is active!
		else if(gameState.equals(GameState.ACTIVE)) {
			player.sendMessage("The game is " + ChatColor.DARK_RED + "in progress" + ChatColor.RESET + "!");
		} else 
			return teamControl.leaveTeam(player);
		
		return true;
	}
	
	/**
	 * Set the scoreboard for the specified player
	 * 
	 * @param player The player
	 */
	public void setScoreboard(Player player) {
		teamControl.setScoreboard(player);
	}
	
	/**
	 * Print the teams!
	 * 
	 * @param sender Person to tell
	 */
	public boolean printTeams(CommandSender sender) {
		return teamControl.printTeams(sender);
	}
	
	/**
	 * Freeze the teams for a specified amount of time
	 * 
	 * @param seconds The length of the freeze
	 */
	public void freezeTeams(int seconds) {
		teamControl.freeze(seconds);
	}
	
	/**
	 * Teleport teams to random location
	 */
	public void teleportTeams() {
		teamControl.teleportTeams();
	}
	
	/**
	 * Set the timer id for the main gameplay timer
	 * 
	 * @param id The id for the timer
	 */
	public void setTimerID(int id) {
		timerID = id;
	}
	
	/**
	 * Get the uhcWorld object
	 * 
	 * @return UHCWorld current world
	 */
	public UHCWorld getUHCWorld() {
		return uhcWorld;
	}
	
	/**
	 * Get the game state
	 * 
	 * @return GameState the game state!
	 */
	public GameState getGameState() {
		return gameState;
	}
	
	/**
	 * Set the gameState
	 * 
	 * @param gameState The game state
	 */
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	/**
	 * Return spectate value
	 * 
	 * @return true if you can spectate, false otherwise
	 */
	public boolean canSpectate(){
		return spectate;
	}

	/**
	 * Get game length so far
	 * 
	 * @return int time passed
	 */
	public int getTimePassed() {
		return timePassed;
	}
	
	/**
	 * Add time to the timePassed
	 * 
	 * @param elapsedTime The amount of time passed to be added
	 */
	public void addTimePassed(int elapsedTime) {
		timePassed += elapsedTime;
	}
	
	/**
	 * Return the clock speed
	 * 
	 * @return int the cockspeed
	 */
	public int getClockSpeed() {
		return clockSpeed;
	}
	
	/**
	 * Get the team the player is on
	 * @param player
	 * @return
	 */
	public Team getPlayerTeam(Player player) {
		return teamControl.getPlayerTeam(player);
	}
	
	/**
	 * Add the player to the observer team
	 * 
	 * @param player
	 */
	public void spectate(Player player) {
		teamControl.joinTeam("Observer", player);
	}
	
	/**
	 * Check to see if the player is on a team
	 * 
	 * @param player The player to check if they are on a team
	 * @return boolean true if they are and false otherwise
	 */
	public boolean isOnTeam(Player player) {
		return teamControl.onTeam(player);
	}
	
	/**
	 * Is the player dead? Check to see if they are or not
	 * 
	 * @param player The player to check if they are dead
	 * @return boolean yes if dead
	 */
	public boolean isDead(Player player) {
		Iterator<Player> deads = dead.iterator();
		while(deads.hasNext()) {
			Player p = deads.next();
			if(p.getName().equalsIgnoreCase(player.getName()))
				return true;
		}
		return false;
	}

	/**
	 * Send a message to all players
	 * 
	 * @param args The array of arguments
	 * @return true
	 */
	public boolean talkGlobal(Player player, String[] args) {
		
		// Make message
		String message = "<" + player.getDisplayName() + ChatColor.RESET + ">: ";
		for(int i = 0; i < args.length; i++) {
			message += args[i] + " ";
		}
		
		// Send message to all online players
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			p.sendMessage(message);
		}
		plugin.getServer().getLogger().info(message);
		return true;
	}
	
	/**
	 * Get the team sizes
	 * @return int the number of players per team
	 */
	public int getTeamSizes() {
		return teamControl.getTeamSizes();
	}
	
	/**
	 * Get the teams
	 * 
	 * @return Get the teams
	 */
	public Set<Team> getTeams() {
		return teamControl.getTeams();
	}
	
	/**
	 * Restart the game! In case something bad happens
	 * 
	 * @return boolean true if game is restarted, false otherwise
	 */
	public boolean restartGame() {
		if(gameState.equals(GameState.STARTING))
			return false;
		
		teamControl.cleanseTeams();
		Utils.clearInventories(plugin);
		Utils.healPlayers(plugin);
		
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			p.teleport(uhcWorld.getWorld().getSpawnLocation());
		}
		
		uhcWorld.setPreUHCRules();
		if(gameState.equals(GameState.ACTIVE))
			Bukkit.getScheduler().cancelTask(timerID);
		
		gameState = GameState.STARTING;
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The game has been " + ChatColor.DARK_GREEN + "RESTARTED" + 
				ChatColor.AQUA + "!");
		
		return true;
	}
}
