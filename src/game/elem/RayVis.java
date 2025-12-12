package game.elem;


// dep

import ana.fx.canvas.Canvas;
import ana.fx.canvas.draw.DrawMode;
import ana.util.color.Color;
import ana.util.math.MathExt;
import ana.util.math.vector.DoubleVector;
import ana.util.math.vector.point.DoublePoint;
import ana.util.math.vector.point.IntPoint;

import game.elem.box.ColorBox;
import game.elem.ray.GridCaster;
import game.elem.ray.RayReceipt;

import java.util.function.Function;


// main

public class RayVis<Box extends ColorBox> {
    // const

    public static final double HORIZON_LINE_RATIO = 0.6;

    public static final double LIGHT_DIST_RATIO = 0.2;
    public static final double FADE_DIST_RATIO  = 0.3;

    public static final double MAX_HEIGHT_MULT_DEFAULT = 1.0;

    public static final Color BG_COLOR_TOP_DEFAULT    = Color.SKY_BLUE; // Color.hex("#5f6975");
    public static final Color BG_COLOR_BOTTOM_DEFAULT = Color.hex("#242230");
    public static final Color HORIZON_FADE_COL = Color.lerp(0.5, BG_COLOR_TOP_DEFAULT, Color.BLACK);

    // attr

    private final Canvas c;
    private final GridCaster<Box> rayCaster;

    private final double visLineMaxHeight;
    private final double horizonY;

    private final Color bgColTop, bgColBot, fadeCol;


    // constr

    public RayVis(Canvas c, Function<IntPoint, Box> boxAt) {
        this(c, boxAt, MAX_HEIGHT_MULT_DEFAULT, BG_COLOR_TOP_DEFAULT, BG_COLOR_BOTTOM_DEFAULT);
    }

    public RayVis(
        Canvas c,
        Function<IntPoint, Box> boxAt,
        double maxHeightMult,
        Color bgColTop,
        Color bgColBot
    ) {
        this.c = c;
        this.rayCaster = new GridCaster<>(boxAt, c.size.x());

        visLineMaxHeight = maxHeightMult * c.size.y();

        this.bgColTop = bgColTop;
        this.bgColBot = bgColBot;

        this.fadeCol = HORIZON_FADE_COL; // Color.lerp(0.5, bgColBot, bgColTop);

        this.horizonY = c.size.y() * HORIZON_LINE_RATIO;
    }

    public void display(DoublePoint castPos, double theta) {
        RayReceipt<Box>[] receipts = rayCaster.cast(castPos, theta);

        c.draw.noStroke();

        c.draw.setMode(DrawMode.Rect.CORNERS);
        c.draw.setFill(bgColTop);
        c.draw.rect(0, 0, c.size.x(), horizonY);
        c.draw.setFill(bgColBot);
        c.draw.rect(0, horizonY, c.size.x(), c.size.y());

        c.draw.setStrokeWeight(1);

        for (int x = 0; x < receipts.length; x++) {
            RayReceipt<Box> receipt = receipts[x];

            if (receipt.saw) {
                VisLine line = calcVisLine(receipt, x);
                drawVisLine(line);
            }
        }
    }

    // draw

    private void drawVisLine(VisLine line) {
        // bottom
        c.draw.setStroke(line.colBottom());
        c.draw.line(
            line.pointMid(),
            line.pointBot()
        );

        // top
        c.draw.setStroke(line.colTop());
        c.draw.line(
            line.pointTop(),
            line.pointMid()
        );
    }

    // box calc

    private VisLine calcVisLine(RayReceipt<Box> receipt, int x) {
        Color[] cols = calcCols(receipt);
        double[] heights = calcLineHeights(receipt.projHeight);
        DoublePoint[] pts = calcLinePoints(heights, x);

        return new VisLine(
            true,
            pts[0], pts[1], pts[2],
            cols[0], cols[1]
        );
    }

    private double[] calcLineHeights(double projHeight) {
        double lenFull = projHeight * visLineMaxHeight * MAX_HEIGHT_MULT_DEFAULT;

        double lenTop = lenFull * HORIZON_LINE_RATIO;
        double lenBot = lenFull * (1 - HORIZON_LINE_RATIO);

        return new double[] {
            lenTop,
            lenBot
        };
    }

    private DoublePoint[] calcLinePoints(double[] sizes, int x) {
        DoublePoint ptMid = new DoubleVector(x, horizonY);

        DoublePoint ptTop = new DoubleVector(
             x, horizonY - sizes[0]
        );

        DoublePoint posBot = new DoubleVector(
            x, horizonY + sizes[1]
        );

        return new DoublePoint[] { ptTop, ptMid, posBot };
    }

    private Color[] calcCols(RayReceipt<Box> receipt) {
        double ratio    = receipt.distRatio;
        double lightVal = LIGHT_DIST_RATIO;
        double fadeVal  = 1 - FADE_DIST_RATIO;

        Color midCol = Color.lerp(
            0.5, receipt.box.color, fadeCol
        );

        Color col = receipt.box.color;
        Color colTop = midCol, colBot = midCol;


        if (ratio <= lightVal) {
            ratio = MathExt.map(ratio, 0, lightVal, 0, 1);

            colTop = Color.lerp(ratio, col, midCol);
            colBot = Color.lerp(ratio, col, midCol);
        }

        else if (ratio >= fadeVal) {
            ratio = MathExt.map(ratio, fadeVal, 1, 0, 1);

            colTop = Color.lerp(ratio, midCol, bgColBot);
            colBot = Color.lerp(ratio, midCol, bgColTop);
        }

        return new Color[] { colTop, colBot };
    }

    // inner

    private record VisLine(
        boolean draw,
        DoublePoint pointTop,
        DoublePoint pointMid,
        DoublePoint pointBot,
        Color colBottom,
        Color colTop
    ) {}
}
