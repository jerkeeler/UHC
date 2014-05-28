package me.cpanda.UHC.listeners;

import java.util.*;

import me.cpanda.UHC.UHC;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Team;

/**
 * @author CacklingPanda
 *
 * This class listens for when a player dies and keeps track of the objective, last one alive
 */
public class PlayerDeathListener implements Listener {
	UHC plugin;
	
	/**
	 * Constructor
	 * 
	 * @param plugin In case needed
	 */
	public PlayerDeathListener(UHC plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Fires whenever a player dies
	 * 
	 * @param event The death event
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player deadPlayer = event.getEntity();
		Team playerTeam = null;
		
		// Is this player an observer or a team?
		if(UHC.getController().isOnTeam(deadPlayer))
			playerTeam = UHC.getController().getPlayerTeam(deadPlayer);
		
		// If they are slain by a player set the new death message
		if(event.getEntity().getKiller() instanceof Player) {
			event.setDeathMessage(deadPlayer.getDisplayName() + ChatColor.AQUA + " has been " 
				+ ChatColor.DARK_RED + " slain" + ChatColor.AQUA + " by " + event.getEntity().getKiller().getDisplayName() +
				ChatColor.AQUA + "!");
		}
		
		// Broadcast death message and set real death message to nothing
		plugin.getServer().broadcastMessage(event.getDeathMessage());
		event.setDeathMessage("");
		
		// Kick player or add to spectators
		if(UHC.getController().canSpectate()) {
			UHC.getController().spectate(deadPlayer);
		} else if (deadPlayer.isOp()){
			UHC.getController().spectate(deadPlayer);
		} else {
			UHC.getController().leaveTeam(deadPlayer);
			event.getEntity().getPlayer().kickPlayer("You have " + ChatColor.DARK_RED + "died" + ChatColor.RESET + "!");
		}
		
		// If the player isn't an observer
		if(playerTeam != null) {
			// Play sound!
			for(Player p : plugin.getServer().getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.IRONGOLEM_DEATH, 1, 1);
			}
			
			// Announce if team is eliminated
			if(playerTeam.getPlayers().size() == 0)
				plugin.getServer().broadcastMessage(playerTeam.getPrefix() + playerTeam.getName() + "" + ChatColor.AQUA + 
						" has been " + ChatColor.DARK_RED + "eliminated" + ChatColor.AQUA + "!");
			
			// Check teams
			if(UHC.getController().getTeams().size() == 2) {
				// Do stuff for single person teams
			} else {
				// Check to see if there is a winner
				Team winner = null;
				int numTeamsLeft = 0;
				
				// Go through all teams and see how many are alive
				Set<Team> teams = UHC.getController().getTeams();
				Iterator<Team> teamIt = teams.iterator();
				while(teamIt.hasNext()) {
					Team team = teamIt.next();
					if(team.getName().equals("Observer"))
						continue;
					if(team.getPlayers().size() > 0) {
						winner = team;
						numTeamsLeft++;
					}
				}
				
				// If only 1 team is alive, broadcast message, play sound, change gamemode and end game
				if(numTeamsLeft == 1) {
					plugin.getServer().broadcastMessage(winner.getPrefix() + winner.getName() + "" + ChatColor.AQUA + 
							" team has " + ChatColor.DARK_GREEN + "WON" + ChatColor.AQUA + "!");
					for(Player p : plugin.getServer().getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
					}
					UHC.getController().endGame();
				}
			}
		}
	}
	
	/**
	 * Calls whenever a player respawns
	 * 
	 * @param event The event
	 */ 
	@EventHandler
	public void onPlayerSpawn(PlayerRespawnEvent event) {
	}
}
