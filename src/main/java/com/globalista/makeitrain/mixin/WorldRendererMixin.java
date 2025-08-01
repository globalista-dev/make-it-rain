package com.globalista.makeitrain.mixin;

import com.globalista.makeitrain.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	private File configFile = FabricLoader.getInstance().getConfigDir().resolve("make-it-rain.json").toFile();
	private Config config = Config.loadConfigFile(configFile);

	@Shadow @Final MinecraftClient client;

	// Redirect biome.hasPrecipitation() inside renderWeather(...)
	@Redirect(
			method = "renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/Biome;hasPrecipitation()Z"
			)
	)
	private boolean overrideHasPrecipitation(Biome biome) {
		if (biome.hasPrecipitation()) return true;
		return client.world.getRegistryKey() == World.OVERWORLD;
	}

	@Redirect(
			method = "renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
			)
	)
	private Biome.Precipitation overrideGetPrecipitation(Biome biome, BlockPos pos) {
		Biome.Precipitation original = biome.getPrecipitation(pos);
		if (original == Biome.Precipitation.NONE) {

			if (config.registryKeyEnable && client.world.getRegistryKey() == World.OVERWORLD) {
				return Biome.Precipitation.RAIN;
			} else if (!config.registryKeyEnable && client.world.getDimension().hasSkyLight()) {
				return Biome.Precipitation.RAIN;
			}

			return original;

		}
		return original;
	}

	@Redirect(
			method = "tickRainSplashing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
			)
	)
	private Biome.Precipitation overrideRainSoundPrecipitation(Biome biome, BlockPos pos) {
		var original = biome.getPrecipitation(pos);

		if (original == Biome.Precipitation.NONE) {
			if (config.registryKeyEnable && client.world.getRegistryKey() == World.OVERWORLD) {
				return Biome.Precipitation.RAIN;
			} else if (!config.registryKeyEnable && client.world.getDimension().hasSkyLight()) {
				return Biome.Precipitation.RAIN;
			}

			return original;
		}

		return original;
	}
}

