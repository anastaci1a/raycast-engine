package game.elem;


// dep

import ana.io.device.keyboard.Key;
import ana.io.device.DeviceInput;
import ana.util.math.Axis;
import ana.util.math.vector.point.DoublePoint;
import ana.util.math.vector.point.IntPoint;
import ana.util.math.vector.DoubleVector;
import ana.util.math.vector.IntVector;


import java.util.function.Function;


// main

public final class BoxedPlayer<Box> {
    // const

    public static double SPRINT_MULT = 2.0;
    public static double SNEAK_MULT  = 0.5;

    // attr

    private final DeviceInput in;
    private final Function<IntPoint, Box> boxAt;

    private final double moveSpeed, sensitivity;

    private double dirTheta;
    private final DoubleVector pos;

    // constr

    public BoxedPlayer(DeviceInput in, Function<IntPoint, Box> boxAt, DoublePoint startPos, double moveSpeed, double sensitivity) {
        this.in = in;
        this.boxAt = boxAt;

        this.dirTheta = 0;
        this.pos = startPos.clone();

        this.moveSpeed = moveSpeed;
        this.sensitivity = sensitivity;
    }

    // get

    public DoublePoint getPos() {
        return pos.toImmut();
    }

    public double getTheta() {
        return dirTheta;
    }

    // movement

    public void manageMovement() {
        double deltaTheta = getDeltaTheta() * sensitivity;
        dirTheta = ((2 * Math.PI) + (dirTheta + deltaTheta)) % (2 * Math.PI);

        DoublePoint vel = getMoveDir().clone().mult(moveSpeed);

        DoublePoint n = collisionNormal(vel);

        double dot = vel.x() * n.x() + vel.y() * n.y();  // component along normal

        if (dot < 0.0) { // moving *into* the surface
            // remove the normal component → slide along the wall
            DoubleVector v = new DoubleVector(vel.x(), vel.y());
            v.add(new DoubleVector(-dot * n.x(), -dot * n.y()));
            vel = v;
        }

        pos.add(vel);
    }

    // input

    private DoublePoint getMoveDir() {
        DoubleVector vel = new DoubleVector(0, 0);

        if (in.get(Key.W).down()) vel.add( 1, 0);
        if (in.get(Key.A).down()) vel.add( 0,-1);
        if (in.get(Key.S).down()) vel.add(-1, 0);
        if (in.get(Key.D).down()) vel.add( 0, 1);

        if (vel.magSq() == 0) return vel;
        vel.norm().rotate(Axis.Z, dirTheta);

        if (in.get(Key.CONTROL).down()) vel.mult(SPRINT_MULT);
        if (in.get(Key.SHIFT  ).down()) vel.mult( SNEAK_MULT);

        return vel;
    }

    private double getDeltaTheta() {
        double vel = 0;

        if (in.get(Key.LEFT ).down()) vel -= 1;
        if (in.get(Key.RIGHT).down()) vel += 1;

        return vel;
    }

    // collision

    DoublePoint collisionNormal(DoublePoint vel) {
        // Current position and predicted position with full velocity
        DoubleVector startPos = pos.clone();         // field, already a DoubleVector
        DoubleVector newPos   = startPos.clone();
        newPos.add(vel);                             // pos + vel

        // Center of the box we're now inside (if any)
        IntVector cell = newPos.toIntVector();       // center at integer coords
        if (boxAt.apply(cell) == null) {
            return new DoubleVector(0.0, 0.0); // no collision → no normal
        }

        // Position relative to box center (remember: edges at ±0.5)
        double dx = newPos.x() - cell.x();                // in (-0.5, 0.5) if inside
        double dy = newPos.y() - cell.y();

        // How deep we are inside each axis
        double overlapX = 0.5 - Math.abs(dx);
        double overlapY = 0.5 - Math.abs(dy);

        // Candidate normals, in priority order:
        //  - primary: face we hit first
        //  - secondary: the other axis (for corner/corridor cases)
        DoubleVector nPrimary, nSecondary;
        if (overlapX < overlapY) {
            // hit left/right face
            nPrimary   = new DoubleVector(Math.signum(dx), 0.0);
            nSecondary = new DoubleVector(0.0, Math.signum(dy));
        } else {
            // hit top/bottom face
            nPrimary   = new DoubleVector(0.0, Math.signum(dy));
            nSecondary = new DoubleVector(Math.signum(dx), 0.0);
        }

        DoubleVector[] candidates = { nPrimary, nSecondary };

        for (DoubleVector n : candidates) {
            if (n.x() == 0.0 && n.y() == 0.0) continue;

            // Component of velocity along this normal
            double dot = vel.x() * n.x() + vel.y() * n.y();
            if (dot >= 0.0) {
                // Not actually moving *into* this face
                continue;
            }

            // Velocity if we slide along this surface:
            //   v_slide = v - (v·n) n
            DoubleVector slideVel = new DoubleVector(
                vel.x() - dot * n.x(),
                vel.y() - dot * n.y()
            );

            // Where we would end this frame *after* sliding
            DoubleVector slidePos = (DoubleVector) startPos.clone();
            slidePos.add(slideVel);

            IntVector slideCell = slidePos.toIntVector();
            boolean slideHitsBox = boxAt.apply(slideCell) != null;

            // <- THIS is the "air adjacent" check:
            // only accept this normal if sliding ends in empty space
            if (!slideHitsBox) {
                return n;
            }
        }

        // If we get here, both candidate slide directions end inside a box
        // (true corner or wedged between boxes). Just stop completely:
        double len = Math.hypot(vel.x(), vel.y());
        if (len == 0.0) {
            return new DoubleVector(0.0, 0.0);
        }

        // normal opposite motion, so projection v - (v·n)n will zero v
        return new DoubleVector(-vel.x() / len, -vel.y() / len);
    }
}
