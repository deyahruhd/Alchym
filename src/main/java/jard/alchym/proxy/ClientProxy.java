package jard.alchym.proxy;

import jard.alchym.AlchymReference;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.minecraft.network.PacketByteBuf;

/***
 *  ClientProxy
 *  Client-sided proxy.
 *
 *  Created by jard at 7:02 PM on March 23, 2019.
 ***/
public class ClientProxy extends Proxy {
    @Override
    public void onInitialize () {
    }

    @Override
    public void registerPacket (AlchymReference.Packets packet, PacketConsumer action) {
        ClientSidePacketRegistry.INSTANCE.register (packet.id,
                (packetContext, packetByteBuf) -> {
                    final PacketByteBuf data = new PacketByteBuf (packetByteBuf.copy ());
                    packetContext.getTaskQueue ().execute (() -> action.accept (packetContext, data));
                });
    }
}
