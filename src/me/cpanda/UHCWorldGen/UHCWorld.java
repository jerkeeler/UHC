package me.cpanda.UHCWorldGen;

import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

/**
 * @author CacklingPanda
 * 
 * This method keeps track of the UHC world defined in the 
 * config.yml file and handles any actions to be performed
 * on it.
 *
 */
public class UHCWorld {
	private int radius, eternalDay;
	private String worldName;
	private World regWorld, netherWorld, endWorld;
	
	/**
	 * The default constructor for the UHCWorld
	 * 
	 * @param radius The playable radius for the world
	 * @param regWorld The world file that is being played in
	 */
	public UHCWorld(int radius, String worldName, int eternalDay,  World regWorld, World netherWorld, World endWorld) {
		this.radius = radius;
		this.worldName = worldName;
		this.eternalDay = eternalDay;
		this.regWorld = regWorld;
		this.netherWorld = netherWorld;
		this.endWorld = endWorld;
	}

	/**
	 * Load all of the chunks within the predefined radius to reduce lag upon playing
	 * the game.
	 */
	private void loadChunks() {
		// Load regular world
		int chunksToLoad = (int) Math.ceil(radius/16.0);
		int spawnZ = regWorld.getSpawnLocation().getChunk().getZ();
		int spawnX = regWorld.getSpawnLocation().getChunk().getX();
		int startZ = spawnZ - chunksToLoad;
		int startX = spawnX - chunksToLoad;
		int endZ = spawnZ + chunksToLoad; 
		int endX = spawnX + chunksToLoad;
		int numChunksGen = 0;
		
		for(int z = startZ; z <= endZ; z++) {
			for(int x = startX; x <= endX; x++) {
				numChunksGen++;
				regWorld.loadChunk(x, z, true);
				regWorld.getChunkAt(x, z).unload();
			}
		}
		System.out.println("Number of chunks generated " + numChunksGen);
	}
	
	/**
	 * Generate the bedrock wall at the predefined radius
	 */
	private void generateBedrock() {
		int spawnZ = regWorld.getSpawnLocation().getBlockZ();
		int spawnX = regWorld.getSpawnLocation().getBlockX();
		makeBedrock(radius, spawnX, spawnZ, regWorld);
		
		// GENERATE NETHER BEDROCK WALL
		int netherSpawnZ = spawnZ/8;
		int netherSpawnX = spawnX/8;
		int netherRadius = (int) Math.ceil(radius/8);
		makeBedrock(netherRadius, netherSpawnX, netherSpawnZ, netherWorld);
		
		// GENERATE END BEDROCK WALLS
		int endSpawnZ = endWorld.getSpawnLocation().getBlockZ();
		int endSpawnX = endWorld.getSpawnLocation().getBlockX();
		makeBedrock(radius, endSpawnX, endSpawnZ, endWorld);
	}
	
	/**
	 * Make bedrock walls given the radius, spawn cords, and the world in which to generate
	 * 
	 * @param radius The radius from the center of spawn
	 * @param spawnX The spawn X
	 * @param spawnZ The spawn Y
	 * @param world The world in which to create the walls
	 */
	private void makeBedrock(int radius, int spawnX, int spawnZ, World world) {
		
		int swCornerZ = spawnZ + radius;
		int swCornerX = spawnX - radius;
		
		int nwCornerZ = spawnZ - radius;
		int nwCornerX = spawnX - radius;
		
		int neCornerZ = spawnZ - radius;
		int neCornerX = spawnX + radius;
		
		int seCornerZ = spawnZ + radius;
		int seCornerX = spawnX + radius;
		
		int x = 0;
		int z = 0;
		
		// NW to NE side
		z = nwCornerZ;
		x = nwCornerX;
		
		for(x = nwCornerX; x <= neCornerX; x++) {
			for(int y = 0; y <= 256; y++) {
				world.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}		
		
		// NE to SE side
		z = neCornerZ;
		x = neCornerX;
		
		for(z = neCornerZ; z <= seCornerZ; z++) {
			for(int y = 0; y <= 256; y++) {
				world.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}
		
		// NW to SW side
		z = nwCornerZ;
		x = nwCornerX;
		
		for(z = nwCornerZ; z <= swCornerZ; z++) {
			for(int y = 0; y <= 256; y++) {
				world.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}
		
		// SW to SE side
		z = swCornerZ;
		x = swCornerX;
		
		for(x = swCornerX; x <= seCornerX; x++) {
			for(int y = 0; y <= 256; y++) {
				world.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}
	}
	
	/**
	 * Set the UHC rules for the world for pre-game
	 */
	public void setPreUHCRules() {
		regWorld.setDifficulty(Difficulty.PEACEFUL);
		regWorld.setPVP(false);
		regWorld.setTime(0);
		regWorld.setGameRuleValue("doDaylightCycle", "false");
		regWorld.setGameRuleValue("naturalRegeneration", "true");
		
		netherWorld.setDifficulty(Difficulty.PEACEFUL);
		netherWorld.setPVP(false);
		netherWorld.setTime(0);
		netherWorld.setGameRuleValue("doDaylightCycle", "false");
		netherWorld.setGameRuleValue("naturalRegeneration", "true");
		
		endWorld.setDifficulty(Difficulty.PEACEFUL);
		endWorld.setPVP(false);
		endWorld.setTime(0);
		endWorld.setGameRuleValue("doDaylightCycle", "false");
		endWorld.setGameRuleValue("naturalRegeneration", "true");
	}
	
	/**
	 * Set the world to in-game UHC rules
	 */
	public void setUHCRules() {
		regWorld.setDifficulty(Difficulty.HARD);
		regWorld.setPVP(true);
		regWorld.setGameRuleValue("naturalRegeneration", "false");
		
		netherWorld.setDifficulty(Difficulty.HARD);
		netherWorld.setPVP(true);
		netherWorld.setGameRuleValue("naturalRegeneration", "false");
		
		endWorld.setDifficulty(Difficulty.HARD);
		endWorld.setPVP(true);
		endWorld.setGameRuleValue("naturalRegeneration", "false");
		
		// Eternal day or night?
		if(eternalDay == 0)
			regWorld.setGameRuleValue("doDaylightCycle", "true");
		else if(eternalDay == 1)
			regWorld.setTime(0);
		else
			regWorld.setTime(1000*10);
	}
	
	/**
	 * Getter method to obtain the playable radius for this world
	 * 
	 * @return int the radius
	 */
	public int getRadius() {
		return radius;
	}
	
	/**
	 * Getter method to obtain the name of the underlying world
	 * 
	 * @return String the name of the world
	 */
	public String getWorldName() {
		return worldName;
	}

	/**
	 * Getter method to obtain the actual world
	 * 
	 * @return World The world object corresponding with the UHC world
	 */
	public World getWorld() {
		return this.regWorld;
	}
	
	/** 
	 * Method to call to run the generateBedrock() method on command
	 * 
	 * @return true
	 */
	public boolean fixBedrock() {
		generateBedrock();
		return true;
	}
	
	/**
	 * Static method to load an already existing UHCWorld
	 * 
	 * @param worldName The name of the world.
	 * @param radius The playable radius of the world
	 * @param server The server object to load the world
	 * @return UHCWorld The loaded UCHWorld object
	 */
	public static UHCWorld loadWorld(int radius, boolean generateBedrock, boolean pregenChunks, int eternalDay, Server server) {
		List<World> listOfWorlds = server.getWorlds();
		// Check to see if world is created or not
		World regWorld = listOfWorlds.get(0);;
		String worldName = regWorld.getName();
		
		if(server.getWorld(worldName) != null)
			regWorld = server.getWorld(worldName);
		else 
			regWorld = server.createWorld(new WorldCreator(worldName));
		
		regWorld.setSpawnLocation(0, 100, 0);
		World netherWorld = generateNetherWorld(worldName, server);
		World endWorld = generateEndWorld(worldName, server);
		
		UHCWorld loadedWorld = new UHCWorld(radius, worldName, eternalDay, regWorld, netherWorld, endWorld);
		
		// Do you want to pregenChunks?
		if(pregenChunks) {
			server.getLogger().info("[UHC] LOADING CHUNKS PREPARE FOR LAG");
			loadedWorld.loadChunks();
			server.getLogger().info("[UHC] CHUNKS ARE GENERATED");
		}
		
		// Do you want to add bedrock?
		if(generateBedrock) {
			server.getLogger().info("[UHC] GENERATING BEDROCK PREPARE FOR LAG");
			loadedWorld.generateBedrock();
			server.getLogger().info("[UHC] BEDROCK HAS BEEN GENERATED");
		}
		
		return loadedWorld;
	}
	
	/**
	 * Create the nether world
	 * 
	 * @param worldName The name of the original world
	 * @param radius The playable radius
	 * @param generateBedrock Do you want to generate bedrock?
	 * @param pregenChunks Do you want to pregen chunks?
	 * @param server The server
	 * @return World the nether world to be returned
	 */
	public static World generateNetherWorld(String worldName, Server server) {
		World netherWorld = null;
		
		// Create the nether world
		if(server.getWorld(worldName + "_nehter") != null) {
			netherWorld = server.getWorld(worldName + "_nether");
		} else {
			WorldCreator netherCreator = new WorldCreator(worldName + "_nether");
			netherCreator.environment(Environment.NETHER);
			netherWorld = server.createWorld(netherCreator);
		}
		
		return netherWorld;
	}
	
	/**
	 * Create the nether world
	 * 
	 * @param worldName The name of the original world
	 * @param server The server
	 * @return World the nether world to be returned
	 */
	public static World generateEndWorld(String worldName, Server server) {
		World endWorld = null;
		
		// Create the nether world
		if(server.getWorld(worldName + "_end") != null) {
			endWorld = server.getWorld(worldName + "_the_end");
		} else {
			WorldCreator endCreator = new WorldCreator(worldName + "_the_end");
			endCreator.environment(Environment.THE_END);
			endWorld = server.createWorld(endCreator);
		}
		
		return endWorld;
	}
}