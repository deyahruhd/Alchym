package jard.alchym.client.render.book;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.helper.BookHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

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

    public final void renderContent (MatrixStack stack, LiteralText [] content, TextureManager textures, TextRenderer font, ItemRenderer itemRenderer) {
        int lineNumber = 0;

        for (LiteralText text : content) {
            renderText (stack, text, lineNumber, font);

            lineNumber ++;
        }
    }

    public final void renderText (MatrixStack stack, LiteralText text, int line, TextRenderer font) {
        font.draw (stack, text, 0.f, 12.f * line, 0xff08f8e1);
    }
}
