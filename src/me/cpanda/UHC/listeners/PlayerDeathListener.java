package me.cpanda.UHC.listeners;

import me.cpanda.UHC.UHC;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
	UHC plugin;
	
	public PlayerDeathListener(UHC plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player deadPlayer = event.getEntity();
		
		// Play sound!
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);
		}
		
		// Broadcast message
		// Remove from team
		// Ban player or add to spectators
		if(UHC.getController().canSpectate()) {
			// TODO: Add spectator
		} else if (!deadPlayer.isOp()){
			// TODO: Add player to dead list and ban
		}
		// Announce members left on team
	}

}
