package jard.alchym.api.book;

import jard.alchym.AlchymReference;
import jard.alchym.client.gui.screen.GuidebookScreen;
import jard.alchym.client.gui.widget.AbstractGuidebookWidget;
import jard.alchym.helper.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 *  BookPage
 *  Top-level abstract class for a page entry in the Alchymic Reference guidebook.
 *
 *  Created by jard at 20:43 on December 22, 2020.
 ***/
public abstract class BookPage {
    /**
     * Retrieve the next 'physical' page to be displayed in the Alchymic Reference GUI.
     *
     * @return the immediate next {@code BookPage} which should be displayed alongside this page
     */
    public abstract BookPage physicalNext ();

    public final Identifier id;

    protected BookPage backlink;
    protected final Map<Identifier, BookPage> forwardlinks = new HashMap<> ();

    /**
     * Constructs a {@code BookPage}. Back-links are automatically assigned to this page for every {@code BookPage}
     * passed in as a forward-link.
     *
     * @param id the {@linkplain Identifier} of this page, which may or may not correspond with
     *           a JSON file in the assets/alchym/pages directory.
     * @param forwardlinks array of {@code BookPage}s that this entry links to
     */
    public BookPage (Identifier id, BookPage... forwardlinks) throws IllegalArgumentException {
        this.id = id;
        this.backlink = null;

        if (checkForwardlinks (forwardlinks))
            for (BookPage forwardlink : forwardlinks) {
                forwardlink.backlink = this;
                this.forwardlinks.put (forwardlink.id, forwardlink);
            }
    }

    private boolean checkForwardlinks (BookPage [] forwardlinks) {
        return (forwardlinks != null) && (forwardlinks.length > 0) && MathHelper.implies (forwardlinks.length == 1, forwardlinks [0] != null);
    }

    @Environment (EnvType.CLIENT)
    public abstract void populateWidgets (GuidebookScreen book, List <AbstractGuidebookWidget> widgets, AlchymReference.PageInfo.BookSide side);
}
