package jard.alchym.client.helper;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;

/***
 *  RenderHelper
 *  Contains various helper methods for graphical rendering and matrix manipulation using linear algebra.
 *
 *  Created by jard at 01:50 on January, 03, 2021.
 ***/
public class RenderHelper {
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
