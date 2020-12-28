package jard.alchym.api.book;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

/***
 *  BookPageJson
 *  Stub object for page JSON objects loaded in from the Alchym datapack.
 *
 *  Created by jard at 18:32 on December, 26, 2020.
 ***/
public class BookPageStub {
    public enum Type {
        NAVIGATOR,
        CONTENT
    }

    public final Identifier id = null;
    public final Type type = null;

    public final String [] contents = null;

    public final Identifier [] requirements = null;
    @SerializedName (value = "links-to")
    public final Identifier [] linksTo = null;
}
