package me.cpanda.UHC.theGame;

import java.util.*;

import me.cpanda.UHC.UHC;
import me.cpanda.UHC.enums.GameState;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

/**
 * @author CacklingPanda
 *
 * This class controls all teams and the scoreboard
 */
public class TeamController {
	private String defaultName = "DEFAULT";
	private ChatColor[] teamColors = { ChatColor.RED, ChatColor.BLUE, ChatColor.AQUA, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE,
			ChatColor.GOLD, ChatColor.BLACK, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.LIGHT_PURPLE };
	private String doesNotExistMessage = "That team " + ChatColor.DARK_RED + "does not exist" + ChatColor.RESET + "!";
	private String teamFullMessage = "That team is already " + ChatColor.DARK_RED + "full" + ChatColor.RESET + "!";
	private String alreadyOnTeamMessage = "You are already " + ChatColor.DARK_RED + "on " + ChatColor.RESET + "a team!";
	private String notOnTeamMessage = "You are " + ChatColor.DARK_RED + "not " + ChatColor.RESET + "on a team!";
	
	private int teamSizes, totalNumTeams;
	private UHC plugin;
	private ScoreboardManager scbManager;
	private Scoreboard mainScoreboard;
	private HashMap<String, Scoreboard> scoreboards = new HashMap<String, Scoreboard>();
	
	/**
	 * Default constructor
	 * 
	 * @param plugin The UHC plugin in case needed
	 * @param teamSizes The number of players per team
	 * @param numTeams The total amount of teams
	 */
	public TeamController(UHC plugin, int teamSizes, int numTeams) {
		this.plugin = plugin;
		scbManager = plugin.getServer().getScoreboardManager();
		mainScoreboard = scbManager.getNewScoreboard();
		scoreboards.put("mainScoreboard", mainScoreboard);
		
		this.teamSizes = teamSizes;
		this.totalNumTeams = numTeams;
		
		addObjectives();
		createTeams();
	}
	
	/**
	 * Add the main objectives to the mainScoreboard!
	 */
	private void addObjectives() {
		mainScoreboard.registerNewObjective("playerHealth", "health");
		mainScoreboard.registerNewObjective("playerKills", "playerKills");
		//mainScoreboard.registerNewObjective("mobKills", "mobKills");
		mainScoreboard.getObjective("playerHealth").setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}
	
	/**
	 * Create the teams!
	 */
	private void createTeams() {
		
		if(teamSizes == 1) {
			mainScoreboard.registerNewTeam(defaultName);
		} else {
			for(int i = 0; i < totalNumTeams; i++) {
				String teamName = teamColors[i].name();
				if(mainScoreboard.getTeam(teamName) ==  null) {
					mainScoreboard.registerNewTeam(teamName);
					mainScoreboard.getTeam(teamName).setPrefix("" + teamColors[i]);
				}
			}
		}
		
		Iterator<OfflinePlayer> playerIt = mainScoreboard.getPlayers().iterator();
		while(playerIt.hasNext()) {
			mainScoreboard.resetScores(playerIt.next());
		}
	}
	
	/**
	 * Add the player to a random team
	 * 
	 * @param player The player to join the team
	 * @return boolean True if joined a team
	 */
	public boolean joinRandomTeam(Player player) {
		if(teamSizes == 1)
			return joinTeam("Default", player);
		
		Random rnd = new Random();
		int choice = rnd.nextInt(totalNumTeams);
		Iterator<Team> teamIt = mainScoreboard.getTeams().iterator();
		String teamPref = "";
		for(int i = 0; i <= choice; i++) {
			teamPref = teamIt.next().getName();
		}
		return joinTeam(teamPref, player);
	}
	
	/**
	 * Join the specified team
	 * 
	 * @param teamPref The team the player wishes to join
	 * @param player The player joining a team
	 * @return boolean true if they joined a team
	 */
	public boolean joinTeam(String teamPref, Player player) {
		if(UHC.gameState.equals(GameState.ACTIVE)) {
			player.sendMessage("The game is " + ChatColor.DARK_RED + "in progress" + ChatColor.RESET + "!");
			return true;
		}
		
		if(teamSizes == 1) {
			mainScoreboard.getTeam(defaultName).addPlayer(player);
			player.sendMessage("You have " + ChatColor.DARK_GREEN + "joined " + ChatColor.RESET + "the game!");
			return true;
		}
		
		teamPref = teamPref.toUpperCase();
		if(mainScoreboard.getTeam(teamPref) == null)
			player.sendMessage(doesNotExistMessage);
		else if(mainScoreboard.getTeam(teamPref).getPlayers().size() >= teamSizes)
			player.sendMessage(teamFullMessage);
		else if(mainScoreboard.getPlayerTeam(player) != null)
			player.sendMessage(alreadyOnTeamMessage);
		else {
			mainScoreboard.getTeam(teamPref).addPlayer(player);
			player.sendMessage("You have " + ChatColor.DARK_GREEN + "joined " + ChatColor.RESET + "the " + 
					ChatColor.valueOf(mainScoreboard.getTeam(teamPref).getName()) + mainScoreboard.getTeam(teamPref).getName() +
					ChatColor.RESET + " team!");
		}
		return true;
	}
	
	/**
	 * Remove the player from the team.
	 * 
	 * @param player The player to remove.
	 * @return true
	 */
	public boolean leaveTeam(Player player) {
		if(UHC.gameState.equals(GameState.ACTIVE)) {
			player.sendMessage("The game is " + ChatColor.DARK_RED + "in progress" + ChatColor.RESET + "!");
			return true;
		}
		
		if(teamSizes == 1) {
			mainScoreboard.getTeam(defaultName).removePlayer(player);
			player.sendMessage("You have " + ChatColor.DARK_RED + "left " + ChatColor.RESET + "the game!");
			return true;
		}
		
		if(!onTeam(player))
			player.sendMessage(notOnTeamMessage);
		else {
			for(Team team : mainScoreboard.getTeams()) {
				if(team.hasPlayer(player)) {
					player.sendMessage("You have been " + ChatColor.DARK_RED + "left " + ChatColor.RESET + "the " + 
							ChatColor.valueOf(team.getName()) + team.getName() + ChatColor.RESET + " team!");
					team.removePlayer(player);
					break;
				}
			}
		}
		return true;
	}
	
	/**
	 * See if a player is on a team or not
	 * 
	 * @param player The player to see if they are on a team
	 * @return boolean True if on team, false if not
	 */
	private boolean onTeam(Player player) {
		Iterator<Team> teamIt = mainScoreboard.getTeams().iterator();
		while(teamIt.hasNext()) {
			Team team = teamIt.next();
			if(team.hasPlayer(player))
				return true;
		}
		
		return false;
	}
	
	/**
	 * The player to set the scoreboard for
	 * 
	 * @param player The player
	 */
	public void setScoreboard(Player player) {
		player.setScoreboard(mainScoreboard);
	}
	
	/**
	 * Print the teams that are there
	 * 
	 * @param sender The sender of the command
	 * @return boolean true
	 */
	public boolean printTeams(CommandSender sender) {
		if(teamSizes == 1) {
			sender.sendMessage("Players: " + mainScoreboard.getTeam(defaultName).getSize() + ChatColor.BLACK + "/" + ChatColor.RESET
					+ totalNumTeams);
			return true;
		}
		
		Iterator<Team> teamIt = mainScoreboard.getTeams().iterator();
		while(teamIt.hasNext()) {
			Team team = teamIt.next();
			sender.sendMessage(ChatColor.valueOf(team.getDisplayName()) + team.getDisplayName() + " " + ChatColor.RESET + team.getSize() 
					+ ChatColor.BLACK + "/" + ChatColor.RESET + teamSizes);
		}
		return true;
	}
	
	
	/**
	 * Teleport all teams out to a random location
	 */
	public void teleportTeams() {
		World world = UHC.uhcWorld.getWorld();
		int radius = UHC.uhcWorld.getRadius();
		int minDistance = radius/5;
		boolean respectTeams = true;
		if(teamSizes == 1) {
			respectTeams = false;
			minDistance = radius/10;
		}
		
		String command = "spreadplayers " + world.getSpawnLocation().getBlockX() + " " + world.getSpawnLocation().getBlockZ() + " " + minDistance + " "
				+ (radius-1) + " " + respectTeams;
		
		// Add all players on teams to the command
		for(Team team : mainScoreboard.getTeams()) {
			for(OfflinePlayer player : team.getPlayers()) {
				command += " " + player.getName();
				if(player instanceof Player) 
					((Player) player).setGameMode(GameMode.SURVIVAL);
			}
		}
		
		System.out.println(command);
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
		
		plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Players have been teleported!");
	}
	
	/**
	 * Freeze all players for the specified amount of time
	 * 
	 * @param seconds The length
	 */
	public void freeze(int seconds) {
		Collection<PotionEffect> freezeEffects = new ArrayList<PotionEffect>();
		
		// Set freeze effects
		freezeEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, 20*seconds, 100));
		freezeEffects.add(new PotionEffect(PotionEffectType.SLOW, 20*seconds, 100));
		freezeEffects.add(new PotionEffect(PotionEffectType.JUMP, 20*seconds, 126));
		freezeEffects.add(new PotionEffect(PotionEffectType.WATER_BREATHING, 20*seconds, 100));
		
		for(Team team : mainScoreboard.getTeams()) {
			for(OfflinePlayer player : team.getPlayers()) {
				if(player instanceof Player) {
					((Player) player).addPotionEffects(freezeEffects);
				}
			}
		}
	}
	
	/**
	 * Get the number of players per team
	 * 
	 * @return int The number of player per team
	 */
	public int getTeamSizes() {
		return teamSizes;
	}

}
