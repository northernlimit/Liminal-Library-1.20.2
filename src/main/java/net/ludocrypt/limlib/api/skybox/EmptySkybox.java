package net.ludocrypt.limlib.api.skybox;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class EmptySkybox extends Skybox {

	public static final Codec<EmptySkybox> CODEC = RecordCodecBuilder
		.create((instance) -> instance.stable(new EmptySkybox()));

	@Override
	@Environment(EnvType.CLIENT)
	public void renderSky(WorldRenderer worldRenderer, MinecraftClient client, MatrixStack matrices,
			Matrix4f projectionMatrix, float tickDelta) {
	}

	@Override
	public Codec<? extends Skybox> getCodec() {
		return CODEC;
	}

}
