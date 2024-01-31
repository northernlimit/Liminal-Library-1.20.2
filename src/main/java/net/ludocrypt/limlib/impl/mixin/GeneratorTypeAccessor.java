package net.ludocrypt.limlib.impl.mixin;

import java.util.Map;

import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

@Mixin(WorldPreset.class)
public interface GeneratorTypeAccessor {

	@Accessor
	Map<RegistryKey<DimensionOptions>, DimensionOptions> getDimensions();

	@Mutable
	@Accessor
	void setDimensions(Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions);

}
