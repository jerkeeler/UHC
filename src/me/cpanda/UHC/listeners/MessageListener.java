package me.cpanda.UHC.listeners;

import me.cpanda.UHC.UHC;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

/**
 * @author CacklingPanda
 *
 */
public class MessageListener implements Listener {
	UHC plugin;
	
	public MessageListener(UHC plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * What happens when a player chats.
	 * 
	 * @param event The event
	 */
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Team team = UHC.getController().getPlayerTeam(event.getPlayer());
		if(team != null) {
			String message = team.getPrefix() + "[team] " + ChatColor.RESET + event.getPlayer().getDisplayName() + ChatColor.RESET + ": " + event.getMessage();	
			
			// Send message to teammate only
			for(OfflinePlayer p : team.getPlayers()) {
				if(p instanceof Player)
					((Player) p).sendMessage(message);
			}
			plugin.getLogger().info(message);
			
			event.setCancelled(true);
		}
	}
}
