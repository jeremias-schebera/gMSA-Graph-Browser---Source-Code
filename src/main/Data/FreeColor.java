package  main.Data;

import java.awt.Color;
import java.util.Stack;

public class FreeColor {
    private Stack<Color> freeColors;
    private final Color defaultColor = new Color(100,100,100);

    public FreeColor() {
        freeColors = new Stack<>();

        freeColors.push(new Color(55,126,184));
        freeColors.push(new Color(255,224,71));
        freeColors.push(new Color(255,127,0));
        freeColors.push(new Color(166,86,40));
        freeColors.push(new Color(247,129,191));
        freeColors.push(new Color(152,78,163));
        freeColors.push(new Color(77,175,74));
        freeColors.push(new Color(228,26,28));
    }

    public Color getFreeColor() {
        if (freeColors.empty()) {
            return defaultColor;
        } else {
            return freeColors.pop();
        }
    }

    public void releaseColor(Color color) {
        freeColors.push(color);
    }

    public Color getDefaultColor() {
        return defaultColor;
    }
}
