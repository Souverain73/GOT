package got.gameStates.test;

import got.animations.Animator;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public class AnimationTestState extends AbstractGameState {
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        AbstractGameObject<?> obj;
        addObject(obj = new ImageObject("buttons/plus.png", new Vector2f(100, 100), 100, 100).setSpace(DrawSpace.SCREEN));
        addObject(new ImageButton("buttons/minus.png", 0, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
        .setCallback((sender, params)->{
            AbstractGameObject object = (AbstractGameObject) params;
            Animator.animateVector2f(object.getPos(), new Vector2f(700,700), 1000, object::setPos);
        }));
    }
}
