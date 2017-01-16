package got.gameObjects.battleDeck;

import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.model.Action;
import got.model.BattleSide;
import got.model.Fraction;
import got.model.Unit;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Souverain73 on 25.11.2016.
 */
public class BattleCardObject extends AbstractGameObject<BattleCardObject>{
    @Override protected BattleCardObject getThis() {return this;}

    private static final int BASE_UNITS_X = 10;
    private static final int BASE_UNITS_Y = 110;
    public static final int  UNITS_SIZE = 30;
    public static final int UNITS_SPACING = 10;

    private final Fraction playerFraction;

    private AbstractGameObject view;
    private Unit[] units;
    private UnitObject[] unitObjects;
    private Action regionAction;
    private List<UnitEffect> effects;

    public BattleCardObject(Fraction playerFraction, Action action, Unit[] units){
        this.playerFraction = playerFraction;
        this.units = units;
        this.regionAction = action;
        effects = null;
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
        if (regionAction!=null) {
            result.addChild(new ImageObject(regionAction.getTexture(), new Vector2f(50, 50), 40, 40)
                    .setSpace(DrawSpace.SCREEN));
        }

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
            }
        }

        //create new units
        createUnitObjects();

        //place new units on card
        placeUnits();
    }

    /**
     * Don't use units to calculate battle power directly
     * @return
     */
    public Unit[] getUnits(){
        return units;
    }

    public int getBattlePower(BattleSide side, boolean isSiege){
        int res = 0;
        res += Arrays.stream(units)
                .filter(unit->(unit==Unit.SIEGE && isSiege && side==BattleSide.ATTACKER) || unit!=Unit.SIEGE)
                .mapToInt(unit->calcPower(unit)).sum();
        if (side == BattleSide.ATTACKER){
            if (regionAction == Action.MOVE ||
                    regionAction == Action.MOVEPLUS ||
                    regionAction == Action.MOVEMINUS){
                res += regionAction.getPower();
            }
        }

        if (side == BattleSide.DEFENDER){
            if (regionAction == Action.DEFEND ||
                    regionAction == Action.DEFENDPLUS){
                res += regionAction.getPower();
            }
        }

        return res;
    }

    public void addEffect(UnitEffect effect){
        if (effects == null){
            effects = new ArrayList<>();
        }
        effects.add(effect);
        effects.sort((a, b)->a.getPriority() - b.getPriority());
    }

    public int calcPower(Unit unit){
        int power = unit.getDamage();
        if (effects == null) return power;
        for (UnitEffect effect : effects){
            power = effect.getAffectedPower(power);
        }
        return power;
    }

    public interface UnitEffect{
        int getAffectedPower(int power);
        boolean isAffected(Unit unit);
        int getPriority();
    }

    public class defaultEffect implements UnitEffect{

        @Override
        public int getAffectedPower(int power) {
            return power;
        }

        @Override
        public boolean isAffected(Unit unit) {
            return false;
        }

        @Override
        public int getPriority() {
            return 0;
        }
    }
}
