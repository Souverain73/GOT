package got.model;

import got.GameClient;
import got.gameObjects.MapPartObject;
import got.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Souverain73
 */
public class SuplyTrack {
    private static final int LEVELS[][] = new int[][]{
            {2, 2},
            {3, 2},
            {3, 2, 2},
            {3, 2, 2, 2},
            {3, 3, 2, 2},
            {4, 3, 2, 2},
            {4, 3, 2, 2, 2}
    };

    private static final int MAX_LEVEL = 6;
    private static final int MIN_LEVEL = 0;

    //init it with default values;
    private EnumMap<Fraction, Integer> fractionPos = new EnumMap<Fraction, Integer>(Fraction.class){{
        put(Fraction.BARATEON, 6);
        put(Fraction.GREYJOY, 6);
        put(Fraction.LANISTER, 6);
        put(Fraction.MARTEL, 6);
        put(Fraction.STARK, 6);
        put(Fraction.TIREL, 6);
    }};

    public int getPos(Fraction fract){
        return fractionPos.get(fract);
    }

    public void setPos(Fraction fract, int pos) {
        fractionPos.put(fract, Utils.limitInt(pos, MIN_LEVEL, MAX_LEVEL));
    }

    public int[] getMaxArmiesValues(Fraction fraction){
        return LEVELS[fractionPos.get(fraction)];
    }

    public boolean canHaveArmies(Fraction fraction, int [] armySizes){
        int [] maxArmy = getMaxArmiesValues(fraction);
        boolean [] slotUsed = new boolean[maxArmy.length];
        for (int armySize : armySizes) {
            if (armySize <= 1) continue;
            boolean correct = false;
            int slot = -1;

            for (int j = 0; j < maxArmy.length; j++) {
                int maxArmyItem = maxArmy[j];
                if (armySize > maxArmyItem || slotUsed[j]) continue;
                if (armySize == maxArmyItem) {
                    correct = true;
                    slot = j;
                    break;
                }
                if (armySize < maxArmyItem) {
                    if (slot == -1) {
                        slot = j;
                        correct = true;
                    } else {
                        if (maxArmy[slot] > armySize) {
                            slot = j;
                            correct = true;
                        }
                    }
                }
            }
            if (!correct) return false;
            slotUsed[slot] = true;
        }

        return true;
    }

    public boolean canMove(Fraction fraction, MapPartObject from, MapPartObject to, int unitsCount){
        return canMove(fraction,
                GameClient.shared.gameMap.getArmySizesForFraction(fraction),
                from.getUnitsCount(),
                to.getUnitsCount(),
                unitsCount);
    }

    public boolean canMove(Fraction fraction, int [] currentArmySizes, int fromArmySize, int toArmySize, int sizeArmyToMove){
        boolean changedFrom = false, changedTo = false;
        List<Integer> newArmySizes = new ArrayList<>();

        for (int i = 0; i < currentArmySizes.length; i++) {
            int size = currentArmySizes[i];
            if (size == fromArmySize && !changedFrom){
                size -= sizeArmyToMove;
                changedFrom = true;
            }else if(size == toArmySize && !changedTo){
                size += sizeArmyToMove;
                changedTo = true;
            }
            newArmySizes.add(size);
        }

        if (!changedTo){
            newArmySizes.add(sizeArmyToMove);
        }

        int[] newArmySizesInt = newArmySizes.stream().mapToInt(val->val).toArray();

        return canHaveArmies(fraction, newArmySizesInt);
    }

    public static void main(String[] args) {
        //testCanHaveArmy canHaveArmies;
        //max is  {4, 3, 2, 2, 2}
        testCanHaveArmy(new int[] {1}, true);
        testCanHaveArmy(new int[] {4, 3, 2, 2, 2, 1, 1}, true);
        testCanHaveArmy(new int[] {4, 3, 2, 2, 2, 2}, false);
        testCanHaveArmy(new int[] {4, 3, 3, 2, 2}, false);
        testCanHaveArmy(new int[] {5, 1}, false);
        testCanHaveArmy(new int[] {}, true);

        testCanMove(new int[] {3, 3, 1}, 3, 3, 2, false);
        testCanMove(new int[] {3, 3, 1}, 3, 3, 3, false);
        testCanMove(new int[] {3, 3, 1}, 3, 3, 1, true);

        testCanMove(new int[] {4, 3, 2, 2, 2, 1, 1}, 4, 0, 2, false);
        testCanMove(new int[] {4, 3, 2, 2, 2, 1, 1}, 4, 0, 3, true);
        testCanMove(new int[] {4, 3, 2, 2, 2, 1, 1}, 4, 0, 4, true);

        testCanMove(new int[] {3, 1, 1}, 3, 0, 2, true);
    }

    public static void testCanHaveArmy(int[] data, boolean expectedResult){
        SuplyTrack track = new SuplyTrack();
        System.out.println("Test can have for:" + Arrays.toString(data));

        boolean result = track.canHaveArmies(Fraction.STARK, data);

        System.out.println(result == expectedResult ? "Passed":"Failed");
    }

    public static void testCanMove(int [] currentArmySizes, int fromArmySize, int toArmySize, int sizeArmyToMove, boolean expectedResult){
        SuplyTrack track = new SuplyTrack();
        System.out.println("Test can move :currentArmySizes = " + Arrays.toString(currentArmySizes) + ", fromArmySize = [" + fromArmySize + "], toArmySize = [" + toArmySize + "], sizeArmyToMove = [" + sizeArmyToMove + "]");

        boolean result = track.canMove(Fraction.STARK, currentArmySizes, fromArmySize, toArmySize, sizeArmyToMove);

        System.out.println(result == expectedResult ? "Passed":"Failed");

    }
}