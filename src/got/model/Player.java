package got.model;

import got.Constants;

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
	private boolean ready;
	
	public Player() {
		nickname = "dumb";
		fraction = null;
		specials = 3;
		money = 100;
		ready = false;
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
	
	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	@Override
	public String toString() {
		return String.format("Player [fraction=%s, specials=%s, money=%s, resouces=%s, ready=%s]", fraction, specials,
				money, resouces, ready);
	}
	
	public void addMoney(int money){
		this.money+=money;
		if (this.money>Constants.MAX_MONEY){
			this.money = Constants.MAX_MONEY;
		}
	}
	
}
