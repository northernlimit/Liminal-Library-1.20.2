package net.ludocrypt.limlib.impl;

import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.registry.DynamicRegistryManager;

public class SaveStorageSupplier {

	public static final AtomicReference<DynamicRegistryManager.Immutable> LOADED_REGISTRY = new AtomicReference<>();

}
