package jard.alchym.client.render.book.impl;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.api.book.impl.ContentPage;
import jard.alchym.api.book.impl.NavigatorPage;
import jard.alchym.client.render.book.PageRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

/***
 *  NavigatorPageRenderer
 *  Implementation of {@link PageRenderer} for the {@link NavigatorPage} class.
 *
 *  Created by jard at 02:18 on January, 03, 2021.
 ***/
public class NavigatorPageRenderer extends PageRenderer <NavigatorPage> {
    @Override
    protected void render (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side, TextureManager textures, TextRenderer font, ItemRenderer itemRenderer) {
        // No op
    }
}
