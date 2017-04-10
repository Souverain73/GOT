package got.gameStates.test;

import got.animations.Animator;
import got.animations.Easings;
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
            Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.IN_OUT_CUBIC);
        }));
        addObject(new ImageButton("buttons/minus.png", 100, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.OUT_CUBIC);
                }));
        addObject(new ImageButton("buttons/minus.png", 200, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.IN_CUBIC);
                }));
        addObject(new ImageButton("buttons/minus.png", 300, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 2000, object::setPos).setEasing(Easings.IN_OUT_BACK);
                }));
        addObject(new ImageButton("buttons/minus.png", 400, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.OUT_BACK);
                }));
        addObject(new ImageButton("buttons/minus.png", 500, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.IN_BACK);
                }));
        addObject(new ImageButton("buttons/minus.png", 600, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.IN_OUT_SINE);
                }));
        addObject(new ImageButton("buttons/minus.png", 700, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.IN_SINE);
                }));
        addObject(new ImageButton("buttons/minus.png", 800, 0, 100, 100, obj).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, params)->{
                    AbstractGameObject object = (AbstractGameObject) params;
                    Animator.animateVector2f(new Vector2f(100, 100), new Vector2f(700,100), 1000, object::setPos).setEasing(Easings.OUT_SINE);
                }));
    }
}
