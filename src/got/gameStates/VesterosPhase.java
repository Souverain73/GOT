package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.gameObjects.ImageObject;
import got.graphics.DrawSpace;
import got.network.Packages;
import got.vesterosCards.VesterosCard;
import got.vesterosCards.VesterosCards;
import org.joml.Vector2f;

//TODO: реализовать отображение карт и выбор по необходимым картам.
public class VesterosPhase extends AbstractGameState {
	private static final String name = "VesterosPhase";
	VesterosCard[] cards = new VesterosCard[3];
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof Packages.OpenCard) {
			Packages.OpenCard msg = (Packages.OpenCard) pkg;
			VesterosCard card = VesterosCards.getCard(msg.card);
			cards[msg.number] = card;
			GameClient.instance().logMessage("vesteros.cardOpen", msg.number+1, card.getTitle());
			GameClient.instance().registerTask(()->{
				addObject(new ImageObject(card.getTexture(), new Vector2f(Constants.SCREEN_WIDTH / 2 - 100, 100 * (msg.number) + 1), 200, 100).setSpace(DrawSpace.SCREEN));
			});
		}
	}
}
