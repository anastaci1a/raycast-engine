package game.elem.box;


// dep

import ana.util.color.Color;

import java.util.ArrayList;
import java.util.List;


// main

public class ColorBox {
    // attr

    public final Color color;

    // static factory

    public static ColorBox ofType(int type) {
        return CBOX_TYPES.get(
            type % CBOX_TYPES.size()
        );
    }

    private static final List<ColorBox> CBOX_TYPES = new ArrayList<>(List.of(
        new ColorBox(Color.RED),
        new ColorBox(Color.ORANGE),
        new ColorBox(Color.YELLOW),
        new ColorBox(Color.GREEN),
        new ColorBox(Color.LIME),
        new ColorBox(Color.AQUAMARINE),
        new ColorBox(Color.CYAN),
        new ColorBox(Color.SKY_BLUE),
        new ColorBox(Color.CMYK_BLUE),
        new ColorBox(Color.MAGENTA),
        new ColorBox(Color.CMYK_MAGENTA),
        new ColorBox(Color.VIOLET),
        new ColorBox(Color.AMARANTH_PURPLE)
    ));

    // constr

    public ColorBox(Color color) {
        this.color = color;
    }
}
