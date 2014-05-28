package me.cpanda.UHC.timers;

import me.cpanda.UHC.UHC;

import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardTimer extends BukkitRunnable {
	private boolean toggle = false;
	
	public void run() {
		if(toggle) 
			UHC.getController().showMobKills();
		else
			UHC.getController().showPlayerKills();
	}
}
