package got.gameObjects.gui;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.gameObjects.interfaceControls.TransparentButton;
import got.graphics.Colors;
import got.graphics.text.FontTrueType;
import got.utils.Utils;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class NumberSelectorObject extends AbstractGameObject<NumberSelectorObject>{
    private final int from;
    private final int to;

    @Override protected NumberSelectorObject getThis() {return this;}
    private int currentValue;
    private TextObject toValue;

    public NumberSelectorObject(int from, int to, int def){
        this.from = from;
        this.to = to;
        this.currentValue = def;
        addChild(new ImageObject("dialogBackground.png", 50, 50));
        addChild(toValue = new TextObject(new FontTrueType("GotKG", 50, Colors.RED.asVector3()), ""));
        addChild(new TransparentButton(0, 0, 50, 25, null).setCallback((gameObject, o) -> addValue(1)));
        addChild(new TransparentButton(0, 25, 50, 25, null).setCallback((gameObject, o) -> addValue(-1)));
        updateValue();
    }

    private void addValue(int value){
        currentValue = Utils.limitInt(currentValue += value, from, to);
        updateValue();
    }

    private void updateValue(){
        toValue.setText(String.valueOf(currentValue));
    }

    public int getValue(){
        return currentValue;
    }
}
