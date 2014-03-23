package me.cpanda.UHC.listeners;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;

import org.bukkit.ChatColor;
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
		
		/* Different scenarios
		 Game is Starting
			 - Auto put to observers
		 Game is Active
		 	Spectate = true
		 		On team
		 			Don't put in observers
		 		Not on team
		 			Put in observers
		 	Spectate = false
		 		On team
		 			Don't put in observers
		 		Is OP?
		 			Put in observers
	 			Not on team
		 			kick
		 Game is Over
			- Auto put to observers
		 */
		// If game is starting put everyone in observers
		if(UHC.getController().getGameState().equals(GameState.STARTING)
				|| UHC.getController().getGameState().equals(GameState.ENDING)) {
			UHC.getController().spectate(joiningPlayer);
		} 
		
		// If game is active
		else {
			// If spectating is allowed
			if(UHC.getController().canSpectate()) {
				if(!UHC.getController().isOnTeam(joiningPlayer))
					UHC.getController().spectate(joiningPlayer);
			}
			
			// If spectating isn't allowed
			else {
				if(UHC.getController().isOnTeam(joiningPlayer)) {
					// Do nothing
				}
				else if(joiningPlayer.isOp())
					UHC.getController().spectate(joiningPlayer);
				else {
					joiningPlayer.kickPlayer(ChatColor.RED + "The game is in progress!!!");
				}					
			}
		}
		
		if(UHC.getController().isOnTeam(joiningPlayer))
			joiningPlayer.setDisplayName(UHC.getController().getPlayerTeam(joiningPlayer).getPrefix()
					+ joiningPlayer.getName() + ChatColor.RESET);

		
		UHC.getController().setScoreboard(joiningPlayer);
	}
	
	/**
	 * Event that is called when a player leaves
	 * @param event
	 */
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
	}
}
