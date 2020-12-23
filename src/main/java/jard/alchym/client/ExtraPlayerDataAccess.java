package jard.alchym.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Vec3d;

public interface ExtraPlayerDataAccess {
    Vec3d getPrevVel ();
    ModelPart getCloak ();
}
