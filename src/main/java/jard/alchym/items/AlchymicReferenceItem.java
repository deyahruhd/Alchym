package jard.alchym.items;

import io.netty.buffer.Unpooled;
import jard.alchym.AlchymReference;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AlchymicReferenceItem extends Item {
    public AlchymicReferenceItem (Settings settings) { super (settings); }

    @Override
    public TypedActionResult<ItemStack> use (World world, PlayerEntity player, Hand hand) {
        if (! world.isClient) {
            ItemStack book = player.getStackInHand (hand);

            if (! hasTag (book)) {
                CompoundTag titleTag = new CompoundTag ();
                titleTag.putString ("CurrentPage", "alchym:title");

                book.setTag (titleTag);
            }

            PacketByteBuf data = new PacketByteBuf (Unpooled.buffer());

            data.writeIdentifier (new Identifier (book.getTag ().getString ("CurrentPage")));

            ServerSidePacketRegistry.INSTANCE.sendToPlayer (player, AlchymReference.Packets.OPEN_GUIDEBOOK.id, data);

            // TODO: Play open sound
        }

        return TypedActionResult.success (player.getStackInHand (hand));
    }

    public static final boolean hasTag (ItemStack stack) {
        return stack.hasTag () && stack.getTag ().contains ("CurrentPage");
    }
}
