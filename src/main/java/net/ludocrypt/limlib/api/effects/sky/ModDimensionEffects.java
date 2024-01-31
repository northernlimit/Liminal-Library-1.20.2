package net.ludocrypt.limlib.api.effects.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import net.minecraft.client.render.DimensionEffects;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * A non-client-side clone of {@link DimensionEffects}
 */
public abstract class ModDimensionEffects {


	public static final RegistryKey<Registry<Codec<? extends ModDimensionEffects>>> DIMENSION_EFFECTS_CODEC_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib/codec/dimension_effects"));
	public static final Registry<Codec<? extends ModDimensionEffects>> DIMENSION_EFFECTS_CODEC = RegistriesAccessor
		.callRegisterSimple(DIMENSION_EFFECTS_CODEC_KEY, Lifecycle.stable(), (registry) -> StaticDimensionEffects.CODEC);
	public static final Codec<ModDimensionEffects> CODEC = DIMENSION_EFFECTS_CODEC
		.getCodec()
		.dispatchStable(ModDimensionEffects::getCodec, Function.identity());
	public static final RegistryKey<Registry<ModDimensionEffects>> DIMENSION_EFFECTS_KEY = RegistryKey
		.ofRegistry(new Identifier("limlib/dimension_effects"));

	@ApiStatus.Internal
	public static final AtomicReference<RegistryWrapper<ModDimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<>();

	public abstract Codec<? extends ModDimensionEffects> getCodec();

	public static void init() {
		Registry
			.register(ModDimensionEffects.DIMENSION_EFFECTS_CODEC, new Identifier("limlib", "static"),
				StaticDimensionEffects.CODEC);
		Registry
			.register(ModDimensionEffects.DIMENSION_EFFECTS_CODEC, new Identifier("limlib", "empty"),
				EmptyDimensionEffects.CODEC);
	}

	@Environment(EnvType.CLIENT)
	public abstract DimensionEffects getDimensionEffects();

	public abstract float getSkyShading();

}
