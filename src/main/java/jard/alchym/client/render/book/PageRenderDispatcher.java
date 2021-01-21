package jard.alchym.client.render.book;

import com.google.common.collect.Maps;
import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.api.book.impl.ContentPage;
import jard.alchym.api.book.impl.EmptyContentPage;
import jard.alchym.api.book.impl.NavigatorPage;
import jard.alchym.api.book.impl.TitlePage;
import jard.alchym.client.render.book.impl.ContentPageRenderer;
import jard.alchym.client.render.book.impl.NavigatorPageRenderer;
import jard.alchym.client.render.book.impl.TitlePageRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Map;

/***
 *  PageRenderDispatcher
 *  Render call dispatcher for {@link BookPage}s and their corresponding {@link PageRenderDispatcher}s.
 *
 *  Created by jard at 02:01 on January, 03, 2021.
 ***/

@Environment (EnvType.CLIENT)
public class PageRenderDispatcher {
    private final Map<Class <? extends BookPage>, PageRenderer<? extends BookPage>> renderers = Maps.newHashMap();

    public PageRenderDispatcher () {
        renderers.put (ContentPage.class, new ContentPageRenderer ());
        renderers.put (EmptyContentPage.class, new ContentPageRenderer ());
        renderers.put (NavigatorPage.class, new NavigatorPageRenderer ());
        renderers.put (TitlePage.class, new TitlePageRenderer ());
    }

    public void render (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side, int bookProgress) {
        PageRenderer<? extends BookPage> renderer = renderers.get (page.getClass ());

        renderer.render (stack, page, side, bookProgress,
                                            MinecraftClient.getInstance ().getTextureManager (),
                                            MinecraftClient.getInstance ().textRenderer,
                                            MinecraftClient.getInstance ().getItemRenderer ());
    }
}
