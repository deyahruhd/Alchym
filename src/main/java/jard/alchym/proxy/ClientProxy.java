package jard.alchym.proxy;

import jard.alchym.AlchymReference;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

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
    public void registerPacket (AlchymReference.Packets packet) {
        ClientSidePacketRegistry.INSTANCE.register (packet.id,
                (packetContext, packetByteBuf) -> packetContext.getTaskQueue ().execute (
                        () -> packet.action.accept (packetContext, packetByteBuf)));
    }
}
