package jard.alchym.init;

import jard.alchym.AlchymReference;
import jard.alchym.blocks.AlchymBlock;
import jard.alchym.blocks.MaterialBlock;
import jard.alchym.world.gen.feature.AlchymFeature;
import jard.alchym.world.gen.feature.NiterDepositFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

/***
 *  InitWorldgenFeatures
 *  The initializing module that initializes every generation feature/configuration object in the mod.
 *
 *  Created by jard at 14:37 on March, 13, 2021.
 ***/
public class InitWorldgenFeatures extends InitAbstract <Feature <?>> {
    public final Feature niterDeposits = new NiterDepositFeature (DefaultFeatureConfig.CODEC);

    InitWorldgenFeatures (InitAlchym alchym) {
        super (Registry.FEATURE, alchym);
    }

    @Override
    public void initialize () {
        register (AlchymReference.WorldGen.Features.NITER_DEPOSIT.getName (), niterDeposits);
    }

    @Override
    void preRegister (String id, Feature <?> obj) {
        if (obj instanceof AlchymFeature) {
            RegistryKey <ConfiguredFeature <?, ?>> key = RegistryKey.of (Registry.CONFIGURED_FEATURE_WORLDGEN,
                    new Identifier (AlchymReference.MODID, id));

            Registry.register (BuiltinRegistries.CONFIGURED_FEATURE, key.getValue (), ((AlchymFeature <?>) obj).getConfiguration ());

            BiomeModifications.addFeature (((AlchymFeature<?>) obj).getSelectors (), ((AlchymFeature<?>) obj).getGenerationStep (), key);
        } else
            throw new RuntimeException ("Features registered with InitWorldgenFeatures must extend AlchymFeature");
    }
}
