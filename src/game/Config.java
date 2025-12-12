package game;


// dep

import ana.fx.window.WindowConfig;
import ana.util.color.Color;
import ana.util.math.vector.point.DoublePoint;
import ana.util.math.vector.DoubleVector;


// main

public final class Config {
    // no inst

    private Config() {}

    // window

    public static final String WINDOW_TITLE = "The Best Game";
    public static final Color  WINDOW_BG_COLOR = Color.BLACK;

    public static final double WINDOW_SIZE_RATIO = 16.0 / 9.0;
    public static final double WINDOW_SIZE_MULT  = 0.7;

    public static final DoublePoint WINDOW_POS_ADJUST = new DoubleVector(0, -0.2); // slightly upwards

    public static final WindowConfig WINDOW_CONFIG = new WindowConfig(
        WINDOW_TITLE,
        WINDOW_SIZE_RATIO,
        WINDOW_SIZE_MULT,
        WINDOW_POS_ADJUST,
        WINDOW_BG_COLOR
    );

    // file

    public static final String DIR_MAPS = "./res/maps";
}
