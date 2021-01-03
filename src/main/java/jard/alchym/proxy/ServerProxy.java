package jard.alchym.proxy;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.util.math.MatrixStack;

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

    @Override
    public void renderPage (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side) {
        // No-op
    }
}
