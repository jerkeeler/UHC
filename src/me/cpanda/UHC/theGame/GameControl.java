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
	private CountdownTimer countdown;
	
	/**
	 * Constructor 
	 * 
	 * @param plugin The plugin, to get the server and such
	 * @param teamController The thing that controls the teams!
	 * @param UHCWorld The wolrd in which the game takes place
	 * @param timePassed The time that has passed so far in the game
	 * @param clockSpeed the time, in minutes, for which a broadcast should be sent out
	 * @param spectate Boolean if spectating is allowed or not
	 * @param gameState The state of the current game.
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
		countdown = null;
		
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
			// Start the countdown
			countdown = new CountdownTimer(30, plugin, "Match is starting in #{COUNTDOWN} seconds!");
			saveTheConfig();
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
		updateVis(true);
		if(countdown != null)
			countdown.cancelCountdown();
		
		// If the game is running
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
			saveTheConfig();
			
			return true;
		}
		
		return false;
	}
	
	/** 
	 * Restart the game! In case something bad happens
	 * 
	 * @return boolean true if game is restarted, false otherwise
	 */
	public boolean resetGame() {
		if(gameState.equals(GameState.STARTING))
			return false;
		
		// cancel countdown if present
		if(countdown != null)
			countdown.cancelCountdown();
		
		// Clear teams
		Utils.clearInventories(plugin);
		Utils.healPlayers(plugin);
		
		// Set world to preUHCRules and cancel gametimer
		uhcWorld.setPreUHCRules();
		uhcWorld.generateSpawnPlatform(plugin.getServer());
		uhcWorld.getWorld().setSpawnLocation(0, 247, 0);
		
		// Teleport everyone to spawn
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			p.teleport(uhcWorld.getWorld().getSpawnLocation());
			p.setGameMode(GameMode.ADVENTURE);
		}
		
		// Reset game stuff
		if(gameState.equals(GameState.ACTIVE))
			Bukkit.getScheduler().cancelTask(timerID);	
		timePassed = 0;
		gameState = GameState.STARTING;
		saveTheConfig();
		
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The game has been " + ChatColor.DARK_GREEN + "RESTARTED" + 
				ChatColor.AQUA + "!");
		
		return true;
	}
	
	/**
	 * Cancel the current countdown
	 * 
	 * @return true if cancelled, false otherwise
	 */
	public boolean cancelCountdown() {
		if(countdown != null) {
			// Cancel timer and reset gamestate
			gameState = GameState.STARTING;
			saveTheConfig();
			countdown.cancelCountdown();
			Utils.clearInventories(plugin);
			Utils.healPlayers(plugin);
			uhcWorld.setPreUHCRules();
			
			// Teleport all players to spawn
			for(Player p : plugin.getServer().getOnlinePlayers()) {
				p.teleport(uhcWorld.getWorld().getSpawnLocation());
			}
			return true;
		} else {
			return false;
		}
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
	 * Randomize the teams with all online players
	 * @return
	 */
	public boolean randomizeTeams() {
		if(gameState.equals(GameState.ACTIVE) || gameState.equals(GameState.ENDING))
			return false;
		
		plugin.getServer().broadcastMessage(ChatColor.GOLD + "Randomizing teams!");
		teamControl.shuffle();
		return true;
	}
	
	/**
	 * Force a player on to a particular team
	 * 
	 * @param playerName The name of the player to force
	 * @param teamName The name of the team you want to force them to
	 * @return boolean true if forced, false otherwise
	 */
	public boolean teamForce(String playerName, String teamName) {
		if(plugin.getServer().getPlayer(playerName) == null)
			return false;
		
		Player player = plugin.getServer().getPlayer(playerName);
		leaveTeam(player);
		joinTeam(teamName, player);		
		return true;
	}
	
	/**
	 * Update the visibility of the observers
	 */
	public void updateVis(boolean viewAll) {
		teamControl.updateVisibility(viewAll);
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
	 * Set the countdown timer to null!
	 */
	public void clearCountdownTimer() {
		countdown = null;
	}
	
	/**
	 * Clear teams
	 * 
	 * @return boolean true if teams are cleared
	 */
	public boolean clearTeams() {
		if(gameState.equals(GameState.STARTING)) {
			teamControl.cleanseTeams();
			return true;
		} 
		
		return false;
	}
	
	/**
	 * Set the team sizes
	 * 
	 * @param teamSize the number of players per team
	 */
	public void setTeamSizes(int teamSize) {
		teamControl.setTeamSizes(teamSize);
	}
	
	/**
	 * Set the number of teams
	 * 
	 * @param numTeams the number of teams to play
	 */
	public void setNumTeams(int numTeams) {
		teamControl.setNumTeams(numTeams);
	}
	
	/**
	 * Save the configuration based on the current variables
	 */
	public void saveTheConfig() {
		plugin.getConfig().set("GeneralOptions.gameState", GameState.gameStateToInt(gameState));
		plugin.getConfig().set("TeamOptions.numberOfTeams", teamControl.getNumTeams());
		plugin.getConfig().set("TeamOptions.teamSizes", teamControl.getTeamSizes());
		plugin.saveConfig();
	}
	
	/**
	 * Increment the mob kill count by one for the specified player
	 * 
	 * @param player the player who killed a mob
	 */
	public void incrementMobKillCount(Player player) {
		teamControl.incrementMobKillCount(player);
	}
	
	public void showMobKills() {
		teamControl.showMobKills();
	}
	
	public void showPlayerKills() {
		teamControl.showPlayerKills();
	}
}
