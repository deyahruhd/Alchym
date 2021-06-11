package jard.alchym.helper;

import jard.alchym.client.helper.RenderHelper;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
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

    public static boolean inRange (float val, float min, float max) {
        return val >= min && val <= max;
    }

    public static float quinticSpline (float x, float xMid, float [][] coeffs) {
        if (x <= 0.f || x >= 1.f)
            return 0.f;

        float xPow = 1.f, polynomial = 0.f;
        int index = 0;

        if (x >= xMid)
            index = 1;

        for (int i = 0; i < 6; ++ i) {
            polynomial += (xPow * coeffs [index][i]);
            xPow *= x;
        }

        return polynomial;
    }
}
