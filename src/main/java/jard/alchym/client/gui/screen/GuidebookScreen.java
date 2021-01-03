package jard.alchym.client.gui.screen;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.MatrixStackAccess;
import jard.alchym.client.helper.BookHelper;
import jard.alchym.client.helper.RenderHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;

import java.util.*;

public class GuidebookScreen extends Screen {
    private static final Identifier [] BOOK_TEXTURE = {
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.1.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.2.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.3.png"),
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.4.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.5.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.6.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.7.png"),
    };

    private static final List<Pair <Vec2f, Vec2f>> PAGE_COORDINATES  = new ArrayList<> ();
    private static final List<Pair <Matrix4f, Matrix4f>> PAGE_SHEARS = new ArrayList<> ();

    static {
        // #1
        PAGE_COORDINATES.add (new Pair<> (new Vec2f (77.f, 60.f), new Vec2f (336.f, 34.f)));
        PAGE_SHEARS.add (new Pair<> (RenderHelper.yShear (2.35f), RenderHelper.IDENTITY_MATRIX));
        // #2
        //PAGE_COORDINATES.add (???);
        // #3
        //PAGE_COORDINATES.add (???);
        // #4
        PAGE_COORDINATES.add (new Pair<> (new Vec2f (72.f, 32.f), new Vec2f (344.f, 32.f)));
        PAGE_SHEARS.add (new Pair<> (RenderHelper.IDENTITY_MATRIX, RenderHelper.IDENTITY_MATRIX));
        // #5
        //PAGE_COORDINATES.add (???);
        // #6
        //PAGE_COORDINATES.add (???);
        // #7
        //PAGE_COORDINATES.add (???);
    };

    private BookPage currentPage;

    public GuidebookScreen (BookPage page, Text text) {
        super (text);

        currentPage = page;
    }


    public void render (MatrixStack stack, int i, int j, float f) {
        this.renderBackground (stack);

        stack.push ();

        stack.scale (2, 2, 2);
        this.client.getTextureManager ().bindTexture(BOOK_TEXTURE [0]);
        this.drawTexture(stack, (this.width - 320) / 4, 4, 0, 0, 160, 104);

        stack.scale (0.25f, 0.25f, 0.25f);

        Pair <Vec2f, Vec2f>       pageCoords = PAGE_COORDINATES.get (0);
        Pair <Matrix4f, Matrix4f> pageShears = PAGE_SHEARS.get (0);

        // Left page
        stack.push ();
        stack.translate ((this.width - 320) + pageCoords.getLeft ().x, pageCoords.getLeft ().y + 16.f, 0.f);
        ((MatrixStackAccess) stack).multiply (pageShears.getLeft ());

        Alchym.getProxy ().renderPage (stack, currentPage, AlchymReference.PageInfo.BookSide.LEFT);

        stack.pop ();

        // Right page
        stack.push ();
        stack.translate ((this.width - 320) + pageCoords.getRight ().x, pageCoords.getRight ().y + 16.f, 0.f);
        ((MatrixStackAccess) stack).multiply (pageShears.getRight ());

        Alchym.getProxy ().renderPage (stack, currentPage.physicalNext (), AlchymReference.PageInfo.BookSide.RIGHT);

        stack.pop ();

        // TODO: Implement all non-page rendering here (bookmarks, tabs, addons, etc)

        stack.pop ();
    }
}
