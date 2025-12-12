package game.elem.map;


// main

import ana.io.cli.Cli;
import ana.util.math.vector.point.IntPoint;
import ana.util.math.vector.IntVector;
import game.elem.box.ColorBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;


// main

public class GridMap<BoxType extends ColorBox> {
    // const

    public static final String DIGIT_EMPTY_DEFAULT = ".";

    // static file processing

    public static <T extends ColorBox> GridMap<T> cliSelect(String mapsDir, Function<Integer, T> boxOfType) {
        String[] loadStrs;
        try (Scanner s = new Scanner(System.in)) {
            loadStrs = Cli.chooseFileInDir(s, mapsDir);
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return parseMapStrs(loadStrs, boxOfType);
    }

    public static <T extends ColorBox> GridMap<T> parseMapStrs(String[] mapStrs, Function<Integer, T> boxOfType) {
        String[] startPosStr = mapStrs[0].split(",");
        IntPoint startPos = new IntVector(
            Integer.parseInt(startPosStr[0]),
            Integer.parseInt(startPosStr[1])
        );

        mapStrs = Arrays.copyOfRange(mapStrs, 1, mapStrs.length);

        ColorBox[][] boxes = new ColorBox[mapStrs[0].length()][mapStrs.length];
        for (int x = 0; x < boxes.length; x++) {
            for (int y = 0; y < boxes[0].length; y++) {
                IntPoint pos = new IntVector(x, y);
                T box = null;

                String digit = String.valueOf(mapStrs[y].charAt(x));
                if (!digit.equals(DIGIT_EMPTY_DEFAULT)) {
                    int boxIdx = Integer.parseInt(digit, 16); // from hex
                    box = boxOfType.apply(boxIdx);
                }

                boxes[pos.x()][pos.y()] = box;
            }
        }

        return new GridMap<>(
            (T[][]) boxes,
            startPos
        );
    }

    // attr

    private final BoxType[][] boxArr;
    public final IntPoint startPos;

    // prot constr

    protected GridMap(BoxType[][] boxArr, IntPoint startPos) {
        this.startPos = startPos;
        this.boxArr = boxArr;
    }

    // get

    public BoxType boxAt(IntPoint p) {
        boolean validX = 0 <= p.x() && p.x() < boxArr.length;
        boolean validY = 0 <= p.y() && p.y() < boxArr[0].length;

        if (validX && validY) return boxArr[p.x()][p.y()];
        return null;
    }
}
