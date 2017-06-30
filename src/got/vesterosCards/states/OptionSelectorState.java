package got.vesterosCards.states;

import got.Constants;
import got.gameObjects.ContainerObject;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.StateMachine;
import got.gameStates.modals.WaitingModal;
import got.graphics.DrawSpace;
import got.utils.UI;
import org.joml.Vector2f;

import java.util.function.BiConsumer;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class OptionSelectorState extends WaitingModal {
    private int result;
    private boolean active;

    public OptionSelectorState(int defaultResult, boolean active){
        this.result = defaultResult;
        this.active = active;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);

        if (!active) return;

        BiConsumer<GameObject, Object> buttonCallback = (gameObject, object) -> setResultAndClose((Integer) object);

        ContainerObject cnt = new ContainerObject(){{
            addChild(new ImageButton("buttons/1.png", 0, 0, 50, 50, 1).setCallback(buttonCallback));
            addChild(new ImageButton("buttons/2.png", 50, 0, 50, 50, 2).setCallback(buttonCallback));
            addChild(new ImageButton("buttons/3.png", 100, 0, 50, 50, 3).setCallback(buttonCallback));
        }}.setSpace(DrawSpace.SCREEN)
                .setPos(new Vector2f((Constants.SCREEN_WIDTH - 150)/2, 175));

        addObject(cnt);
    }

    private void setResultAndClose(int result){
        this.result = result;
        resumeModal();
    }

    public int getResult() {
        return result;
    }

    @Override
    protected void onResume() {
        UI.systemMessage("Modal state resumed");
    }

}
