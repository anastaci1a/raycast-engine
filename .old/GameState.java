package game;


// dep

import ana.util.color.Color;

import ana.util.math.Axis;
import ana.util.math.point.DoublePoint;
import ana.util.math.point.IntPoint;
import ana.util.math.vector.IntVector;
import game.util.graphics.draw.ShapeMode;
import game.util.input.Input;
import game.util.input.mouse.Mouse;
import game.util.graphics.canvas.PixelCanvas;
import game.util.graphics.WindowConfig;

import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;


// main

public class GameState {
    // attr

    private final WindowConfig cfg;
    private final Application  app;
    private final PixelCanvas  c;
    private final Input in;

    private DoublePoint p1;

    private List<DoublePoint> points;

    // constr

    public GameState(WindowConfig cfg, Application app, PixelCanvas canvas, Input input) {
        this.cfg = cfg;
        this.app = app;
        this.c = canvas;
        this.in = input;

        points = new ArrayList<>();
    }

    // main

    public void eventLoop() {
        c.draw.background(cfg.bgColor);

        // ...

        c.draw.translate(
            100 * Math.sin(c.getFrameCount() / 100.0), 0
        );

        c.draw.rotate(
            Axis.Z,
            (Math.PI / 1000) * (2 * Math.cos(c.getFrameCount() / 100.0) - 1)
        );

        if (in.get(Mouse.LEFT).tapped()) {
            points.add(in.getMouse());
        }

        c.draw.setMode(ShapeMode.Rect.CENTER);
        c.draw.setFill(Color.hsv(c.getFrameCount() % 360.0, 50, 100));
        c.draw.setStrokeWeight(10);

        for (DoublePoint p : points) c.draw.rect(p.x(), p.y(), 100, 100);

        // ...
    }
}
