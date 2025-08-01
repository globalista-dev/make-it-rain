package com.globalista.makeitrain.mixin;

import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherRendering.class)
public abstract class WeatherRendererMixin {

	@Invoker("getPrecipitationAt")
	public abstract Biome.Precipitation invokeGetPrecipitationAt(World world, BlockPos pos);

	@Redirect(
			method = "buildPrecipitationPieces(Lnet/minecraft/world/World;IFLnet/minecraft/util/math/Vec3d;ILjava/util/List;Ljava/util/List;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/WeatherRendering;getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
			)
	)
	private Biome.Precipitation redirectGetPrecipitationAt(WeatherRendering instance, World world, BlockPos pos) {
		Biome.Precipitation original = invokeGetPrecipitationAt(world, pos);
		if (original == Biome.Precipitation.NONE && world.getRegistryKey() == World.OVERWORLD) {
			return Biome.Precipitation.RAIN;
		}
		return original;
	}

	@Redirect(
			method = "addParticlesAndSound(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/render/Camera;ILnet/minecraft/particle/ParticlesMode;)V",
			at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WeatherRendering;getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
	)
)
	private Biome.Precipitation redirectGetPrecipitationAtForSounds(WeatherRendering instance, World world, BlockPos pos) {
		Biome.Precipitation original = invokeGetPrecipitationAt(world, pos);
		if (original == Biome.Precipitation.NONE && world.getRegistryKey() == World.OVERWORLD) {
			return Biome.Precipitation.RAIN;
		}
		return original;
	}


}
