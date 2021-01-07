package jard.alchym.client.render.book;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

/***
 *  PageRenderer
 *  Generic, abstract render handler for a BookPage.
 *
 *  This class importantly declares the render method that is called by a {@link PageRenderDispatcher} when
 *  a guidebook page needs to be drawn.
 *
 *  Created by jard at 01:56 on January, 03, 2021.
 ***/

@Environment (EnvType.CLIENT)
public abstract class PageRenderer <T extends BookPage> {
    protected abstract void render (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side, TextureManager textures, TextRenderer font, ItemRenderer itemRenderer);

    public final void renderContent (MatrixStack stack, String [] content, TextureManager textures, TextRenderer font, ItemRenderer itemRenderer) {
        int lineNumber = 0;

        for (String text : content) {
            renderBodyText (stack, text, lineNumber, font);

            lineNumber ++;
        }
    }

    public final void renderBodyText (MatrixStack stack, String text, int line, TextRenderer font) {
        font.draw (stack, text, 0.f, 12.f * line, AlchymReference.PageInfo.BODY_TEXT_COLOR);
    }
}
