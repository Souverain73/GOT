package got.houseCards.lanister;

import got.houseCards.ActiveHouseCard;

import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class TywinLanister extends ActiveHouseCard {
    @Override
    public void onWin() {
        super.onWin();
        owner().addMoney(2);
        logAction("Player " + owner().getNickname() + "get 2 power tokens");
    }
}
