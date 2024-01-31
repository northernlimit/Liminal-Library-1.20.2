package net.ludocrypt.limlib.impl.mixin;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.registry.*;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;

import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.LimlibWorld.RegistryProvider;
import net.ludocrypt.limlib.impl.SaveStorageSupplier;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.dimension.DimensionOptions;

@Mixin(LevelStorage.class)
public class WorldSaveStorageMixin {

	@ModifyVariable(method = "readGeneratorProperties(Lcom/mojang/serialization/Dynamic;Lcom/mojang/datafixers/DataFixer;I)Lcom/mojang/serialization/DataResult;", at = @At(value = "STORE"), ordinal = 1)
	private static <T> Dynamic<T> limlib$readGeneratorProperties$datafix(Dynamic<T> in, Dynamic<T> levelData,
			DataFixer dataFixer, int version) {
		Dynamic<T> dynamic = in;

		for (Entry<RegistryKey<LimlibWorld>, LimlibWorld> entry : LimlibWorld.LIMLIB_WORLD.getEntrySet()) {
			dynamic = limlib$addDimension(entry.getKey(), entry.getValue(), dynamic);
		}

		return dynamic;
	}

	@Unique
	@SuppressWarnings("unchecked")
	private static <T> Dynamic<T> limlib$addDimension(RegistryKey<LimlibWorld> key, LimlibWorld world, Dynamic<T> in) {
		Dynamic<T> dimensions = in.get("dimensions").orElseEmptyMap();

		if (!dimensions.get(key.getValue().toString()).result().isPresent()) {
			Map<Dynamic<T>, Dynamic<T>> dimensionsMap = Maps.newHashMap(dimensions.getMapValues().result().get());

			DynamicRegistryManager registryManager = SaveStorageSupplier.LOADED_REGISTRY.get();

			dimensionsMap
				.put(dimensions.createString(key.getValue().toString()),
					new Dynamic<T>(dimensions.getOps(),
						(T) DimensionOptions.CODEC
							.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager),
								world.getDimensionOptionsSupplier().apply(new RegistryProvider() {

									@Override
									public <Q> RegistryEntryLookup<Q> get(RegistryKey<Registry<Q>> key) {
										return registryManager.getOptionalWrapper(key).get();
									}

								}))
							.result()
							.get()));
			in = in.set("dimensions", in.createMap(dimensionsMap));
		}

		return in;
	}

}
