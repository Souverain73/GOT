package got.houseCards.lanister;

import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.MapPartObject;
import got.gameStates.AbstractGameState;
import got.gameStates.ActionPhase;
import got.gameStates.GameState;
import got.gameStates.StateMachine;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.network.Packages;

import static got.server.PlayerManager.getSelf;
import static sun.misc.PostVMInitHook.run;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class CerseiLanister extends ActiveHouseCard {
    private Fraction enemyFraction;
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        if (GameClient.shared.battleDeck.getAttackerPlayer().getFraction() == fraction){
            enemyFraction = GameClient.shared.battleDeck.getDefenderPlayer().getFraction();
        }else{
            enemyFraction = GameClient.shared.battleDeck.getAttackerPlayer().getFraction();
        }
    }

    @Override
    public void onWin() {
        super.onWin();
        if (placerFraction == getSelf().getFraction()){
            GameClient.instance().setTooltipText("Выберите, какой приказ убрать");
            (new ModalState(new RemoveActionState(), true, true)).run();
        }else{
            GameClient.instance().setTooltipText("Серцея выбирает какой приказ убрать");
        }

        //todo: убрать приказ врага на выбор
    }

    private class RemoveActionState extends ActionPhase{
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            enableRegionsWithActions(enemyFraction);
        }

        private void enableRegionsWithActions(Fraction fraction) {
            GameClient.shared.gameMap.setEnabledByCondition(reg->
                reg.getFraction() == fraction && reg.getAction() != null
            );
        }

        @Override
        public void click(InputManager.ClickEvent event) {
            super.click(event);
            if (event.getTarget() instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) event.getTarget();
                GameClient.instance().send(new Packages.SetAction(region.getID(), null));
                GameClient.instance().closeModal();
            }
        }
    }
}
