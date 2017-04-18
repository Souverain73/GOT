package got.gameStates.modals;

import got.Constants;
import got.ModalState;
import got.gameObjects.ContainerObject;
import got.gameObjects.HouseCardsListObject;
import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.houseCards.Deck;
import got.houseCards.HouseCard;

import org.joml.Vector2f;

import java.util.List;

import static got.server.PlayerManager.getSelf;

/**
 * Created by Souverain73 on 24.11.2016.
 */
public class Dialogs {
    public enum DialogResult{
        OK,
        CANCEL
    }

    public static class Dialog extends CustomModalState<DialogResult>{
        public Dialog(DialogResult defaultResult) {
            super(defaultResult);
        }
    }

    public static Dialog createConfirmDialog() {
        return createConfirmDialog(new Vector2f(0, 0));
    }

    public static Dialog createConfirmDialog(Vector2f pos){
        Dialog cms = new Dialog(DialogResult.CANCEL);

        ImageObject bg = new ImageObject("DialogBackground.png", pos, 200, 100).setSpace(DrawSpace.WORLD);

        bg.addChild(new ImageButton("buttons/ok.png", 0, 50, 100, 50, null)
                .setSpace(DrawSpace.WORLD)
                .setCallback((sender, param)->{
                    cms.setResult(DialogResult.OK);
                    cms.close();
                }));
        System.out.println();
        bg.addChild(new ImageButton("buttons/cancel.png", 100, 50, 100, 50, null)
                .setSpace(DrawSpace.WORLD)
                .setCallback((sender, param)->{
                    cms.setResult(DialogResult.CANCEL);
                    cms.close();
                }));

        cms.addObject(bg);
        return cms;
    }

    public static DialogResult showConfirmDialog() {
        return showConfirmDialog(new Vector2f(0,0));
    }


    public static DialogResult showConfirmDialog(Vector2f pos){
        Dialog dlg = createConfirmDialog(pos);
        (new ModalState(dlg)).run();
        return dlg.getResult();
    }

    public static CustomModalState<HouseCard> createSelectHouseCardDialog(Deck deck){
        CustomModalState<HouseCard> cms = new CustomModalState<>(null, false);
        ImageButton selectButton = new ImageButton("buttons/select.png", 0,0,200,100,null);
        selectButton.setVisible(false);

        HouseCardsListObject hclo = new HouseCardsListObject(deck){
            @Override
            protected void onSelect() {
                selectButton.setVisible(true);
            }

            @Override
            protected void onUnSelect() {
                selectButton.setVisible(false);
            }
        }.setSpace(DrawSpace.SCREEN);

        hclo.setPos(
                new Vector2f((Constants.SCREEN_WIDTH - hclo.getW())/2, 200)
        );

        selectButton.setPos(new Vector2f((hclo.getW()-200) / 2, 160*2+10))
                .setCallback((sender, param)->cms.setResultAndClose(hclo.getSelectedCard()));

        cms.addObject(hclo);
        hclo.addChild(selectButton);
        return cms;
    }


    public static CustomModalState<HouseCard> createSelectHouseCardDialog(List<HouseCard> cards){
        CustomModalState<HouseCard> cms = new CustomModalState<>(null, false);
        List<HouseCard> cardsToSelect = cards;
        ContainerObject cnt = new ContainerObject().setSpace(DrawSpace.SCREEN).setPos(new Vector2f(0, 0));
        int cx = 290; int cy = 200;

        for (HouseCard card : cardsToSelect){
            ImageButton ib = new ImageButton(card.getTexture(), cx, cy, 100, 300, card).setSpace(DrawSpace.SCREEN);
            ib.setCallback((sender, param) ->{
                cms.setResult((HouseCard) param);
                cms.close();
            });
            cnt.addChild(ib);
            cx+=100;
        }

        cms.addObject(cnt);
        return cms;
    }

}
