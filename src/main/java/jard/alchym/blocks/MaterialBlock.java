package jard.alchym.blocks;

import jard.alchym.AlchymReference;
import net.minecraft.block.Block;

/***
 *  MaterialBlock.java
 *  A generic block item which is instantiated with a certain material and form.
 *
 *  Created by jard at 12:23 PM on December 28, 2018.
 ***/
public class MaterialBlock extends Block {
    public final AlchymReference.Materials material;
    public final AlchymReference.Materials.Forms form;

    public MaterialBlock (Block.Settings settings, AlchymReference.Materials material, AlchymReference.Materials.Forms form) {
        super (settings);

        this.material = material;
        this.form = form;
    }
}
