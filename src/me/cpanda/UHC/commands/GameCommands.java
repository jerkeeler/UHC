package me.cpanda.UHC.commands;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;

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
			
			// Start the game
			if(label.equalsIgnoreCase("start") && (!isPlayer || player.isOp())) {
				UHC.getController().getUHCWorld().fixBedrock();
				return UHC.getController().startGame();
			} 
			
			// End the game
			else if(label.equalsIgnoreCase("end") && (!isPlayer || player.isOp())) {
				return UHC.getController().endGame();
			} 
			
			// Join a team
			else if(label.equalsIgnoreCase("join") && isPlayer) {
				if(args.length == 0)
					return UHC.getController().joinTeam(player);
				else
					return UHC.getController().joinTeam(args[0], player);
			} 
			
			// Leave your team
			else if(label.equalsIgnoreCase("leave") && isPlayer) {
				return UHC.getController().leaveTeam(player);
			} 
			
			// Print the teams
			else if(label.equalsIgnoreCase("teams")) {
				return UHC.getController().printTeams(sender);
			} 
			
			// Fix holes in the bedrock
			else if(label.equalsIgnoreCase("fixbedrock")) {
				plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Fixing bedrock... Prepare for lag!");
				return UHC.getController().getUHCWorld().fixBedrock();
			} 
			
			// Talk in global chat
			else if(label.equalsIgnoreCase("g") && isPlayer && args.length > 0) {
				return UHC.getController().talkGlobal(player, args);
			} 
			
			// Reset the game
			else if(label.equalsIgnoreCase("reset")) {
				boolean temp = UHC.getController().resetGame();
				if(!temp) sender.sendMessage(ChatColor.ITALIC + "The game has not started yet!");
				return true;
			} 
			
			// Cancel the countdown
			else if(label.equalsIgnoreCase("cancel")) {
				boolean temp = UHC.getController().cancelCountdown();
				if(!temp) sender.sendMessage(ChatColor.ITALIC +  "There is no countdown to cancel!");
				return true;
			} 
			
			// Randomize teams
			else if(label.equalsIgnoreCase("randomize")) {
				boolean temp = UHC.getController().randomizeTeams();
				if(!temp) sender.sendMessage(ChatColor.ITALIC + "You can only randomize teams before the game starts!");
				return true;
			}
			
			// Team force a player onto a team 
			else if(label.equalsIgnoreCase("teamf") && args.length > 1) { // TODO: Add more functionality.
				boolean temp = UHC.getController().teamForce(args[0], args[1]);
				if(!temp) sender.sendMessage(ChatColor.ITALIC + "That player does not exist or the team is full!");
				else sender.sendMessage(ChatColor.ITALIC + "Congrats! They either joined the team or are now an observer! I'm too lazy to flesh this out at the moment!");
				return true;
			}
			
			// Clear teams
			else if(label.equalsIgnoreCase("clearteams")) {
				boolean temp = UHC.getController().clearTeams();
				if(temp)
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "Teams have been " + ChatColor.DARK_RED +
							"cleared" + ChatColor.AQUA + "!");
				else 
					sender.sendMessage(ChatColor.ITALIC + "The game is either in progress or over! Cannot clear!");
				return true;
			}
			
			// Set game parameters
			else if(label.equalsIgnoreCase("set") && args.length >= 2) {
				// Make sure it's before the game has started
				if(!UHC.getController().getGameState().equals(GameState.STARTING)) {
					sender.sendMessage(ChatColor.ITALIC + "You must set stuff BEFORE the game starts!");
					return true;
				}
				
				String parameter = args[0];
				int number = 0;
				
				// Make sure they gave a valid number
				try {
					number = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.ITALIC + "Please provide a valid integer!");
					return true;
				}
				
				
				// Actually do stuff
				if(parameter.equalsIgnoreCase("teamsize")) {
					UHC.getController().setTeamSizes(number);
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "The team size has been set to " + 
							ChatColor.DARK_RED + number + ChatColor.AQUA + "!");
				} else if (parameter.equalsIgnoreCase("numteams")) {
					if(number > 10) {
						sender.sendMessage(ChatColor.ITALIC + "The max number of teams is 10");
						number = 10;
					}
					
					UHC.getController().setNumTeams(number);
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "The number of teams has been set to " + 
							ChatColor.DARK_RED + number + ChatColor.AQUA + "!");
				} else {
					sender.sendMessage(ChatColor.ITALIC + "Please provide a valid parameter to set!");
				}
				return true;
			}
		return false;
	}
}
