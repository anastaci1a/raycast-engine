package game.elem.ray;


// main

public final class RayReceipt<Box> { // not using a record for generics
    public final boolean saw;
    public final Box box;
    public final double dist, distRatio, projHeight;

    public RayReceipt(Box box, double dist, double distRatio, double projHeight) {
        this.saw = true;
        this.box = box;
        this.dist = dist;
        this.distRatio = distRatio;
        this.projHeight = projHeight;
    }

    public RayReceipt() {
        this.saw = false;
        this.box = null;
        this.dist = 0;
        this.distRatio = 0;
        this.projHeight = 0;
    }
}
