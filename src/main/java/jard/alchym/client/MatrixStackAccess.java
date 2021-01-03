package jard.alchym.client;

import net.minecraft.util.math.Matrix4f;

/***
 *  MatrixStackAccess
 *  Interface hook into {@link net.minecraft.client.util.math.MatrixStack} to provide additional
 *  methods for manipulating the inner matrix stack.
 *
 *  Created by jard at 19:21 on January, 02, 2021.
 ***/
public interface MatrixStackAccess {
    void multiply (Matrix4f matrix);
}
