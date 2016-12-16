package got.gameObjects.battleDeck;

import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Souverain73 on 25.11.2016.
 */

public class BattleDeckObject extends AbstractGameObject<BattleDeckObject> {
    @Override protected BattleDeckObject getThis() {return this;}

    private final ContainerObject attackerContainer;
    private final ContainerObject defenderContainer;

    private final List<PlayerCardObject> attackers = new ArrayList<>();
    private final List<PlayerCardObject> defenders = new ArrayList<>();

    private final MapPartObject attacker;
    private final MapPartObject defender;


    public int attackersPower;
    public int defendersPower;

    public BattleDeckObject(MapPartObject atacker, MapPartObject defender) {
        this.attacker = atacker;
        this.defender = defender;
        ImageObject background = new ImageObject("BattleDeckBG.png", new Vector2f(140, 50),
                1000, 200)
                .setSpace(DrawSpace.SCREEN);

        attackerContainer = new ContainerObject()
                .setPos(new Vector2f(0, 0))
                .setDim(new Vector2f(500, 200))
                .setSpace(DrawSpace.SCREEN);

        PlayerCardObject attackerCard = new PlayerCardObject(this.attacker.getFraction(), this.attacker.getUnits())
                .setSpace(DrawSpace.SCREEN).setPos(new Vector2f(400, 0));
        attackers.add(attackerCard);
        attackerContainer.addChild(attackerCard);

        defenderContainer = new ContainerObject()
                .setPos(new Vector2f(500, 0))
                .setDim(new Vector2f(500, 200))
                .setSpace(DrawSpace.SCREEN);

        PlayerCardObject defenderCard = new PlayerCardObject(defender.getFraction(), defender.getUnits())
                .setSpace(DrawSpace.SCREEN).setPos(new Vector2f(0, 0));
        defenders.add(defenderCard);
        defenderContainer.addChild(defenderCard);

        addChild(background);
        background.addChild(attackerContainer);
        background.addChild(defenderContainer);
        updateState();
    }

    public void addAttackerHelper(Player player){
        addBattler(player, attackers, attackerContainer);
    }

    public void addDefenderHelper(Player player){
        addBattler(player, defenders, defenderContainer);
    }

    private void addBattler(Player player, List<PlayerCardObject> set, ContainerObject container){

        PlayerCardObject cpc = new PlayerCardObject(player.getFraction(),
                defender.getUnitsForHelp(player.getFraction()));

        set.add(cpc);
        container.addChild(cpc);
        updateState();
    }

    private void updateState() {
        int xa = 400;
        int xd = 000;
        for (int i = 0; i < 4; i++) {
            if (i < attackers.size()) {
                PlayerCardObject attacker = attackers.get(i);
                attacker.setPos(new Vector2f(xa, 0));
            }

            if (i < defenders.size()) {
                PlayerCardObject defender = defenders.get(i);
                defender.setPos(new Vector2f(xd, 0));
            }

            xa--;
            xd++;
        }

        attackersPower = getAttackPower(attackers);
        defendersPower = getDefendPower(defenders);
    }

    private int getAttackPower(List<PlayerCardObject> list) {
        return list.stream()
                .flatMap(card-> Arrays.stream(card.getUnits()))
                .mapToInt(Unit::getDamage)
                .sum();
    }

    private int getDefendPower(List<PlayerCardObject> list) {
        return list.stream()
                .flatMap(card-> Arrays.stream(card.getUnits()))
                .filter(unit->unit != Unit.SIEGE)
                .mapToInt(Unit::getDamage)
                .sum();
    }

    public MapPartObject getAttacker() {
        return attacker;
    }

    public MapPartObject getDefender() {
        return defender;
    }

    public boolean isBattleMember(Fraction fraction){
        return  (attacker.getFraction() == fraction || defender.getFraction() == fraction);
    }

    public boolean isDefender(Fraction fraction) {
        return fraction == defender.getFraction();
    }

    public boolean isAttacker(Fraction fraction){
        return fraction == attacker.getFraction();
    }

    public MapPartObject getPlayerRegion(Player player) {
        if (player.getFraction() == defender.getFraction()) return defender;
        if (player.getFraction() == attacker.getFraction()) return attacker;
        throw new IllegalStateException("Игрок попыталя получить свой регион в бою, но он не был участником боя.");
    }
}
