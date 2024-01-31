package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;

import net.ludocrypt.limlib.api.effects.sky.ModDimensionEffects;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionEffects.class)
public class DimensionVisualEffectsMixin {

	@Inject(method = "byDimensionType", at = @At("HEAD"), cancellable = true)
	private static void limlib$byDimensionType(DimensionType dimensionType,
			CallbackInfoReturnable<DimensionEffects> ci) {
		Optional<ModDimensionEffects> dimensionEffects = LookupGrabber
			.snatch(ModDimensionEffects.MIXIN_WORLD_LOOKUP.get(),
				RegistryKey.of(ModDimensionEffects.DIMENSION_EFFECTS_KEY, dimensionType.effects()));

		if (dimensionEffects.isPresent()) {
			ci.setReturnValue(dimensionEffects.get().getDimensionEffects());
		}

	}

}
