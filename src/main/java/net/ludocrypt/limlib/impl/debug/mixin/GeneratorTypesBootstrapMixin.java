package net.ludocrypt.limlib.impl.debug.mixin;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

@Mixin(targets = "net/minecraft/world/gen/WorldPresets$Registrar")
public abstract class GeneratorTypesBootstrapMixin {

	@Shadow
	private RegistryEntryLookup<Biome> biome;
	@Shadow
	private RegistryEntry<DimensionType> overworld;

	@Inject(method = "Lnet/minecraft/world/gen/WorldPresets$Registrar;bootstrap()V", at = @At("TAIL"))
	public void limlib$addDimensionOpions(CallbackInfo ci) {
		this
			.addDimensionGenerator(DebugWorld.DEBUG_KEY, new DimensionOptions(this.overworld,
				new DebugNbtChunkGenerator(this.biome.getOrThrow(BiomeKeys.THE_VOID))));
	}

	abstract void addDimensionGenerator(RegistryKey<WorldPreset> generator, DimensionOptions dimension);

}
