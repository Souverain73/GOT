package got.gameObjects.battleDeck;

import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.houseCards.HouseCard;
import got.model.Action;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import got.utils.UI;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Souverain73 on 25.11.2016.
 */

public class BattleDeckObject extends AbstractGameObject<BattleDeckObject> {
    @Override protected BattleDeckObject getThis() {return this;}

    private final ContainerObject attackerContainer;
    private final ContainerObject defenderContainer;

    private final List<PlayerCardObject> attackers = new ArrayList<>();
    private final List<PlayerCardObject> defenders = new ArrayList<>();

    private final ImageObject attackerHouseCardObject;
    private final ImageObject defenderHouseCardObject;

    private final TextObject attackersPowerText;
    private final TextObject defendersPowerText;

    private final MapPartObject attackerRegion;
    private final MapPartObject defenderRegion;

    private final Player attackerPlayer;
    private final Player defenderPlayer;

    private HouseCard attackerCard;
    private HouseCard defenderCard;

    public int attackersPower;
    public int defendersPower;

    public BattleDeckObject(MapPartObject attackerRegion, MapPartObject defenderRegion) {
        this.attackerRegion = attackerRegion;
        this.defenderRegion = defenderRegion;

        this.attackerPlayer = attackerRegion.getOwnerPlayer();
        this.defenderPlayer = defenderRegion.getOwnerPlayer();

        ImageObject background = new ImageObject("BattleDeckBG.png", new Vector2f(140, 50),
                1000, 200)
                .setSpace(DrawSpace.SCREEN);

        attackerContainer = new ContainerObject()
                .setPos(new Vector2f(0, 0))
                .setDim(new Vector2f(500, 200))
                .setSpace(DrawSpace.SCREEN);

        PlayerCardObject attackerCard = new PlayerCardObject(this.attackerRegion.getFraction(), this.attackerRegion.getUnits())
                .setSpace(DrawSpace.SCREEN).setPos(new Vector2f(400, 0));
        attackers.add(attackerCard);
        attackerContainer.addChild(attackerCard);

        defenderContainer = new ContainerObject()
                .setPos(new Vector2f(500, 0))
                .setDim(new Vector2f(500, 200))
                .setSpace(DrawSpace.SCREEN);

        PlayerCardObject defenderCard = new PlayerCardObject(defenderRegion.getFraction(), defenderRegion.getUnits())
                .setSpace(DrawSpace.SCREEN).setPos(new Vector2f(0, 0));
        defenders.add(defenderCard);
        defenderContainer.addChild(defenderCard);

        //House cards
        attackerHouseCardObject = new ImageObject(attackerPlayer.getFraction().getCoverTexture(),
                new Vector2f(20, 50), 100, 200).setSpace(DrawSpace.SCREEN);
        defenderHouseCardObject = new ImageObject(defenderPlayer.getFraction().getCoverTexture(),
                new Vector2f(1160, 50), 100, 200).setSpace(DrawSpace.SCREEN);

        addChild(attackerHouseCardObject);
        addChild(defenderHouseCardObject);

        attackersPowerText = new TextObject("").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(140, 20));
        defendersPowerText = new TextObject("").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(1120, 20));

        addChild(attackersPowerText);
        addChild(defendersPowerText);

        addChild(background);
        background.addChild(attackerContainer);
        background.addChild(defenderContainer);
        updateState(true);
    }

    public void addAttackerHelper(Player player){
        addBattler(player, attackers, attackerContainer);
    }

    public void addDefenderHelper(Player player){
        addBattler(player, defenders, defenderContainer);
    }

    private void addBattler(Player player, List<PlayerCardObject> set, ContainerObject container){

        PlayerCardObject cpc = new PlayerCardObject(player.getFraction(),
                defenderRegion.getUnitsForHelp(player.getFraction()));

        set.add(cpc);
        container.addChild(cpc);
        updateState(true);
    }

    private void updateState(boolean fullUpdate) {
        if (fullUpdate) {
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

                xa -= 100;
                xd += 100;
            }
        }
        attackersPower = getAttackPower(attackers);
        if (attackerCard!=null) attackersPower += attackerCard.getPower();
        if (attackerRegion.getAction() == Action.MOVEMINUS || attackerRegion.getAction() == Action.MOVEPLUS){
            attackersPower += attackerRegion.getAction().getPower();
        }

        attackersPowerText.setText(String.valueOf(attackersPower));

        defendersPower = getDefendPower(defenders);
        if (defenderCard!=null) defendersPower += defenderCard.getPower();
        if (defenderRegion.getAction() == Action.DEFENDPLUS || defenderRegion.getAction() == Action.DEFEND){
            defendersPower += defenderRegion.getAction().getPower();
        }

        defendersPowerText.setText(String.valueOf(defendersPower));

        UI.logAction("Battle deck updated: " + attackersPower + " vs " + defendersPower);
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

    public void placeCard(HouseCard card, Player player){
        if (isAttacker(player.getFraction())){
            attackerCard = card;
            attackerHouseCardObject.setTexture(card.getTexture());
        }
        if (isDefender(player.getFraction())){
            defenderCard = card;
            defenderHouseCardObject.setTexture(card.getTexture());
        }
        if (attackerCard != null && defenderCard != null){
            defenderCard.onPlace(defenderPlayer.getFraction());
            attackerCard.onPlace(defenderPlayer.getFraction());
            updateState(false);
        }
    }

    public MapPartObject getAttackerRegion() {
        return attackerRegion;
    }

    public MapPartObject getDefenderRegion() {
        return defenderRegion;
    }

    public List<PlayerCardObject> getAttackers() {
        return attackers;
    }

    public List<PlayerCardObject> getDefenders() {
        return defenders;
    }

    public Player getAttackerPlayer() {
        return attackerPlayer;
    }

    public Player getDefenderPlayer() {
        return defenderPlayer;
    }

    public boolean isBattleMember(Fraction fraction){
        return  (isAttacker(fraction) || isDefender(fraction));
    }

    public boolean isDefender(Fraction fraction) {
        return fraction == defenderRegion.getFraction();
    }

    public boolean isAttacker(Fraction fraction){
        return fraction == attackerRegion.getFraction();
    }

    public MapPartObject getPlayerRegion(Player player) {
        if (player.getFraction() == defenderRegion.getFraction()) return defenderRegion;
        if (player.getFraction() == attackerRegion.getFraction()) return attackerRegion;
        throw new IllegalStateException("Игрок попыталя получить свой регион в бою, но он не был участником боя.");
    }
}
