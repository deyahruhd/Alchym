package jard.alchym.items;

import jard.alchym.AlchymReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/***
 *  MaterialItem.java
 *  A generic material item which is instantiated with a certain material and form.
 *
 *  Created by jard at 1:36 PM on December 21, 2018.
 ***/
public class MaterialItem extends Item {
    public final AlchymReference.Materials material;
    public final AlchymReference.Materials.Forms form;

    public MaterialItem (Settings settings, AlchymReference.Materials material, AlchymReference.Materials.Forms form) {
        super (settings);

        this.material = material;
        this.form = form;
    }

    @Environment (EnvType.CLIENT)
    public boolean hasEnchantmentGlow (ItemStack itemStack) {
        return material == AlchymReference.Materials.ALCHYMIC_GOLD;
    }
}
