package net.ludocrypt.limlib.impl.debug.mixin;

import java.util.Optional;

import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugWorld;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(WorldPresets.class)
public class GeneratorTypesMixin {

	@Inject(method = "getKey", at = @At("HEAD"), cancellable = true)
	private static void limlib$getKey(Registry<DimensionOptions> registry,
									  CallbackInfoReturnable<Optional<RegistryKey<WorldPreset>>> ci) {
		Optional<RegistryKey<WorldPreset>> optional = registry
				.getOrEmpty(DimensionOptions.OVERWORLD)
				.flatMap(dimensionOptions -> {
					ChunkGenerator chunkGenerator = dimensionOptions.chunkGenerator();

					if (chunkGenerator instanceof DebugNbtChunkGenerator) {
						return Optional.of(DebugWorld.DEBUG_KEY);
					}

					return Optional.empty();
				});

		if (optional.isPresent()) {
			ci.setReturnValue(optional);
		}

	}

}
