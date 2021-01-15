package jard.alchym.client.gui.widget;

import jard.alchym.client.gui.screen.GuidebookScreen;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.text.Text;

/***
 *  AbstractGuidebookWidget
 *  The abstract widget class used solely by the Alchymic Reference.
 *  All widgets that are to be used in the guidebook GUI must be subclasses of this class.
 *
 *  Created by jard at 23:27 on January, 14, 2021.
 ***/
public abstract class AbstractGuidebookWidget extends AbstractPressableButtonWidget {
    protected GuidebookScreen book;

    public AbstractGuidebookWidget (GuidebookScreen book, int i, int j, int k, int l, Text text) {
        super (i, j, k, l, text);

        this.book = book;
    }
}
