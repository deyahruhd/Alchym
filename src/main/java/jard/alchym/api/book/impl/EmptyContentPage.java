package jard.alchym.api.book.impl;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.gui.screen.GuidebookScreen;
import jard.alchym.client.gui.widget.AbstractGuidebookWidget;
import jard.alchym.client.gui.widget.GuidebookPageTurnWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.List;

/***
 *  EmptyContentPage
 *  An empty {@link ContentPage}. Its primary purpose is to serve as a forward link directly to a {@link NavigatorPage},
 *  and is the cleanest implementation for this task given the structuring of the book page API.
 *
 *  Created by jard at 00:26 on January, 15, 2021.
 ***/
public final class EmptyContentPage extends ContentPage {
    public EmptyContentPage () throws IllegalArgumentException {
        super (new Identifier (AlchymReference.MODID, "empty"), null, new LiteralText [] { new LiteralText ("")}, -1);
    }

    @Override
    @Environment (EnvType.CLIENT)
    public void populateWidgets (GuidebookScreen book, List<AbstractGuidebookWidget> widgets, AlchymReference.PageInfo.BookSide side) {
        GuidebookPageTurnWidget turnArrow = new GuidebookPageTurnWidget (
                book,
                physicalNext (), // Asserted to be a NavigatorPage
                GuidebookPageTurnWidget.ArrowDirection.RETURN,
                side == AlchymReference.PageInfo.BookSide.LEFT ? 2 : AlchymReference.PageInfo.PAGE_WIDTH - 16 - 2,
                AlchymReference.PageInfo.PAGE_HEIGHT - 9 - 7, 16, 9, LiteralText.EMPTY);

        widgets.add (turnArrow);
    }
}
