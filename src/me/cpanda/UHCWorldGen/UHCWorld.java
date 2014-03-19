package me.cpanda.UHCWorldGen;

import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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
	private int radius;
	private String worldName;
	private World regWorld;
	
	/**
	 * The default constructor for the UHCWorld
	 * 
	 * @param radius The playable radius for the world
	 * @param regWorld The world file that is being played in
	 */
	public UHCWorld(int radius, String worldName, World regWorld) {
		this.radius = radius;
		this.worldName = worldName;
		this.regWorld = regWorld;
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
	}
	
	/**
	 * Set the world to in-game UHC rules
	 */
	public void setUHCRules() {
		regWorld.setDifficulty(Difficulty.HARD);
		regWorld.setPVP(true);
		regWorld.setGameRuleValue("doDaylightCycle", "true"); // TODO: Check if eternal day or night
		regWorld.setGameRuleValue("naturalRegeneration", "false");
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
	 * Static method to create a new UHCWorld if one doesn't exist
	 * 
	 * @param worldName The name of the world
	 * @param radius The playable radius of the world
	 * @param server The server object to create the world
	 * @return UHCWorld The new UHCWorld object
	 */
	public static UHCWorld createWorld(String worldName, int radius, Server server, boolean generateBedrock) {
		UHCWorld newWorld = new UHCWorld(radius, worldName, server.createWorld(new WorldCreator(worldName)));
		
		// Set the parameters to meet the UHC requirements for Pre-game
		newWorld.setPreUHCRules();
		
		//server.getLogger().info("[UHC] LOADING CHUNKS PREPARE FOR LAG");
		//newWorld.loadChunks();
		
		if(generateBedrock) {
			server.getLogger().info("[UHC] GENERATING BEDROCK PREPARE FOR LAG");
			newWorld.generateBedrock();
		}
		
		return newWorld;
	}
	
	/**
	 * Static method to load an already existing UHCWorld
	 * 
	 * @param worldName The name of the world.
	 * @param radius The playable radius of the world
	 * @param server The server object to load the world
	 * @return UHCWorld The loaded UCHWorld object
	 */
	public static UHCWorld loadWorld(String worldName, int radius, Server server, boolean addBedrock) {
		UHCWorld loadedWorld = new UHCWorld(radius, worldName, server.getWorld(worldName)); // temp, TODO: remove = null when addBedrock is finished
		
		server.getLogger().info("[UHC] LOADING CHUNKS PREPARE FOR LAG");
		loadedWorld.loadChunks();
		
		if(addBedrock) {
			server.getLogger().info("[UHC] GENERATING BEDROCK PREPARE FOR LAG");
			loadedWorld.generateBedrock();
		} else {
			loadedWorld = new UHCWorld(radius, worldName, server.getWorld(worldName));
		}
		
		return loadedWorld;
	}

	/**
	 * Load all of the chunks within the predefined radius to reduce lag upon playing
	 * the game.
	 */
	private void loadChunks() {
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
				regWorld.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}		
		
		// NE to SE side
		z = neCornerZ;
		x = neCornerX;
		
		for(z = neCornerZ; z <= seCornerZ; z++) {
			for(int y = 0; y <= 256; y++) {
				regWorld.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}
		
		// NW to SW side
		z = nwCornerZ;
		x = nwCornerX;
		
		for(z = nwCornerZ; z <= swCornerZ; z++) {
			for(int y = 0; y <= 256; y++) {
				regWorld.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}
		
		// SW to SE side
		z = swCornerZ;
		x = swCornerX;
		
		for(x = swCornerX; x <= seCornerX; x++) {
			for(int y = 0; y <= 256; y++) {
				regWorld.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}
		}
	}
	
	public boolean fixBedrock() {
		generateBedrock();
		return true;
	}
	
}