package jard.alchym.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/***
 *  ItemStackMixin
 *  Mixin adding extra functionality to {@link ItemStack} serialization that supports stack counts larger than 255.
 *
 *  Created by jard at 4:50 PM on February 03, 2019.
 ***/
@Mixin (ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    private int count;
    @Shadow
    public Item getItem () { return null; }
    @Shadow
    private CompoundTag tag;

    @Inject (method = "toTag", at = @At ("HEAD"), cancellable = true)
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (count > Byte.MAX_VALUE) {
            // Write the amount as an integer instead of a byte
            Identifier identifier = Registry.ITEM.getId (getItem ());
            tag.putString("id", identifier == null ? "minecraft:air" : identifier.toString ());

            tag.putByte("Count", (byte) this.count);
            tag.putInt("FullCount", this.count);
            if (this.tag != null) {
                tag.put("tag", this.tag);
            }

            info.cancel ();

            info.setReturnValue (tag);
        }
    }

    @Inject(method = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void onConstructor (CompoundTag tag, CallbackInfo info) {
        if (tag.containsKey ("FullCount"))
            count = tag.getInt ("FullCount");
    }
}
