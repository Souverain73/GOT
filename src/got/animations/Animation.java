package got.animations;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public interface Animation<T> {
    boolean isFinished();
    void pause();
    void resume();
    void restart();
    T getCurrentValue();
    void update();
}
