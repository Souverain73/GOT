package got.model;

import got.utils.Utils;

import java.util.Arrays;
import java.util.EnumMap;

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
            if (armySize == 1) continue;
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

    public static void main(String[] args) {
        //test canHaveArmies;
        //max is  {4, 3, 2, 2, 2}
        test(new int[] {1}, true);
        test(new int[] {4, 3, 2, 2, 2, 1, 1}, true);
        test(new int[] {4, 3, 2, 2, 2, 2}, false);
        test(new int[] {4, 3, 3, 2, 2}, false);
        test(new int[] {5, 1}, false);
        test(new int[] {}, true);
    }

    public static void test(int[] data, boolean expectedResult){
        SuplyTrack track = new SuplyTrack();
        System.out.println("Test for:" + Arrays.toString(data));

        boolean result = track.canHaveArmies(Fraction.STARK, data);

        System.out.println(result == expectedResult ? "Passed":"Failed");
    }
}
