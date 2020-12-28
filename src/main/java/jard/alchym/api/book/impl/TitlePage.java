package jard.alchym.api.book.impl;

import jard.alchym.api.book.BookPage;
import net.minecraft.util.Identifier;

/***
 *  TitlePage
 *  Implementation of {@link BookPage}. The first, immediately accessible page of the Alchymic Reference.
 *
 *  Created by jard at 14:52 on December, 23, 2020.
 ***/
public class TitlePage extends BookPage {
    public TitlePage (Identifier id, BookPage... forwardlinks) throws IllegalArgumentException {
        super (id, forwardlinks);
    }

    @Override
    public BookPage physicalNext () {
        return null;
    }
}
