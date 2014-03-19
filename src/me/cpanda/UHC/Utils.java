package me.cpanda.UHC;

import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;

/**
 * @author CacklingPanda
 * 
 * Random utility class with needed methods
 *
 */
public class Utils {
	
	/**
	 * Kills all hostile mobs in the specified world
	 * 
	 * @param world The world to kill mobs in
	 */
	public static int killHostileMobs(World world) {
		int hostileMobs = 0;
		
		// Cycle through all the entities in the current world
		for (Entity entity : world.getEntities()) {
			// If the entity is one of these, don't do anything, else remove it
			if (entity instanceof Player ||
					entity instanceof Cow ||
					entity instanceof Sheep ||
					entity instanceof Pig ||
					entity instanceof Squid ||
					entity instanceof Chicken ||
					entity instanceof MushroomCow ||
					entity instanceof Ocelot ||
					entity instanceof Villager ||
					entity instanceof Wolf) {
				continue;
			} else {
				hostileMobs++;
				entity.remove();
			}
		}
		
		return hostileMobs;
	}
	
	
	/**
	 * Heal all players and set saturation
	 * 
	 * @return boolean true if players, false otherwise
	 */
	public static boolean healPlayers(UHC plugin) {
		if(plugin.getServer().getOnlinePlayers() == null)
			return false;
		else {
			for(Player player : plugin.getServer().getOnlinePlayers()) {
				player.setHealth(player.getMaxHealth());
				player.setSaturation(20);
				player.setFoodLevel(20);
			}
		return true;
		}
	}
}
