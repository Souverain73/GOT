package got.gameObjects;

import got.Constants;
import got.GameClient;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.GameState;
import got.houseCards.Deck;
import got.houseCards.HouseCard;
import org.joml.Vector2f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Souverain73 on 17.04.2017.
 */
public class HouseCardsListObject extends AbstractGameObject<HouseCardsListObject> {
    Deck deck;
    ImageButton[] cardButtons;
    int currentScaled = -1;
    private float newScale;

    @Override
    protected HouseCardsListObject getThis() {
        return this;
    }

    public HouseCardsListObject(Deck deck){
        super();
        this.deck = deck;
        List<HouseCard> cards = deck.getActiveCards();
        cardButtons = new ImageButton[cards.size()];
        int i=0;
        for(HouseCard card : cards){
            ImageButton cardButton = new ImageButton(card.getTexture(), i*100, 0, 100, 160, i).setCallback(this::clickCallback);
            cardButtons[i] = cardButton;
            addChild(cardButton);
            i++;
        }
        this.w = (i-1)*100;
        this.h = 160;
    }

    private void clickCallback(GameObject sender, Object param) {
        int index = (int) param;
        if (index == currentScaled) {
            zoomOut(index);
            currentScaled = -1;
        } else {
            if (currentScaled != -1) {
                zoomOut(currentScaled);
            }
            zoomIn(index);
            currentScaled = index;
        }
    }

    @Override
    public void draw(GameState state) {
        super.draw(state);
    }

    private void zoomOut(int index) {
        AnimatedObject ao = new AnimatedObject(cardButtons[index]);
        ao.scale(1.0f, 1000);
        ao.move(new Vector2f(index*100, 0), 1000).after(()->cardButtons[index].setPriority(2));
    }

    private void zoomIn(int index){
        ImageButton card = cardButtons[index];
        AnimatedObject ao = new AnimatedObject(card);
        card.setPriority(3);
        GameClient.instance().registerTask(()->{
            removeChild(card);
            addChild(card);
        });
        newScale = 3.0f;
        ao.scale(newScale, 1000);
        ao.move(new Vector2f((this.w - 100*newScale/2) / 2, this.h/2 - 100), 1000);
    }

}
