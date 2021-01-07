package jard.alchym.client.render.book.impl;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.api.book.impl.TitlePage;
import jard.alchym.client.render.book.PageRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

/***
 *  TitlePageRenderer
 *  Implementation of {@link PageRenderer} for the {@link TitlePage} class.
 *
 *  Created by jard at 02:22 on January, 03, 2021.
 ***/
@Environment (EnvType.CLIENT)
public class TitlePageRenderer extends PageRenderer <TitlePage> {
    @Override
    protected void render (MatrixStack stack, BookPage page, AlchymReference.PageInfo.BookSide side, TextureManager textures, TextRenderer font, ItemRenderer itemRenderer) {
        if (side == AlchymReference.PageInfo.BookSide.LEFT)
            return;

        renderContent (stack, ((TitlePage) page).content, textures, font, itemRenderer);
    }
}
