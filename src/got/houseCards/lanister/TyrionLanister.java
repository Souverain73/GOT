package got.houseCards.lanister;

import got.houseCards.ActiveHouseCard;
import got.model.Fraction;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class TyrionLanister extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        //todo: убрать карту игрока и дать ему перевыбрать карту
        //Если у енго не осталось карты, он остается без карты
    }
}
