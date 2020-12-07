package jard.alchym.proxy;

import jard.alchym.AlchymReference;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import java.util.stream.Stream;

/***
 *  ServerProxy
 *  Server-sided proxy.
 *
 *  Created by jard at 9:30 PM on March 23, 2019.
 ***/
public class ServerProxy extends Proxy {
    @Override
    public void onInitialize () {
    }

    @Override
    public void registerPacket (AlchymReference.Packets packet, PacketConsumer action) {
        // No-op
    }
}
