package jard.alchym.helper;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
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

    public static Matrix4f yShear (float angle) {
        angle += 45.f;

        float oneOverAlpha = (float) Math.tan (angle * 0.017453292F);

        Matrix4f first = new Matrix4f (Vector3f.NEGATIVE_Z.getDegreesQuaternion (- angle));
        Matrix4f second = Matrix4f.scale (1.f / oneOverAlpha, oneOverAlpha, 1.f);
        Matrix4f third = new Matrix4f (Vector3f.NEGATIVE_Z.getDegreesQuaternion (90.f - angle));

        first.multiply (second);
        first.multiply (third);
        first.transpose ();

        return first;
    }

    public static final Matrix4f IDENTITY_MATRIX = new Matrix4f ();
    static {
        IDENTITY_MATRIX.loadIdentity ();
    }
}
