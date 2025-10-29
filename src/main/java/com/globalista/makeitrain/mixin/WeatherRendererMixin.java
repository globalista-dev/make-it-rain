package com.globalista.makeitrain.mixin;

import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherRendering.class)
public abstract class WeatherRendererMixin {

    @Redirect(
            method = "getPrecipitationAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/world/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation forcePrecipitation(Biome biome, BlockPos pos, int seaLevel, World world, BlockPos pos2) {
        Biome.Precipitation original = biome.getPrecipitation(pos, seaLevel);

        if (original == Biome.Precipitation.NONE) {
            return Biome.Precipitation.RAIN;
        }

        return original;
    }


}
