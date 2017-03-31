package got.vesterosCards;

import org.joml.Vector3f;

import java.util.HashMap;

/**
 * Created by Souverain73 on 30.03.2017.
 */
public class VesterosCards {
    static HashMap<Integer, VesterosCard> cards = new HashMap<>();
    static HashMap<String, VesterosCard> cardsByTitle = new HashMap<>();
    private static void add(VesterosCard card){
        cards.put(card.getID(), card);
        cardsByTitle.put(card.getTitle(), card);
    }

    public static void init(){
        add(new CommonVesterosCard("AutumnRains.png", "Autumn Rains"));
        add(new CommonVesterosCard("BattleOfKings.png", "Battle Of Kings"));
        add(new CommonVesterosCard("BlackWings.png", "Black Wings"));
        add(new CommonVesterosCard("CollectUnits.png", "Collect Units"));
        add(new CommonVesterosCard("FeastForCrows.png", "Feast For Crows"));
        add(new CommonVesterosCard("GameOfThrones.png", "Game Of Thrones"));
        add(new CommonVesterosCard("PutToSword.png", "Put To Sword"));
        add(new CommonVesterosCard("SeaOfStorms.png", "Sea Of Storms"));
        add(new CommonVesterosCard("StormOfSwords.png", "Storm Of Swords"));
        add(new CommonVesterosCard("SummerTime1.png", "Summer Time 1"));
        add(new CommonVesterosCard("SummerTime2.png", "Summer Time 2"));
        add(new CommonVesterosCard("Suply.png", "Suply"));
        add(new CommonVesterosCard("ThroneOfSwords.png", "Throne Of Swords"));
        add(new CommonVesterosCard("WebOfLie.png", "Web Of Lie"));
        add(new CommonVesterosCard("Wildlings.png", "Wildlings"));
        add(new CommonVesterosCard("WinterTime1.png", "Winter Time 1"));
        add(new CommonVesterosCard("WinterTime2.png", "Winter Time 2"));
    }

    public static VesterosCard getCard(int id){
        return cards.get(id);
    }

    public static VesterosCard getCardByName(String name){
        return cardsByTitle.get(name);
    }
}
