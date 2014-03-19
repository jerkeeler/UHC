package me.cpanda.UHC.theGame;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;
import me.cpanda.UHC.timers.CountdownTimer;
import me.cpanda.UHCWorldGen.UHCWorld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	 * @return boolean true if game started, false otherwise TODO: move to another class?
	 */
	public boolean startGame() {
		// If pre-game
		if(gameState.equals(GameState.STARTING)) {			
			// Insert countdown
			new CountdownTimer(30, plugin, "Match is starting in #{COUNTDOWN} seconds!");
			
			// TODO: Write teams to config
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
			
			// Deny block placement/breakage while in creative TODO: Actually, maybe not? Config option?
			plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "GAME HAS " 
					+ ChatColor.DARK_RED + "ENDED" + ChatColor.DARK_GREEN + "!");
			
			// Set gamestate to ending
			gameState = GameState.ENDING;
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
}
