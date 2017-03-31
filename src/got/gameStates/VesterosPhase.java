package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.network.Packages;
import got.server.GameServer.PlayerConnection;
import got.vesterosCards.VesterosCard;
import got.vesterosCards.VesterosCards;

//TODO: реализовать отображение карт и выбор по необходимым картам.
public class VesterosPhase extends AbstractGameState {
	private static final String name = "VesterosPhase";
	VesterosCard[] cards = new VesterosCard[3];
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {

	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof Packages.OpenCard) {
			Packages.OpenCard msg = (Packages.OpenCard) pkg;
			VesterosCard card = VesterosCards.getCard(msg.card);
			cards[msg.number] = card;
			GameClient.instance().logMessage("vesteros.cardOpen", msg.number, card.getTitle());
		}
	}
}
