package net.ludocrypt.limlib.impl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;


import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.resource.ResourceType;

public class LimlibClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ResourceManagerHelperImpl.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(PostProcesserManager.INSTANCE);
	}}
