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
	
//	private Fraction[][] throneDefault = new Fraction[][] { { Fraction.STARK },
//			{ Fraction.STARK, Fraction.LANISTER },
//			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATHEON },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL,
//					Fraction.MARTEL } };
//
//	private Fraction[][] swordDefault = new Fraction[][] { { Fraction.STARK },
//			{ Fraction.STARK, Fraction.LANISTER },
//			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATHEON },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL,
//					Fraction.MARTEL } };
//
//	private Fraction[][] crowDefault = new Fraction[][] { { Fraction.STARK },
//			{ Fraction.STARK, Fraction.LANISTER },
//			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATHEON },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL },
//			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL,
//					Fraction.MARTEL } };

//Baratheon vs Tyrell
	private Fraction[][] throneDefault = new Fraction[][] { { Fraction.BARATHEON },
			{ Fraction.BARATHEON, Fraction.TYRELL },
			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATHEON },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL,
					Fraction.MARTEL } };

	private Fraction[][] swordDefault = new Fraction[][] { { Fraction.BARATHEON },
			{ Fraction.BARATHEON, Fraction.TYRELL },
			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATHEON },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL,
					Fraction.MARTEL } };

	private Fraction[][] crowDefault = new Fraction[][] { { Fraction.BARATHEON },
			{ Fraction.BARATHEON, Fraction.TYRELL },
			{ Fraction.STARK, Fraction.LANISTER, Fraction.BARATHEON },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL },
			{ Fraction.BARATHEON, Fraction.LANISTER, Fraction.STARK, Fraction.GREYJOY, Fraction.TYRELL,
					Fraction.MARTEL } };


	Track throneTrack;
	Track swordTrack;
	Track crowTrack;
	SuplyTrack suplyTrack;
	// victoryTrack;

	public void __init(int playersCount) {
		throneTrack = new Track("Throne", throneDefault[playersCount-1]);
		swordTrack = new Track("Sword", swordDefault[playersCount-1]);
		crowTrack = new Track("Crow", crowDefault[playersCount-1]);
		suplyTrack = new SuplyTrack();
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

	public SuplyTrack getSuplyTrack() {
		return suplyTrack;
	}
}
