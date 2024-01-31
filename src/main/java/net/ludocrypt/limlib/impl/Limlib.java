package net.ludocrypt.limlib.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.ludocrypt.limlib.api.effects.sky.ModDimensionEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import java.lang.Iterable;

public class Limlib implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Limlib");

	@Override
	public void onInitialize() {
		LimlibWorld.load();

		// Effects
		ReverbEffect.init();
		DistortionEffect.init();
		ModDimensionEffects.init();
		PostEffect.init();
		Skybox.init();

		Registry
			.register(Registries.CHUNK_GENERATOR, new Identifier("limlib", "debug_nbt_chunk_generator"),
				DebugNbtChunkGenerator.CODEC);
		FabricLoader
				.getInstance().getEntrypoints(LimlibRegistrar.ENTRYPOINT_KEY, LimlibRegistrar.class)
				.forEach(LimlibRegistrar::registerHooks);
	}
}
