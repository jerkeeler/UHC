package me.cpanda.UHC.listeners;

import me.cpanda.UHC.UHC;

import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author CacklingPanda
 * 
 * Keep track of mob deaths and increment the player's score
 */
public class MobDeathListener implements Listener {
	UHC plugin;
	
	public MobDeathListener(UHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER) 
				|| (event.getEntity() instanceof Animals && !(event.getEntity() instanceof Wolf)))
			return;
		
		if(event.getEntity().getKiller() instanceof Player) {
			UHC.getController().incrementMobKillCount(event.getEntity().getKiller());
		}
	}
}
