package game.elem.ray;


// dep

import ana.util.math.Axis;
import ana.util.math.vector.point.DoublePoint;
import ana.util.math.vector.point.IntPoint;
import ana.util.math.vector.DoubleVector;

import java.util.function.Function;


// main

public class GridRay<Box> {
    // attr

    private final Function<IntPoint, Box> boxAt;
    private final DoublePoint fullRay;
    private final int nodeAmount;

    private final double adjustedLength, projDist;


    // constr

    public GridRay(Function<IntPoint, Box> boxAt, double theta, double length, int nodeAmount, double projDist) {
        this.boxAt = boxAt;
        this.nodeAmount = nodeAmount;

        this.adjustedLength = length * Math.cos(theta);
        this.projDist = projDist;

        this.fullRay = new DoubleVector(1, 0).rotate(Axis.Z, theta).mult(length);
    }

    // update

    RayReceipt<Box> castAt(DoublePoint pos, double atTheta) {
        DoublePoint fullRayDir = fullRay.clone().rotate(Axis.Z, atTheta);
        DoubleVector rootPos = pos.clone();

        for (int i = 0; i < nodeAmount; i++) {
            DoublePoint nodeD = rootPos.clone().add(
                fullRayDir.clone().mult((double) i / nodeAmount)
            );
            IntPoint node = nodeD.toIntVector();

            Box seenBox = boxAt.apply(node);
            if (seenBox != null) {
                double ratio = (double) i / (nodeAmount - 1);

                double dist = ratio * adjustedLength;
                double projHeight = projDist / dist;

                return new RayReceipt<>(
                    seenBox,
                    dist, ratio,
                    projHeight
                );
            }
        }

        return new RayReceipt<>();
    }
}
