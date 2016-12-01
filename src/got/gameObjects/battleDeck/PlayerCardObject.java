package got.gameObjects.battleDeck;

import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.model.Fraction;
import got.model.Unit;
import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Created by Souverain73 on 25.11.2016.
 */
public class PlayerCardObject extends AbstractGameObject<PlayerCardObject>{
    @Override protected PlayerCardObject getThis() {return this;}

    private static final int BASE_UNITS_X = 10;
    private static final int BASE_UNITS_Y = 110;
    public static final int  UNITS_SIZE = 30;
    public static final int UNITS_SPACING = 10;

    private final Fraction playerFraction;

    private AbstractGameObject view;
    private Unit[] units;
    private UnitObject[] unitObjects;

    public PlayerCardObject(Fraction playerFraction, Unit[] units){
        this.playerFraction = playerFraction;
        this.units = units;
        addChild(view = createView());
        updateUnits();
    }

    private ContainerObject createView() {
        ContainerObject result = new ContainerObject();
        ///BG
        result.addChild(new ImageObject("PlayerCardBG.png", new Vector2f(0, 0), 100, 200)
                .setSpace(DrawSpace.SCREEN));
        //Fraction icon
        result.addChild(new ImageObject(playerFraction.getBackTexture(), new Vector2f(10,10), 80, 80)
                .setSpace(DrawSpace.SCREEN));

        return result;
    }

    private void createUnitObjects(){
        unitObjects = new UnitObject[units.length];
        for (int i = 0; i < units.length; i++) {
            Unit unit = units[i];
            unitObjects[i] = new UnitObject(unit)
                            .setSpace(DrawSpace.SCREEN)
                            .setSize(UNITS_SIZE);
            view.addChild(unitObjects[i]);
        }
    }

    private void placeUnits(){
        int x = BASE_UNITS_X;
        int y = BASE_UNITS_Y;
        for (int i=0; i<units.length; i++){
            System.out.printf("Place unit at x=%d y=%d", x, y);
            unitObjects[i].setPos(new Vector2f( x, y));
            x += UNITS_SIZE + UNITS_SPACING * 2;
            if (x > 100 /*ширина панельки*/){
                x-=100;
                y+= UNITS_SIZE + UNITS_SPACING*2;
            }
        }
    }

    private void updateUnits(){
        //remove old units
        if (unitObjects!=null) {
            for (UnitObject unitObject : unitObjects) {
                removeChild(unitObject);
                unitObject.finish();
                unitObject = null;
            }
        }

        //create new units
        createUnitObjects();

        //place new units on cadrd
        placeUnits();
    }

    public Unit[] getUnits(){
        return units;
    }
}
