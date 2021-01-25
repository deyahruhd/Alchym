package jard.alchym.proxy;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.render.book.PageRenderDispatcher;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

/***
 *  ClientProxy
 *  Client-sided proxy.
 *
 *  Created by jard at 7:02 PM on March 23, 2019.
 ***/
public class ClientProxy extends Proxy {
    private PageRenderDispatcher pageRenderDispatcher = null;

    @Override
    public void onInitialize () {
        pageRenderDispatcher = new PageRenderDispatcher ();

        BlockRenderLayerMap.INSTANCE.putBlock (Alchym.content ().blocks.copperCrucible, RenderLayer.getCutout ());
        BlockRenderLayerMap.INSTANCE.putBlock (Alchym.content ().blocks.alembic, RenderLayer.getCutout ());
    }

    @Override
    public void registerPacket (AlchymReference.Packets packet, PacketConsumer action) {
        ClientSidePacketRegistry.INSTANCE.register (packet.id,
                (packetContext, packetByteBuf) -> {
                    final PacketByteBuf data = new PacketByteBuf (packetByteBuf.copy ());
                    packetContext.getTaskQueue ().execute (() -> action.accept (packetContext, data));
                });
    }

    @Override
    public void renderPage (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side, int bookProgress) {
        assert pageRenderDispatcher != null;

        if (page == null)
            return;

        pageRenderDispatcher.render (stack, page, side, bookProgress);
    }
}
