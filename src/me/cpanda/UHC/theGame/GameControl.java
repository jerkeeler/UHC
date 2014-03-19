package me.cpanda.UHC.theGame;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;
import me.cpanda.UHC.timers.CountdownTimer;
import me.cpanda.UHC.timers.GameplayTimer;

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
	private UHC plugin;
	private TeamController teamControl;
	private int timerID;
	
	public GameControl(UHC plugin, TeamController teamController) {
		this.plugin = plugin;
		this.teamControl = teamController;
		timerID = 0;
	}
	
	/**
	 * Stuff to run when the game starts
	 * 
	 * @return boolean true if game started, false otherwise TODO: move to another class?
	 */
	public boolean startGameCommand() {
		// If pre-game
		if(UHC.gameState.equals(GameState.STARTING)) {			
			// Insert countdown
			new CountdownTimer(20, plugin, "Match is starting in #{COUNTDOWN} seconds!");
			timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new GameplayTimer(plugin), 0, 20);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Stuff to run when the game ends
	 * 
	 * @return boolean true if game ended, false otherwise 
	 */
	public boolean endGameCommand() {
		// If pre-game
		if(UHC.gameState.equals(GameState.ACTIVE)) {
			// Set gamemodes
			for(Player player : plugin.getServer().getOnlinePlayers()) {
				player.setGameMode(GameMode.CREATIVE);
			}
			
			// Create new world
			// Teleport
			
			// Add broadcast
			Bukkit.getScheduler().cancelTask(timerID);
			// Deny block placement/breakage while in creative TODO: Actually, maybe not? Config option?
			plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "GAME HAS " 
					+ ChatColor.DARK_RED + "ENDED" + ChatColor.DARK_GREEN + "!");
			
			UHC.gameState = GameState.STARTING; // TODO: Change to ENDING WHEN DONE
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add a player to a team, either random or specified
	 * 
	 * @return boolean true if added to a team
	 */
	public boolean joinTeamCommand(Player player) {
		return joinTeamCommand("RANDOM", player);
	}

	/**
	 * Add a player to a team, either random or specified
	 * 
	 * @return boolean true if added to a team
	 */
	public boolean joinTeamCommand(String teamPref, Player player) {
		if(teamPref.equalsIgnoreCase("RANDOM")) {
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
	public boolean leaveTeamCommand(Player player) {
		return teamControl.leaveTeam(player);
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
}
