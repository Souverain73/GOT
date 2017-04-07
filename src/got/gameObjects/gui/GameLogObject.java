package got.gameObjects.gui;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.graphics.Font;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Souverain73 on 21.03.2017.
 */
public class GameLogObject extends AbstractGameObject<GameLogObject> {
    @Override protected GameLogObject getThis() {return this;}
    private List<String> log = new ArrayList<>();
    private int currentMessage = 0;
    private int linesCount;
    private List<TextObject> textObjects = new ArrayList<>();
    private Font font = new Font("test");

    public GameLogObject(int width, int height, int fontSize) {
        this.w = width;
        this.h = height;

        addChild(new ImageObject("PlayerPanel.png", new Vector2f(0, 0), width, height).setSpace(space));

        linesCount = height / fontSize;
        IntStream.range(0, linesCount).forEach((i)->{
            TextObject to = new TextObject("").setSpace(space).setPos(new Vector2f(0, i * fontSize));
            textObjects.add(to);
            addChild(to);
        });

        addChild(new ImageButton("buttons/minus.png", width - 25, 0, 25, 25, null).setSpace(space).setCallback((sender, param)->{
            scroll(-1);
        }));

        addChild(new ImageButton("buttons/plus.png", width - 25, height-25, 25, 25, null).setSpace(space).setCallback((sender, param)->{
            scroll(1);
        }));
    }

    public GameLogObject setFont(Font font){
        this.font = font;
        textObjects.stream().forEach(to->to.setFont(font));
        return this;
    }

    public void addMessage(String message){
        if (font.getStringWidth(message) > w){
            log.addAll(font.splitForWidth(message, (int)w));
        }else{
            log.add(message);
        }

        updateContent();
        if (log.size() - currentMessage > linesCount){
            scrollToLast();
        }
    }

    public void scrollToLast(){
        currentMessage = log.size() - linesCount;
        currentMessage = currentMessage < 0 ? 0 : currentMessage;
        updateContent();
    }

    private void scroll(int offset){
        currentMessage += offset;
        currentMessage = currentMessage + linesCount >= log.size() ? log.size() - linesCount : currentMessage;
        currentMessage = currentMessage < 0 ? 0 : currentMessage;
        updateContent();
    }

    private void updateContent(){
        IntStream.range(0, linesCount).forEach(i->{
            int current = i + currentMessage;
            String message = current >= log.size() ? "" : log.get(current);
            textObjects.get(i).setText(message);
        });
    }
}