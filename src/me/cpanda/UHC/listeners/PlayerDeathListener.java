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

public class PlayerDeathListener implements Listener {
	UHC plugin;
	
	public PlayerDeathListener(UHC plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player deadPlayer = event.getEntity();
		Team playerTeam = null;
		if(UHC.getController().isOnTeam(deadPlayer))
			playerTeam = UHC.getController().getPlayerTeam(deadPlayer);
		
		if(event.getEntity().getKiller() instanceof Player) {
			event.setDeathMessage(deadPlayer.getDisplayName() + ChatColor.AQUA + " has been " 
				+ ChatColor.DARK_RED + " slain" + ChatColor.AQUA + " by " + event.getEntity().getKiller().getDisplayName() +
				ChatColor.AQUA + "!");
		}
		
		plugin.getServer().broadcastMessage(event.getDeathMessage());
		event.setDeathMessage("");
		
		// Broadcast message
		// Remove from team
		// Ban player or add to spectators
		if(UHC.getController().canSpectate()) {
			UHC.getController().spectate(deadPlayer);
		} else if (!deadPlayer.isOp()){
			UHC.getController().spectate(deadPlayer);
		} else {
			UHC.getController().leaveTeam(deadPlayer);
			event.getEntity().getPlayer().kickPlayer("You have " + ChatColor.DARK_RED + "died" + ChatColor.RESET + "!");
		}
		
		if(playerTeam != null) {
			// Play sound!
			for(Player p : plugin.getServer().getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.IRONGOLEM_DEATH, 1, 1);
			}
			// Announce members left on team
			if(playerTeam.getPlayers().size() == 0)
				plugin.getServer().broadcastMessage(playerTeam.getPrefix() + playerTeam.getName() + "" + ChatColor.AQUA + 
						" has been " + ChatColor.DARK_RED + "eliminated" + ChatColor.AQUA + "!");
			
			// Check teams
			if(UHC.getController().getTeamSizes() == 1) {
				
			} else {
				Team winner = null;
				int numTeamsLeft = 0;
				
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
				
				if(numTeamsLeft == 1) {
					plugin.getServer().broadcastMessage(winner.getPrefix() + winner.getName() + "" + ChatColor.AQUA + 
							" team has " + ChatColor.DARK_GREEN + "WON" + ChatColor.AQUA + "!");
					UHC.getController().endGame();
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerSpawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(UHC.getController().getUHCWorld().getWorld().getSpawnLocation());
	}
}
