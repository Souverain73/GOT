package got.gameObjects.gui;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class NumberSelectorObject extends AbstractGameObject<NumberSelectorObject>{
    @Override protected NumberSelectorObject getThis() {return this;}
    private int currentValue;
    private TextObject toValue;

    public NumberSelectorObject(int from, int to, int def){

    }
}
