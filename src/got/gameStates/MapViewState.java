package got.gameStates;

import got.InputManager;
import got.gameObjects.GameMapObject;
import got.gameObjects.MapPartObject;
import got.interfaces.IClickListener;
import got.utils.LoaderParams;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public class MapViewState extends AbstractGameState implements IClickListener{
    private static final String name = "MainState";
    private static final String MAP_FILE = "data/map.xml";

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameMapObject map = new GameMapObject();
        map.Debug();
        map.init(new LoaderParams(new String[]{"filename", MAP_FILE}));

        addObject(map);
    }

    @Override
    public void click(InputManager.ClickEvent event) {
        if (event.getTarget() instanceof MapPartObject) {
            MapPartObject region = (MapPartObject) event.getTarget();
        }
    }
}
