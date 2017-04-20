package got.wildlings;

import got.utils.Utils;

import java.util.HashMap;
import java.util.List;

import static got.translation.Translator.tt;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class Wildlings {
    int level = 0;
    static int maxLevel = 12;
    private static Wildlings _instance = new Wildlings();

    public static Wildlings instance(){
        return _instance;
    }

    private HashMap<Integer, WildlingsCard> cards = new HashMap<>();
    private HashMap<String, WildlingsCard> cardsByTitle = new HashMap<>();
    private List<WildlingsCard> deck;

    private void add(WildlingsCard card){
        cards.put(card.getID(), card);
        cardsByTitle.put(card.getTitle(), card);
        deck.add(card);
    }

    private Wildlings(){
        add(new CommonWildlingsCard("king.png",     tt("wildlings.king"),       "King"));
        add(new CommonWildlingsCard("robbers.png",  tt("wildlings.robbers"),    "Robbers"));
        add(new CommonWildlingsCard("silence.png",  tt("wildlings.silence"),    "Silence"));
        add(new CommonWildlingsCard("killers.png",  tt("wildlings.killers"),    "Killers"));
        add(new CommonWildlingsCard("riders.png",   tt("wildlings.riders"),     "Riders"));
        add(new CommonWildlingsCard("squad.png",    tt("wildlings.squad"),      "Squad"));
        add(new CommonWildlingsCard("horde.png",    tt("wildlings.horde"),      "Horde"));
        add(new CommonWildlingsCard("scout.png",    tt("wildlings.scout"),      "Scout"));
        add(new CommonWildlingsCard("gathering.png",tt("wildlings.gathering"),  "Gathering"));

        deck = Utils.shuffle(deck);
    }

    public WildlingsCard getTopCard(){
        return deck.get(0);
    }

    public void moveTopCardToEnd(){
        WildlingsCard top = getTopCard();
        deck.remove(top);
        deck.add(top);
    }

    public void nextCard(){
        deck.remove(0);
    }

    public int getLevel(){
        return level;
    }

    public void nextLevel(){
        level += 2;
    }

    public boolean readyToAttack(){
        return level >= maxLevel;
    }

    public void attack(){
        level = 0;
    }
}
