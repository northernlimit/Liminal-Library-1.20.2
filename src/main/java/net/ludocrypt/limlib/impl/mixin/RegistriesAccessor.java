package net.ludocrypt.limlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.serialization.Lifecycle;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

@Mixin(Registries.class)
public interface RegistriesAccessor {

	@Invoker("create")
	static <T> Registry<T> callRegisterSimple(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle,
													 Registries.Initializer<T> initializer) {
		throw new UnsupportedOperationException();
	}

}