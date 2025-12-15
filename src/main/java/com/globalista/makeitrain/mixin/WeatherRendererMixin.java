package com.globalista.makeitrain.mixin;

import com.globalista.makeitrain.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Mixin(WeatherRendering.class)
public abstract class WeatherRendererMixin {

	private File configFile = FabricLoader.getInstance().getConfigDir().resolve("make-it-rain.json").toFile();
	private Config config = Config.loadConfigFile(configFile);

	@WrapOperation(
			method = "buildPrecipitationPieces(Lnet/minecraft/world/World;IFLnet/minecraft/util/math/Vec3d;ILjava/util/List;Ljava/util/List;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/WeatherRendering;getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
			)
	)
	private Biome.Precipitation redirectGetPrecipitationAt(WeatherRendering instance, World world, BlockPos pos, Operation<Biome.Precipitation> operation) {
		Biome.Precipitation original = operation.call(instance, world, pos);
		if (original == Biome.Precipitation.NONE && world.getRegistryKey() == World.OVERWORLD) {
			return Biome.Precipitation.RAIN;
		}
		return original;
	}

	@WrapOperation(
			method = "addParticlesAndSound(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/render/Camera;ILnet/minecraft/particle/ParticlesMode;)V",
			at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WeatherRendering;getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
	)
)
	private Biome.Precipitation redirectGetPrecipitationAtForSounds(WeatherRendering instance, World world, BlockPos pos, Operation<Biome.Precipitation> operation) {
		Biome.Precipitation original = operation.call(instance, world, pos);

		if (original == Biome.Precipitation.NONE) {

			if (config.registryKeyEnable && world.getRegistryKey() == World.OVERWORLD) {
				return Biome.Precipitation.RAIN;
			} else if (!config.registryKeyEnable && world.getDimension().hasSkyLight()) {
				return Biome.Precipitation.RAIN;
			}
			return original;

		}

		return original;
	}


}
