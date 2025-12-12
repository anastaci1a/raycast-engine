package game;


// dep

import ana.fx.app.AppX;
import ana.util.math.vector.point.DoublePoint;

import game.elem.BoxedPlayer;
import game.elem.RayVis;
import game.elem.box.ColorBox;
import game.elem.map.GridMap;


// main

public class MainApp extends AppX {
    // const

    public static final double PLAYER_MOVE_SPEED  = 0.08;
    public static final double PLAYER_SENSITIVITY = 0.015;

    // game elements

    private final GridMap<ColorBox> gridMap;
    private final BoxedPlayer<ColorBox> player;
    private final RayVis<ColorBox> rayVis;

    // constr

    public MainApp(AppX.Init init) {
        super(init);

        // cli

        gridMap = GridMap.cliSelect(
            Config.DIR_MAPS,
            ColorBox::ofType
        );

        // init

        DoublePoint startPos = gridMap.startPos.toDoubleVector();

        player = new BoxedPlayer<>(input, gridMap::boxAt, startPos, PLAYER_MOVE_SPEED, PLAYER_SENSITIVITY);
        rayVis = new RayVis<>(canvas, gridMap::boxAt);
    }

    // main

    @Override
    public void eventLoop() {
        player.manageMovement();
        rayVis.display(player.getPos(), player.getTheta());
    }
}
