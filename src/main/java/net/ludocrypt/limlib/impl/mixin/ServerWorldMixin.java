package net.ludocrypt.limlib.impl.mixin;

import java.util.List;
import java.util.concurrent.Executor;

import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorage;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorageProvider;
import net.ludocrypt.limlib.api.world.maze.storage.ServerWorldMazeAccess;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldMazeAccess {

	@Unique
	MazeStorage mazeStorage;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void limlib$init(MinecraftServer server, Executor executor, LevelStorage.Session session,
							 ServerWorldProperties worldProperties, RegistryKey<World> registryKey, DimensionOptions dimensionOptions,
							 WorldGenerationProgressListener worldGenerationProgressListener, boolean bl, long l, List<Spawner> spawners,
							 boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState, CallbackInfo ci) {

		if (dimensionOptions.chunkGenerator() instanceof MazeStorageProvider provider) {
			this.mazeStorage = new MazeStorage(provider.generators(),
				session.getWorldDirectory(registryKey).resolve("maze_region").toFile());
		}

		if (dimensionOptions.chunkGenerator() instanceof AbstractNbtChunkGenerator generator) {
			generator.loadTags(((ServerWorld) (Object) this));
		}

	}

	@Inject(method = "saveLevel", at = @At("TAIL"))
	private void limlib$saveWorld(CallbackInfo ci) {

		if (this.mazeStorage != null) {
			this.mazeStorage.save();
		}

	}

	@Override
	public MazeStorage getMazeStorage() {
		return this.mazeStorage;
	}

}
