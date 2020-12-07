package jard.alchym.client.gui.screen;

import jard.alchym.AlchymReference;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AlchymRefBookScreen extends Screen {
    public static final Identifier BOOK_TEXTURE [] = {
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.1.png"),
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.2.png"),
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.3.png"),
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.4.png"),
        new Identifier (AlchymReference.MODID, "textures/gui/alchymic_reference.5.png")
    };


    public AlchymRefBookScreen (Text text) {
        super (text);
    }


    public void render (MatrixStack matrixStack, int i, int j, float f) {
        this.renderBackground (matrixStack);

        matrixStack.push ();

        matrixStack.scale (2, 2, 2);

        this.client.getTextureManager ().bindTexture(BOOK_TEXTURE [0]);
        this.drawTexture(matrixStack, (this.width - 320) / 4, 4, 0, 0, 160, 96);

        matrixStack.pop ();
    }
}
