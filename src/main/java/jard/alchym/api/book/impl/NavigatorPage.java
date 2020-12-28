package jard.alchym.api.book.impl;

import jard.alchym.api.book.BookPage;
import net.minecraft.util.Identifier;

/***
 *  NavigatorPage
 *  Implementation of {@link BookPage}. Represents a quasi-infinite draggable field which spans
 *  the entire GUI. Interactive elements shall populate the field; each one corresponds to exactly one
 *  of the forward-links used to initialize the page.
 *
 *  The list of {@link BookPage}s this {@code NavigatorPage} links to, as well as the relative positioning of their
 *  corresponding elements, is defined by the JSON file pointed to by this {@code NavigatorPage}'s {@code id}. Notably,
 *  'discovering' this {@code NavigatorPage} becomes an implicit pre-requisite of every page it contains.
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
        return null;
    }
}
