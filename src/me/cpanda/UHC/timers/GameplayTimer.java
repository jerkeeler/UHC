package me.cpanda.UHC.timers;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author CacklingPanda
 *
 * This class controls the game clock and runs every second.
 */
public class GameplayTimer extends BukkitRunnable {
	//private final UHC PLUGIN;
	private final Server SERVER;
	private final int clockSpeed;
	
	/**
	 * Default constructor
	 * 
	 * @param plugin The plugin, in case needed
	 */
	public GameplayTimer(UHC plugin, int clockSpeed) {
		//this.PLUGIN = plugin;
		this.SERVER = plugin.getServer();
		this.clockSpeed = clockSpeed;
	}

	/**
	 * This is the logic that runs every minute
	 */
	@Override
	public void run() {
		if(UHC.getController().getGameState().equals(GameState.ACTIVE)) {
			UHC.getController().addTimePassed(1);
			if(UHC.getController().getTimePassed() % clockSpeed == 0) {
				SERVER.broadcastMessage(ChatColor.DARK_GREEN + Integer.toString(UHC.getController().getTimePassed()) + ChatColor.DARK_PURPLE + " minutes have passed!");
				// TODO: Display scoreboard
				for(Player p : SERVER.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1, 1);
				}
			}
		}
	}
}
