package jard.alchym.api.book.impl;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.gui.screen.GuidebookScreen;
import jard.alchym.client.gui.widget.AbstractGuidebookWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.List;

/***
 *  NavigatorPage
 *  Implementation of {@link BookPage}. Represents a quasi-infinite draggable field which spans
 *  the entire GUI. Interactive elements shall populate the field; each one corresponds to exactly one
 *  of the forward-links used to initialize the page.
 *
 *  The list of {@link BookPage}s this {@code NavigatorPage} links to, as well as the relative positioning of their
 *  corresponding elements, is defined by the JSON file pointed to by this {@code NavigatorPage}'s {@code id}.
 *
 *  Created by jard at 21:14 on December, 22, 2020.
 ***/
public class NavigatorPage extends BookPage {
    public NavigatorPage (Identifier id, BookPage... forwardlinks) throws IllegalArgumentException {
        super (id, forwardlinks);
    }

    @Override
    public BookPage physicalNext () {
        // NavigatorPages do not have a successor page since they occupy both sides of the GUI.
        return this;
    }

    @Override
    @Environment (EnvType.CLIENT)
    public void populateWidgets (GuidebookScreen book, List<AbstractGuidebookWidget> widgets, AlchymReference.PageInfo.BookSide side) {

    }
}
