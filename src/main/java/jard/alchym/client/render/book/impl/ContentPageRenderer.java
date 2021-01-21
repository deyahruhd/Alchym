package jard.alchym.client.render.book.impl;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.api.book.impl.ContentPage;
import jard.alchym.client.render.book.PageRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

/***
 *  ContentPageRenderer
 *  Implementation of {@link PageRenderer} for the {@link ContentPage} class.
 *
 *  Created by jard at 02:17 on January, 03, 2021.
 ***/
@Environment (EnvType.CLIENT)
public class ContentPageRenderer extends PageRenderer <ContentPage> {
    @Override
    protected void render (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side, int bookProgress,
                           TextureManager textures, TextRenderer font, ItemRenderer itemRenderer) {
        renderContent (stack, ((ContentPage) page).content, font);
    }
}
