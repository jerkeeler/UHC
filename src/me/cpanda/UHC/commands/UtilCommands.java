package me.cpanda.UHC.commands;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author CacklingPanda
 * 
 * Class to handle all utility commands that might be useful but aren't 
 * needed.
 *
 */
public class UtilCommands implements CommandExecutor {
	UHC plugin;
	
	/**
	 * Default constructor
	 * 
	 * @param plugin The plugin, in case needed
	 */
	public UtilCommands(UHC plugin) {
		this.plugin = plugin;
	}

	/**
	 * Control all commands that aren't related to actual gameplay or the game state
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		
		if(label.equalsIgnoreCase("heal") && (player.isOp() || player == null)) {
			Utils.healPlayers(plugin);
		} else if(label.equalsIgnoreCase("freeze") && args.length == 1 && (player.isOp() || player == null)) {
			UHC.getController().freezeTeams(Integer.parseInt(args[0]));
			return true;
		}
		
		return false;
	}
}
