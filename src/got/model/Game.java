package got.model;

import got.model.Track;

/**
 * @author Souverain73 This class handles all Game data like tracks, provision,
 *         etc.
 */
public class Game {
	private static Game _instance = null;

	private Game() {
	}

	public static void init(int playersCount){
		_instance = new Game();
		_instance.__init(playersCount);
	}
	
	public static Game instance() {
		if (_instance == null) {
			throw new IllegalStateException("You must init Game for current players before you can use it. use Game.init(playersCount)");
		}
		return _instance;
	}
	
	private Fraction[][] throneDefault = new Fraction[][] { { Fraction.STARK }, 
			{ Fraction.STARK, Fraction.LANISTER },
			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATEON },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TIREL },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TIREL,
					Fraction.MARTEL } };

	private Fraction[][] swordDefault = new Fraction[][] { { Fraction.STARK }, 
			{ Fraction.STARK, Fraction.LANISTER },
			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATEON },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TIREL },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TIREL,
					Fraction.MARTEL } };

	private Fraction[][] crowDefault = new Fraction[][] { { Fraction.STARK }, 
			{ Fraction.STARK, Fraction.LANISTER },
			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATEON },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TIREL },
			{ Fraction.BARATEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TIREL,
					Fraction.MARTEL } };

	Track throneTrack;
	Track swordTrack;
	Track crowTrack;
	// resourcesTrack;
	// victoryTrack;

	public void __init(int playersCount) {
		throneTrack = new Track("Throne", throneDefault[playersCount-1]);
		swordTrack = new Track("Sword", swordDefault[playersCount-1]);
		crowTrack = new Track("Crow", crowDefault[playersCount-1]);
	}

	public Track getThroneTrack() {
		return throneTrack;
	}

	public Track getSwordTrack() {
		return swordTrack;
	}

	public Track getCrowTrack() {
		return crowTrack;
	}

	
}
