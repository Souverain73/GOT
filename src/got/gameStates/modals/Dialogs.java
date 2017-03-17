package got.gameStates.modals;

import got.ModalState;
import got.gameObjects.ContainerObject;
import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.graphics.DrawSpace;
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

        bg.addChild(new ImageButton("ButtonOK.png", 0, 50, 100, 50, null)
                .setSpace(DrawSpace.WORLD)
                .setCallback((sender, param)->{
                    cms.setResult(DialogResult.OK);
                    cms.close();
                }));
        System.out.println();
        bg.addChild(new ImageButton("ButtonCancel.png", 100, 50, 100, 50, null)
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

    public static CustomModalState<HouseCard> createSelectHouseCardDialog() {
        return createSelectHouseCardDialog(getSelf().getDeck().getActiveCards());
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
