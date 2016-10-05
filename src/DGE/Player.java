package DGE;

/**
 * Class contains all player data
 * @author Souverain73
 *
 */

public class Player {
	
	private static Player _instance = null;

	public static Player instance() {
		if (_instance == null) {
			_instance = new Player();
		}
		return _instance;
	}
	
	private Fraction fraction;
	private int specials;
	private int money;
	private int resouces;
	
	private Player() {
		fraction = Fraction.STARK;
		specials = 3;
		money = 100;
	}

	public int getSpecials() {
		return specials;
	}

	public void setSpecials(int specials) {
		this.specials = specials;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getResouces() {
		return resouces;
	}

	public void setResouces(int resouces) {
		this.resouces = resouces;
	}

	public Fraction getFraction() {
		return fraction;
	}

	public void setFraction(Fraction fraction) {
		this.fraction = fraction;
	}
	
}
