package jard.alchym.api.book.impl;

import jard.alchym.api.book.BookPage;
import net.minecraft.util.Identifier;

/***
 *  TitlePage
 *  Subclass of {@link ContentPage}. The first, immediately accessible page of the Alchymic Reference.
 *
 *  Created by jard at 14:52 on December, 23, 2020.
 ***/
public class TitlePage extends ContentPage {
    public TitlePage (Identifier id, BookPage main, String [] content) throws IllegalArgumentException {
        super (id, main, content);

        if (! (main instanceof NavigatorPage))
            throw new IllegalArgumentException ("Forward-link passed to TitlePage must be a NavigatorPage");
    }

    @Override
    public BookPage physicalNext () {
        // The TitlePage only occupies the right side of the book
        return this;
    }
}
