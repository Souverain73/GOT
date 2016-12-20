package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.ImageObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.graphics.DrawSpace;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 28.11.2016.
 */
public class HelpPhase extends ActionPhase {

    private BattleDeckObject bdo;

    enum BattleSide{
        SIDE_ATTACKER(Packages.Help.SIDE_ATTACKER),
        SIDE_DEFENDER(Packages.Help.SIDE_DEFENDER),
        SIDE_NONE(Packages.Help.SIDE_NONE);

        private int id;
        BattleSide(int id){
            this.id = id;
        }

        public int getId(){
            return id;
        }
    }

    private final String name = "HelpPhase";

    @Override
    public int getID() {
        return StateID.HELP_PHASE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameClient.shared.gameMap.disableAllRegions();
    }

    @Override
    public void click(InputManager.ClickEvent event) {
        super.click(event);
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        GameClient.instance().registerTask(()->{
            if (pkg instanceof Packages.InitBattle) {
                Packages.InitBattle msg = (Packages.InitBattle) pkg;
                System.out.printf("Init battle from %d to %d", msg.from, msg.to);
                bdo = new BattleDeckObject(
                        GameClient.shared.gameMap.getRegionByID(msg.from),
                        GameClient.shared.gameMap.getRegionByID(msg.to)
                );
                GameClient.shared.battleDeck = bdo;
            }

            if (pkg instanceof Packages.PlayerTurn) {
                Packages.PlayerTurn msg = (Packages.PlayerTurn) pkg;
                if (PlayerManager.getSelf().id == msg.playerID){
                    if (bdo.isBattleMember(PlayerManager.getSelf().getFraction())){
                        GameClient.instance().sendReady(false);
                    }else if(bdo.getDefender().canHelp(PlayerManager.getSelf().getFraction())){
                        int battlePointsToHelp = bdo.getDefender().getBattlePowerForHelpers(
                                PlayerManager.getSelf().getFraction()
                        );

                        //TODO: отображать количество очков в диалоге выбора.
                        BattleSide userSelect = showSelectSideDialogAndGetResult();

                        GameClient.instance().send(new Packages.Help(userSelect.getId()));
                    }else {
                        GameClient.instance().sendReady(false);
                    }
                }
            }
            if (pkg instanceof Packages.PlayerHelp) {
                Packages.PlayerHelp msg = (Packages.PlayerHelp) pkg;
                Player player = PlayerManager.instance().getPlayer(msg.player);
                if (msg.side == Packages.Help.SIDE_NONE){
                    System.out.printf("Player %s will help nobody.", player.getNickname());
                } else{
                    System.out.printf("Player %s will help %s", player.getNickname(), msg.side);
                    if (msg.side == BattleSide.SIDE_ATTACKER.getId()){
                        bdo.addAttackerHelper(player);
                    }else if (msg.side == BattleSide.SIDE_DEFENDER.getId()){
                        bdo.addDefenderHelper(player);
                    }
                }
            }
        });
    }

    private BattleSide showSelectSideDialogAndGetResult(){
        CustomModalState<BattleSide> cms = new CustomModalState<>(BattleSide.SIDE_NONE);

        ImageObject bg = new ImageObject("DialogBG.png", new Vector2f(490, 285), 300, 150)
                            .setSpace(DrawSpace.SCREEN);

        bg.addChild(new ImageButton("ButtonAttacker.png", 0,0, 100, 150, null)
                        .setSpace(DrawSpace.SCREEN)
                        .setCallback((sender, param)->cms.setResultAndClose(BattleSide.SIDE_ATTACKER))
        );

        bg.addChild(new ImageButton("ButtonDefender.png", 100,0, 100, 150, null)
                .setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->cms.setResultAndClose(BattleSide.SIDE_DEFENDER))
        );

        bg.addChild(new ImageButton("ButtonNone.png", 200,0, 100, 150, null)
                .setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->cms.setResultAndClose(BattleSide.SIDE_NONE))
        );

        cms.addObject(bg);

        (new ModalState(cms)).run();

        return cms.getResult();
    }
}