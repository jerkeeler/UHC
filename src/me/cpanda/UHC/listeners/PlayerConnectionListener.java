package me.cpanda.UHC.listeners;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author CacklingPanda
 *
 * This class handles players joining the server. If they are not in
 * the UHC world make sure they are!
 */
public class PlayerConnectionListener implements Listener {
	UHC plugin;
	
	/**
	 * Default constructor
	 * 
	 * @param plugin The main plugin class in case needed.
	 */
	public PlayerConnectionListener(UHC plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Make sure they are in the UHC world and if they aren't teleport them and then set
	 * their spawn location.
	 * 
	 * @param event The event that fires when a player joins
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player joiningPlayer = event.getPlayer();
		// TODO: Add player to the spectators team if game is in progress
		// TODO: Check to see if dead
		
		// Check to see if the player is in the correct world, if not, teleport them
		joiningPlayer.getLocation().setWorld(UHC.getController().getUHCWorld().getWorld());
		joiningPlayer.teleport(UHC.getController().getUHCWorld().getWorld().getSpawnLocation());
		joiningPlayer.setBedSpawnLocation(UHC.getController().getUHCWorld().getWorld().getSpawnLocation());
		
		// Set gamemode to adventure unless the player is OP or game is active
		if(!joiningPlayer.isOp() && !UHC.getController().getGameState().equals(GameState.ACTIVE)) {
			joiningPlayer.setGameMode(GameMode.ADVENTURE);
		}
		
		UHC.getController().setScoreboard(joiningPlayer);
	}
	

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
	}
}
