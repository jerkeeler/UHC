package me.cpanda.UHC.enums;

/**
 * 
 * @author CacklingPanda
 * @modified March 6, 2014
 * 
 * This class is an enums representing the current state of the game. Whether
 * the game is in the pre-game, game, or post-game;
 *
 */
public enum GameState {
	STARTING, ACTIVE, ENDING;
	
	/**
	 * Get the game state based on an integer
	 * 
	 * @param value The integer being turned into a GameState
	 * @return GameState The current GameState
	 */
	public static GameState intToGameState(int value) {
		if(value < 0) return STARTING;
	    else if(value > 0) return ENDING;
		else return ACTIVE;
	}
	
	/**
	 * Return an integer representation of the given GameState
	 * 
	 * @param state The GameState to be converted to an integer
	 * @return int The representative integer
	 */
	public static int gameStateToInt(GameState state) {
		if(state.equals(STARTING)) return -1;
		else if(state.equals(ENDING)) return 1;
		else return 0;
	}
}
