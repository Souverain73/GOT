package got.houseCards.lanister;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.ModalState;
import got.gameStates.StateMachine;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.houseCards.HouseCard;
import got.model.Fraction;
import got.network.Packages;
import org.joml.Vector2f;

import static got.gameStates.modals.Dialogs.createConfirmDialog;
import static got.server.PlayerManager.getSelf;
import static got.server.PlayerManager.instance;
import static got.utils.UI.logAction;
import static sun.misc.PostVMInitHook.run;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class TyrionLanister extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);

        (new ModalState(new ConfirmUseDialog(placerFraction, enemyFraction), true, true)).run();
        //todo: убрать карту игрока и дать ему перевыбрать карту
        //Если у него не осталось карты, он остается без карты

    }

    private class ConfirmUseDialog extends WaitingModal{
        Fraction placerFraction;
        Fraction enemyFraction;

        public ConfirmUseDialog(Fraction placerFraction, Fraction enemyFraction) {
            this.placerFraction = placerFraction;
            this.enemyFraction = enemyFraction;
        }

        @Override
        public void enter(StateMachine stm) {
            if (getSelf().getFraction() == placerFraction){
                GameClient.instance().setTooltipText("Убрать карту противника?");
                Dialogs.Dialog  dlg = Dialogs.createConfirmDialog(new Vector2f(100,100));
                (new ModalState(dlg)).run();
                if (dlg.getResult() == Dialogs.DialogResult.OK){
                    //todo: убрать карту противника и дать ему перевыбрать
                    GameClient.instance().send(new Packages.Confirm());
                }else{
                    resume();
                }
            }else{
                GameClient.instance().setTooltipText("Тирион решает что делать");
            }
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerConfirm) {
                if (getSelf().getFraction() == enemyFraction) {

                    GameClient.instance().registerTask(()->{
                        CustomModalState<HouseCard> cms = Dialogs.createSelectHouseCardDialog();
                        (new ModalState(cms)).run();
                        HouseCard selectedCard = cms.getResult();
                        GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
                        resume();
                    });
                }
            }
        }

        @Override
        protected void onResume() {
        }
    }
}
