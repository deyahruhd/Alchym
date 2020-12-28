package jard.alchym.api.book.impl;

import jard.alchym.api.book.BookPage;
import net.minecraft.util.Identifier;

/***
 *  ContentPage
 *  Implementation of {@link BookPage}. Acts as the node for a doubly-linked list of pages that
 *  represent an entry in the guidebook.
 *
 *  Created by jard at 19:37 on December, 26, 2020.
 ***/
public class ContentPage extends BookPage {
    public ContentPage (Identifier id, BookPage next) throws IllegalArgumentException {
        super (id, next);
    }

    @Override
    public BookPage physicalNext () {
        if (forwardlinks == null)
            return null;

        return forwardlinks.values ().toArray (new BookPage [0]) [0];
    }
}
