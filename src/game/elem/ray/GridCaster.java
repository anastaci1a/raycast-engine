package game.elem.ray;


// dep

import ana.util.math.vector.point.DoublePoint;
import ana.util.math.vector.point.IntPoint;

import java.util.function.Function;


// main

public class GridCaster<Box> {
    // const

    public static final double FOV_DEFAULT        = Math.PI * 0.3;
    public static final double PROJ_DIST_DEFAULT  = 3.0; // (2 == box shows as cube)

    public static final double RAY_LENGTH_DEFAULT     = 40.0;
    public static final int    RAY_NODES_MULT_DEFAULT = 5;


    // attr

    private final GridRay[] rays;
    public final int rayCount;

    public final double rayLength;
    public final double projDist; // projection image distance from player

    // constr

    public GridCaster(Function<IntPoint, Box> boxAt, int rayCount) {
        this(
            boxAt,
            FOV_DEFAULT,
            rayCount,
            RAY_NODES_MULT_DEFAULT * (int) Math.round(RAY_LENGTH_DEFAULT),
            RAY_LENGTH_DEFAULT,
            PROJ_DIST_DEFAULT
        );
    }

    public GridCaster(Function<IntPoint, Box> boxAt, double fov, int rayCount, int rayNodes, double rayLength, double projDist) {
        this.rayCount = rayCount;
        this.rayLength = rayLength;
        this.projDist = projDist;

        this.rays = new GridRay[rayCount];
        double th = -fov/2, incr = fov / (rayCount);
        for (int i = 0; i < rayCount; i++) {
            rays[i] = (new GridRay<>(boxAt, th, rayLength, rayNodes, projDist));
            th += incr;
        }
    }

    // update

    public RayReceipt<Box>[] cast(DoublePoint pos, double theta) {
        RayReceipt[] seen = new RayReceipt[rays.length];
        for (int i = 0; i < rays.length; i++) {
            seen[i] = rays[i].castAt(pos, theta);
        }

        return (RayReceipt<Box>[]) seen;
    }
}
