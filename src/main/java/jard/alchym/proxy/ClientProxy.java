package jard.alchym.proxy;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.render.book.PageRenderDispatcher;
import jard.alchym.client.render.model.ChymicalFlaskBakedModel;
import jard.alchym.client.render.model.SpeedloaderBakedModel;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.*;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

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

        // Chymical flask models
        ModelLoadingRegistry.INSTANCE.registerModelProvider ((resourceManager, consumer) -> {
            consumer.accept (new ModelIdentifier (new Identifier (AlchymReference.MODID,
                    String.format ("%s_base", AlchymReference.Items.CHYMICAL_FLASK.getName ())), "inventory"));

            for (AlchymReference.Materials material : AlchymReference.Materials.values ()) {
                if (material.forms.contains (AlchymReference.Materials.Forms.LIQUID))
                    consumer.accept (new ModelIdentifier (new Identifier (AlchymReference.MODID,
                            String.format ("%s_flask_layer", material.getName ())), "inventory"));
            }
        });

        // Speedloader models
        ModelLoadingRegistry.INSTANCE.registerModelProvider ((resourceManager, consumer) -> {
            consumer.accept (new ModelIdentifier (new Identifier (AlchymReference.MODID,
                    String.format ("empty_%s", AlchymReference.Items.SPEEDLOADER.getName ())), "inventory"));

            consumer.accept (new ModelIdentifier (new Identifier (AlchymReference.MODID,
                    String.format ("%s_base", AlchymReference.Items.SPEEDLOADER.getName ())), "inventory"));
        });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider (rm -> (modelIdentifier, modelProviderContext) -> {
            if (modelIdentifier.getNamespace ().equals (AlchymReference.MODID)) {
                // Chymical flask
                if (modelIdentifier.getPath ().equals (AlchymReference.Items.CHYMICAL_FLASK.getName ())) {
                    return new ChymicalFlaskBakedModel ();
                } else if (modelIdentifier.getPath ().equals (AlchymReference.Items.SPEEDLOADER.getName ())) {
                    return new SpeedloaderBakedModel ();
                }
            }

            return null;
        });
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
