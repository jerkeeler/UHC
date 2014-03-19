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
	
	/**
	 * Default constructor
	 * 
	 * @param plugin The plugin, in case needed
	 */
	public GameplayTimer(UHC plugin) {
		//this.PLUGIN = plugin;
		this.SERVER = plugin.getServer();
	}

	@Override
	public void run() {
		if(UHC.gameState.equals(GameState.ACTIVE)) {
			UHC.timePassed += 1;
			if(UHC.timePassed % UHC.clockSpeed == 0) {
				SERVER.broadcastMessage(ChatColor.DARK_GREEN + Integer.toString(UHC.timePassed/60) + ChatColor.DARK_PURPLE + " minutes have passed!");
				// TODO: Display scoreboard and play sound
				for(Player p : SERVER.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1, 1);
				}
			}
		}
	}
}
