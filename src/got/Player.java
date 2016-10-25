package got;

/**
 * Class contains all player data
 * @author Souverain73
 *
 */

public class Player {
		
	private Fraction fraction;
	private int specials;
	private int money;
	private int resouces;
	private int id;
	
	public Player(int id) {
		fraction = Fraction.STARK;
		specials = 3;
		money = 100;
		this.id = id;
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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
}
