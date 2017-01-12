package got.houseCards.lanister;

import got.GameClient;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Unit;

import java.util.Arrays;

/**
 * Created by Souverain73 on 11.01.2017.
 */

public class SerKevanLanister extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);

        if(GameClient.shared.battleDeck.isAttacker(fraction)){
            bonusPower = (int)GameClient.shared.battleDeck.getAttackers().stream().flatMap(card->
                Arrays.stream(card.getUnits())
            ).filter(unit->unit == Unit.SOLDIER).count();
        }
    }
}
