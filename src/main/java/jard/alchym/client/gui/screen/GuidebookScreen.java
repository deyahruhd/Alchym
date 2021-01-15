package jard.alchym.client.gui.screen;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.api.book.BookPage;
import jard.alchym.client.MatrixStackAccess;
import jard.alchym.client.gui.widget.AbstractGuidebookWidget;
import jard.alchym.client.helper.BookHelper;
import jard.alchym.client.helper.RenderHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;

import java.util.*;

public class GuidebookScreen extends Screen {
    public static final Identifier [] BOOK_TEXTURE = {
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.1.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.2.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.3.png"),
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.4.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.5.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.6.png"),
        //new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.7.png"),
    };
    public static final Identifier BOOK_ELEMENTS = new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.elements.png");

    private static final List<Pair <Vec2f, Vec2f>> PAGE_COORDINATES          = new ArrayList<> ();
    private static final List<Pair <Matrix4f, Matrix4f>> PAGE_SHEARS         = new ArrayList<> ();
    private static final List<Pair <Matrix4f, Matrix4f>> PAGE_SHEAR_INVERSES = new ArrayList<> ();

    static {
        // #1
        addPageTransform (new Vec2f (77.f, 60.f), new Vec2f (343.f, 34.f), RenderHelper.yShear (2.35f), RenderHelper.IDENTITY_MATRIX);
        // #2
        //addPageTransform (???);
        // #3
        //addPageTransform (???);
        // #4
        addPageTransform (new Vec2f (72.f, 32.f), new Vec2f (344.f, 32.f), RenderHelper.IDENTITY_MATRIX, RenderHelper.IDENTITY_MATRIX);
        // #5
        //addPageTransform (???);
        // #6
        //addPageTransform (???);
        // #7
        //addPageTransform (???);
    };
    private static void addPageTransform (Vec2f coordLeft, Vec2f coordRight, Matrix4f shearLeft, Matrix4f shearRight) {
        Matrix4f inverseLeft = shearLeft.copy ();
        inverseLeft.invert ();
        Matrix4f inverseRight = shearRight.copy ();
        inverseRight.invert ();
        PAGE_COORDINATES.add (new Pair <> (coordLeft, coordRight));
        PAGE_SHEARS.add (new Pair <> (shearLeft, shearRight));
        PAGE_SHEAR_INVERSES.add (new Pair <> (inverseLeft, inverseRight));
    }

    private List <AbstractGuidebookWidget> leftPageWidgets  = new ArrayList<> ();
    private List <AbstractGuidebookWidget> rightPageWidgets = new ArrayList<> ();

    private int bookProgress;
    private BookPage currentPage;

    public GuidebookScreen (BookPage page, Text text) {
        super (text);

        currentPage = page;
        bookProgress = 0;
    }

    public void init () {
        leftPageWidgets.clear ();
        rightPageWidgets.clear ();

        BookPage left  = currentPage;
        BookPage right = currentPage.physicalNext ();

        left.populateWidgets (this, leftPageWidgets, AlchymReference.PageInfo.BookSide.LEFT);
        right.populateWidgets (this, rightPageWidgets, AlchymReference.PageInfo.BookSide.RIGHT);
    }

    public void jumpToPage (BookPage page) {
        currentPage = page;
        bookProgress = 0;

        init ();
    }

    @Override
    public boolean mouseClicked (double d, double e, int i) {
        Pair <Vec2f, Vec2f>       pageCoords = PAGE_COORDINATES.get (bookProgress);
        Pair <Matrix4f, Matrix4f> pageShearInverses = PAGE_SHEAR_INVERSES.get (bookProgress);

        Vector4f mouseCoords = new Vector4f ((float) d, (float) e, 0.f, 0.f);

        Vector4f leftTransformCoords = new Vector4f (
                mouseCoords.getX () - (this.width - 320 + pageCoords.getLeft ().x) / 2.f,
                mouseCoords.getY () - (pageCoords.getLeft ().y) / 2.f,
                0.f, 0.f);
        leftTransformCoords.transform (pageShearInverses.getLeft ());

        Vector4f rightTransformCoords = new Vector4f (
                mouseCoords.getX () - (this.width - 320 + pageCoords.getRight ().x) / 2.f,
                mouseCoords.getY () - (pageCoords.getRight ().y) / 2.f,
                0.f, 0.f);
        rightTransformCoords.transform (pageShearInverses.getRight ());

        if (BookHelper.withinPageBounds (leftTransformCoords.getX (), leftTransformCoords.getY ()))
            propagateToPageWidgets (leftPageWidgets, leftTransformCoords.getX (), leftTransformCoords.getY (), i);
        else if (BookHelper.withinPageBounds (rightTransformCoords.getX (), rightTransformCoords.getY ()))
            propagateToPageWidgets (rightPageWidgets, rightTransformCoords.getX (), rightTransformCoords.getY (), i);

        return false;
    }

    public void render (MatrixStack stack, int i, int j, float f) {
        this.renderBackground (stack);

        stack.push ();

        this.client.getTextureManager ().bindTexture (BOOK_TEXTURE [bookProgress]);
        this.drawTexture(stack, (int) (((float) this.width - 320.f) / 2.f), 8, 0, 0, 320, 208, 512, 512);

        Pair <Vec2f, Vec2f>       pageCoords = PAGE_COORDINATES.get (bookProgress);
        Pair <Matrix4f, Matrix4f> pageShears = PAGE_SHEARS.get (bookProgress);

        stack.scale (0.5f, 0.5f, 0.5f);

        // Left page
        stack.push ();
        stack.translate (this.width - 320 + pageCoords.getLeft ().x, pageCoords.getLeft ().y, 0.f);
        ((MatrixStackAccess) stack).multiply (pageShears.getLeft ());

        stack.scale (2.f, 2.f, 2.f);

        for (AbstractGuidebookWidget widget : leftPageWidgets) {
            widget.renderButton (stack, i, j, f);
        }

        stack.scale (0.5f, 0.5f, 0.5f);
        stack.translate (0.f, 16.f, 0.f);

        Alchym.getProxy ().renderPage (stack, currentPage, AlchymReference.PageInfo.BookSide.LEFT);

        stack.pop ();

        // Right page
        stack.push ();
        stack.translate (this.width - 320 + pageCoords.getRight ().x, pageCoords.getRight ().y, 0.f);
        ((MatrixStackAccess) stack).multiply (pageShears.getRight ());

        stack.scale (2.f, 2.f, 2.f);

        for (AbstractGuidebookWidget widget : rightPageWidgets) {
            widget.renderButton (stack, i, j, f);
        }

        stack.scale (0.5f, 0.5f, 0.5f);
        stack.translate (0.f, 16.f, 0.f);

        Alchym.getProxy ().renderPage (stack, currentPage.physicalNext (), AlchymReference.PageInfo.BookSide.RIGHT);

        stack.pop ();

        // TODO: Implement all non-page rendering here (bookmarks, tabs, addons, etc)

        stack.pop ();
    }

    private boolean propagateToPageWidgets (List <AbstractGuidebookWidget> widgets, double x, double y, int i) {
        Iterator <AbstractGuidebookWidget> var6 = widgets.iterator();

        Element element;
        do {
            if (!var6.hasNext ()) {
                return false;
            }

            element = var6.next();
        } while (!element.mouseClicked (x, y, i));

        this.setFocused (element);
        if (i == 0) {
            this.setDragging (true);
        }

        return true;
    }
}
