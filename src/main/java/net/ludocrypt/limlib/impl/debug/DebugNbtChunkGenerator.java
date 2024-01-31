package net.ludocrypt.limlib.impl.debug;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.nbt.NbtGroup;
import net.ludocrypt.limlib.api.world.nbt.NbtPlacerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

public class DebugNbtChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<DebugNbtChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance
			.group(RegistryOps.getEntryCodec(BiomeKeys.THE_VOID))
			.apply(instance, instance.stable(DebugNbtChunkGenerator::new));
	});
	BiMap<Identifier, BlockPos> positions = HashBiMap.create();

	public DebugNbtChunkGenerator(RegistryEntry.Reference<Biome> reference) {
		super(new FixedBiomeSource(reference), new DebugNbtGroup());
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

	}

	@Override
	public int getPlacementRadius() {
		return 4;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(ChunkRegion chunkRegion, ChunkStatus targetStatus, Executor executor,
			ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager,
			ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> fullChunkConverter, List<Chunk> chunks,
			Chunk chunk) {

		if (chunk.getPos().getStartPos().getX() < 0 || chunk.getPos().getStartPos().getZ() < 0) {
			return CompletableFuture.completedFuture(chunk);
		}

		ResourceManager resourceManager = world.getServer().getResourceManager();

		if (positions.isEmpty()) {
			Map<Identifier, List<Resource>> ids = resourceManager
				.findAllResources("structures/nbt", (id) -> id.getPath().endsWith(".nbt"));
			Map<Identifier, NbtPlacerUtil> nbts = new LinkedHashMap<>();

			for (Identifier id : ids.keySet()) {
				NbtPlacerUtil nbt = NbtPlacerUtil.load(id, resourceManager);
				nbts.put(id, nbt);
			}

			List<Map.Entry<Identifier, NbtPlacerUtil>> sortedNbts = new ArrayList<>(nbts.entrySet());
			sortedNbts.sort((a, b) -> a.getKey().compareTo(b.getKey()));
			int maxSizeZ = 0;

			for (int i = 0; i < sortedNbts.size(); i++) {
				Map.Entry<Identifier, NbtPlacerUtil> entry = sortedNbts.get(i);
				BlockPos prevPos;
				BlockPos prevSize;

				if (i == 0) {
					prevPos = BlockPos.ORIGIN;
					prevSize = BlockPos.ORIGIN.add(-2, 0, 0);
				} else {
					prevPos = positions.get(sortedNbts.get(i - 1).getKey());
					prevSize = new BlockPos(sortedNbts.get(i - 1).getValue().sizeX, sortedNbts.get(i - 1).getValue().sizeY,
						sortedNbts.get(i - 1).getValue().sizeZ);
				}

				if (prevPos.getX() > 160) {
					prevPos = BlockPos.ORIGIN.add(-prevSize.getX() - 2, 0, prevPos.getZ() + maxSizeZ + 2);
					maxSizeZ = 0;
				}

				if (entry.getValue().sizeZ > maxSizeZ) {
					maxSizeZ = entry.getValue().sizeZ;
				}

				positions.put(entry.getKey(), prevPos.add(prevSize.getX() + 2, 0, 0));
				this.nbtGroup
					.getGroups()
					.computeIfAbsent("debug", (s) -> Lists.newArrayList())
					.add(entry.getKey().toString());
			}

			this.nbtGroup.fill(this.structures);
		}

		for (int x = 0; x < 16; x++) {

			for (int z = 0; z < 16; z++) {
				BlockPos pos = chunk.getPos().getStartPos().add(x, 10, z);

				if (positions.inverse().containsKey(pos.add(0, -10, 0))) {
					Identifier id = positions.inverse().get(pos.add(0, -10, 0));
					this.generateNbt(chunkRegion, pos, id);
					chunkRegion
						.setBlockState(pos.add(-1, -1, -1),
							Blocks.STRUCTURE_BLOCK.getDefaultState().with(StructureBlock.MODE, StructureBlockMode.SAVE),
							Block.FORCE_STATE);
					BlockEntity be = chunkRegion.getBlockEntity(pos.add(-1, -1, -1));

					if (be != null && be instanceof StructureBlockBlockEntity blockEntity) {
						blockEntity
							.setSize(new Vec3i(this.structures.eval(id, resourceManager).sizeX,
								this.structures.eval(id, resourceManager).sizeY,
								this.structures.eval(id, resourceManager).sizeZ));
						blockEntity
							.setTemplateName(
								id.toString().substring(0, id.toString().length() - 4).replaceFirst("structures/", ""));
						blockEntity.setOffset(new BlockPos(1, 1, 1));
						blockEntity.setIgnoreEntities(false);
					}

				}

				chunkRegion.setBlockState(pos.add(0, -10, 0), Blocks.BARRIER.getDefaultState(), Block.FORCE_STATE);
			}

		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getWorldHeight() {
		return 448;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		return null;
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return 0;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		return null;
	}

	@Override
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

	}

	@Override
	protected void modifyStructure(ChunkRegion region, BlockPos pos, BlockState state, Optional<NbtCompound> blockEntityNbt,
			int update, int depth) {
		region.setBlockState(pos, state, Block.FORCE_STATE, 0);
		blockEntityNbt.ifPresent((nbt) -> {
			if (region.getBlockEntity(pos) != null)
				region.getBlockEntity(pos).readNbt(nbt);
		});
	}
	public void method_40450(List<String> list, NoiseConfig randomState, BlockPos pos) {
	}

	public static class DebugNbtGroup extends NbtGroup {

		public DebugNbtGroup() {
			super(new Identifier("debug"), Maps.newHashMap());
		}

		@Override
		public Identifier nbtId(String group, String nbt) {
			return new Identifier(nbt);
		}

	}

}
