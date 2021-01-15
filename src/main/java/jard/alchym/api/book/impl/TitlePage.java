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
 *  TitlePage
 *  Subclass of {@link ContentPage}. The first, immediately accessible page of the Alchymic Reference.
 *
 *  Created by jard at 14:52 on December, 23, 2020.
 ***/
public class TitlePage extends ContentPage {
    public TitlePage (Identifier id, BookPage main, LiteralText[] content) throws IllegalArgumentException {
        super (id, main, content);

        if (! (main instanceof NavigatorPage))
            throw new IllegalArgumentException ("Forward-link passed to TitlePage must be a NavigatorPage");
    }

    @Override
    public BookPage physicalNext () {
        // The TitlePage only occupies the right side of the book
        return this;
    }

    @Override
    @Environment (EnvType.CLIENT)
    public void populateWidgets (GuidebookScreen book, List<AbstractGuidebookWidget> widgets, AlchymReference.PageInfo.BookSide side) {
        if (side == AlchymReference.PageInfo.BookSide.RIGHT) {
            GuidebookPageTurnWidget turnArrow = new GuidebookPageTurnWidget (
                    book,
                    forwardlinks.get (new Identifier (AlchymReference.MODID, "main")),
                    GuidebookPageTurnWidget.ArrowDirection.FORWARD,
                    AlchymReference.PageInfo.PAGE_WIDTH - 16 - 2,
                    AlchymReference.PageInfo.PAGE_HEIGHT - 9 - 7, 16, 9, LiteralText.EMPTY);

            widgets.add (turnArrow);

        }
    }
}
