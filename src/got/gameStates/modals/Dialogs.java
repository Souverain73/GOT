package got.gameStates.modals;

import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.GameState;
import got.graphics.DrawSpace;
import org.joml.Vector2f;

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
}
