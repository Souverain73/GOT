package got.gameObjects;

import got.graphics.DrawSpace;
import got.graphics.text.Font;
import got.graphics.text.FontTrueType;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by КизиловМЮ on 24.07.2017.
 */
public class TextBoxObject extends AbstractGameObject<TextBoxObject> {
    private final Align verticalAlign;
    private final Font font;
    private final int SPACING = 2;
    private List<TextObject> lines = new ArrayList<>();
    private String text;

    @Override protected TextBoxObject getThis() {return this;}
    public enum Align{TOP, CENTER, BOTTOM}

    public TextBoxObject(Font font, float w, float h, Align verticalAlign, String text){
        this.w = w;
        this.h = h;
        this.verticalAlign = verticalAlign;
        this.font = font;

        int linesCount = (int) (h / font.getSize());
        for (int i=0; i<linesCount; i++){
            TextObject to = new TextObject(font, "");
            to.setPos(new Vector2f(0, i*(font.getSize() + SPACING)));
            lines.add(to);
            addChild(to);
        }

        setText(text);
        setSpace(DrawSpace.SCREEN);
    }

    public TextBoxObject(float w, float h){
        this(new FontTrueType("calibri", 16), w, h, Align.CENTER, "");
    }

    public TextBoxObject setText(String text) {
        this.text = text;
        List<String> textLines = font.splitForWidth(text, (int) w);
        for (int i=0; i <lines.size(); i++){
            lines.get(i).setText(textLines.size()>i ? textLines.get(i) : "");
        }
        return this;
    }
}
