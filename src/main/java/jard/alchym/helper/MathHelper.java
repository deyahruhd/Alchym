package jard.alchym.helper;

import net.minecraft.util.math.Vec3d;

public class MathHelper {
    public static int rectify (int i) {
        return Math.max (0, i);
    }

    public static Vec3d lerp (Vec3d p, Vec3d n, double factor) {
        return p.add (n.subtract (p).multiply (factor));
    }

    public static boolean implies (boolean a, boolean b) {
        return ! (a && ! b);
    }
}
