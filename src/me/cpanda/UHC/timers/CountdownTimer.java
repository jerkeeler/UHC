package me.cpanda.UHC.timers;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.Utils;
import me.cpanda.UHC.enums.GameState;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author CacklingPanda
 *
 * Arbitrary countdown timer! Specify a time and second and watch it go!
 */
public class CountdownTimer {
	private int timerID;	
	private UHC plugin;
	private int timeLeft;
	
	/**
	 * Constructor
	 * 
	 * @param seconds The length of the countdown
	 * @param plugin The plugin, to ge the server
	 * @param msg The message to be played every second / 5 seconds
	 */
	public CountdownTimer(int seconds, UHC plugin, String msg) {
		this(0, seconds, plugin, msg);
	}
	
	/**
	 * Constructor 
	 * 
	 * @param delay The delay before starting the countdown
	 * @param seconds The length of the countdown
	 * @param plugin The plugin, to ge the server
	 * @param msg The message to be played every second / 5 seconds
	 * @param finalMsg The final message to be played!
	 */
	public CountdownTimer(int delay, int seconds, UHC plugin, String msg) {
		this.plugin = plugin;
		timeLeft = seconds;
		
		timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Countdowner(msg), delay*20, 20); 
	}

	/*
	 * Do the actual stuff for the countdown
	 */
	private class Countdowner extends BukkitRunnable {
		private String msg;
		private boolean first = true;
		
		public Countdowner(String msg) {
			this.msg = msg;
		}
		
		@Override
		public void run() {
			if(timeLeft == 10) {
				// Teleport
				UHC.getController().teleportTeams();
				UHC.getController().freezeTeams(10);		
			}
			
			if(first) {
				UHC.getController().setGameState(GameState.ACTIVE);
				plugin.getServer().broadcastMessage(ChatColor.RED + "Fixing bedrock...");
				UHC.getController().getUHCWorld().fixBedrock();
				String toPrint = msg.replace("#{COUNTDOWN}", ChatColor.AQUA + Integer.toString(timeLeft) + ChatColor.RESET);
				plugin.getServer().broadcastMessage(toPrint);	
				first = false;
			} 
			
			else if(timeLeft <= 10 && timeLeft > 0) {
				String toPrint = msg.replace("#{COUNTDOWN}", ChatColor.RED + Integer.toString(timeLeft) + ChatColor.RESET);
				plugin.getServer().broadcastMessage(toPrint);			
			} 
			
			else if(timeLeft % 5 == 0 && timeLeft > 0) {
				String toPrint = msg.replace("#{COUNTDOWN}", ChatColor.AQUA + Integer.toString(timeLeft) + ChatColor.RESET);
				plugin.getServer().broadcastMessage(toPrint);
			} 
			
			else if (timeLeft <= 0){		
				UHC.getController().clearCountdownTimer();
				
				// Update observer visibility
				UHC.getController().updateVis(false);
				UHC.getController().getUHCWorld().deleteSpawn();
				
				// Clear inventories
				Utils.clearInventories(plugin);
				
				// Heal
				Utils.healPlayers(plugin);			
				
				// Butcher
				Utils.killHostileMobs(UHC.getController().getUHCWorld().getWorld());
				
				// Stuff to start the game
				// Set world stuff to correct values
				UHC.getController().getUHCWorld().setUHCRules();
				UHC.getController().getUHCWorld().getWorld().setSpawnLocation(0, 120, 0);
				plugin.getServer().broadcastMessage(ChatColor.AQUA + "GAME IS STARTING " 
						+ ChatColor.DARK_RED + "NOW" + ChatColor.AQUA + "!");
				
				UHC.getController().setTimerID(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, 
						new GameplayTimer(plugin, UHC.getController().getClockSpeed()), 20*60, 20*60));

				Bukkit.getScheduler().cancelTask(timerID);
			}	
			
			timeLeft--;
		}
	}
	
	/**
	 * Cancel the current countdown!
	 * @return
	 */
	public boolean cancelCountdown() {
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The countdown has been " + ChatColor.DARK_RED + "cancelled" +
				ChatColor.AQUA + "!");
		if(Bukkit.getScheduler().isQueued(timerID))
			Bukkit.getScheduler().cancelTask(timerID);
		UHC.getController().clearCountdownTimer();
		return true;
	}
}