package me.cpanda.UHC.commands;

import me.cpanda.UHC.UHC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 * @author CacklingPanda
 * 
 * This class handles all commands that are related to the game or the 
 * state of the game
 */
public class GameCommands implements CommandExecutor {
	UHC plugin;
	
	/**
	 * Default constructor
	 * 
	 * @param plugin The plugin, in case needed
	 */
	public GameCommands(UHC plugin) {
		this.plugin = plugin;
	}

	/**
	 * Control all commands that are related to the acutal game or the game state
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
			// Check to see if the sender is a player or not
			Player player = null;
			boolean isPlayer = false;
			if(sender instanceof Player) {
				player = (Player) sender;
				isPlayer = true;
			}
			
			if(label.equalsIgnoreCase("start") && (!isPlayer || player.isOp())) {
				UHC.uhcWorld.fixBedrock();
				return UHC.controller.startGameCommand();
			} else if(label.equalsIgnoreCase("end") && (!isPlayer || player.isOp())) {
				return UHC.controller.endGameCommand();
			} else if(label.equalsIgnoreCase("join") && isPlayer) {
				if(args.length == 0)
					return UHC.controller.joinTeamCommand(player);
				else
					return UHC.controller.joinTeamCommand(args[0], player);
			} else if(label.equalsIgnoreCase("leave") && isPlayer) {
				return UHC.controller.leaveTeamCommand(player);
			} else if(label.equalsIgnoreCase("teams")) {
				return UHC.controller.printTeams(sender);
			} else if(label.equalsIgnoreCase("fixbedrock")) {
				plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Fixing bedrock... Prepare for lag!");
				return UHC.uhcWorld.fixBedrock();
			}
		return false;
	}
}
