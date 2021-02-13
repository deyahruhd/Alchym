package jard.alchym.mixin.rendering;

import jard.alchym.AlchymReference;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 *  DynamicModelLoaderMixin
 *  Supplies dynamically generated JSON models in lieu of loading asset files whenever necessary.
 *
 *  Created by jard at 15:55 on February, 11, 2021.
 ***/
@Mixin (ModelLoader.class)
public abstract class DynamicModelLoaderMixin {
    private static final String FORMATTABLE_FLASK_LAYER = "{\n" +
            "  \"parent\": \"item/generated\",\n" +
            "  \"textures\": {\n" +
            "    \"layer0\": \"alchym:item/chymical_flask_layers/%s\"\n" +
            "  }\n" +
            "}";

    private static final Pattern FIND_MATERIAL = Pattern.compile ("item/(.*)_flask_layer");

    @Inject (method = "loadModelFromJson", at = @At ("HEAD"), cancellable = true)
    public void injectDynamicJson (Identifier id, CallbackInfoReturnable<JsonUnbakedModel> info) {
        if (id.getNamespace ().equals (AlchymReference.MODID)) {
            JsonUnbakedModel model = null;

            if (id.getPath ().equals (String.format ("item/%s_base", AlchymReference.Items.CHYMICAL_FLASK.getName ()))) {
                model = JsonUnbakedModel.deserialize (AlchymReference.BASE_FLASK_LAYER_MODEL);
            } else {
                Matcher m = FIND_MATERIAL.matcher (id.getPath ());
                if (m.find ()) {
                    String materialType = m.group (1);

                    String layerAsJson = String.format (FORMATTABLE_FLASK_LAYER, materialType);

                    model = JsonUnbakedModel.deserialize (layerAsJson);
                }
            }

            if (model != null) {
                model.id = id.toString ();
                info.setReturnValue (model);
                info.cancel ();
            }
        }
    }
}
