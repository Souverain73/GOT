package got;

/**
 * Class contains all player data
 * @author Souverain73
 *
 */

public class Player {
		
	public int id;
	private Fraction fraction;
	private int specials;
	private int money;
	private int resouces;
	private String nickname;
	
	public Player() {
		nickname = "dumb";
		fraction = null;
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
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return String.format("Player [fraction=%s, specials=%s, money=%s, resouces=%s]", fraction, specials,
				money, resouces);
	}
	
	
}
