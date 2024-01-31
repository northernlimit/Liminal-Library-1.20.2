package net.ludocrypt.limlib.impl.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import net.ludocrypt.limlib.api.effects.sky.ModDimensionEffects;
import net.minecraft.registry.entry.RegistryElementCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.serialization.Codec;

import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

@Mixin(RegistryElementCodec.class)
public abstract class DynamicRegistrySyncMixin {

	@Inject(method = "method_45958()Lcom/google/common/collect/ImmutableMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryElementCodec;addSyncedRegistry(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Codec;)V", ordinal = 2, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void limlib$makeMap$mapped(
			CallbackInfoReturnable<ImmutableMap<RegistryKey<? extends Registry<?>>, RegistryElementCodec<?>>> ci,
			Builder<RegistryKey<? extends Registry<?>>, RegistryElementCodec<?>> builder) {
		addSyncedRegistry(builder, PostEffect.POST_EFFECT_KEY, PostEffect.CODEC);
		addSyncedRegistry(builder, ModDimensionEffects.DIMENSION_EFFECTS_KEY, ModDimensionEffects.CODEC);
		addSyncedRegistry(builder, SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC);
		addSyncedRegistry(builder, Skybox.SKYBOX_KEY, Skybox.CODEC);
	}

	@Shadow
	private native static <E> void addSyncedRegistry(
			Builder<RegistryKey<? extends Registry<?>>, RegistryElementCodec<?>> builder,
			RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec);

}
