package jard.alchym.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Matrix4f;

/***
 *  CustomUseAnimItem
 *  TODO: Write a description for this file.
 *
 *  Created by jard at 01:04 on June, 07, 2021.
 ***/
public interface CustomAttackItem {
    Matrix4f getAnimMatrix (ItemStack stack, Arm arm, float progress);

    int getSwingDuration (ItemStack stack);

    boolean hasAttackCooldown (ItemStack stack);
}
