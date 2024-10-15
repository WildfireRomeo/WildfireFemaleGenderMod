package com.wildfire.main.entitydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.config.FloatConfigKey;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Function;

/**
 * <p>Data component-like class for storing player breast settings on armor equipped onto armor stands</p>
 *
 * <p>Note that while this is treated similarly to any other {@link DataComponentTypes data component} for performance reasons,
 * this is never written as its own component on item stacks, but instead uses the {@link DataComponentTypes#CUSTOM_DATA custom NBT data component}
 * for compatibility with vanilla clients on servers.</p>
 */
public record BreastDataComponent(float breastSize, float cleavage, Vector3f offsets, boolean jacket, @Nullable NbtComponent nbtComponent) {

	private static Codec<Float> boundedFloat(FloatConfigKey configKey) {
		return Codec.FLOAT.xmap(val -> MathHelper.clamp(val, configKey.getMinInclusive(), configKey.getMaxInclusive()), Function.identity());
	}

	private static final String KEY = "WildfireGender";
	private static final Codec<BreastDataComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			boundedFloat(Configuration.BUST_SIZE)
					.optionalFieldOf("BreastSize", 0f)
					.forGetter(BreastDataComponent::breastSize),
			boundedFloat(Configuration.BREASTS_CLEAVAGE)
					.optionalFieldOf("Cleavage", Configuration.BREASTS_CLEAVAGE.getDefault())
					.forGetter(BreastDataComponent::cleavage),
			Codec.BOOL
					.optionalFieldOf("Jacket", true)
					.forGetter(BreastDataComponent::jacket),
			boundedFloat(Configuration.BREASTS_OFFSET_X)
					.optionalFieldOf("XOffset", 0f)
					.forGetter(component -> component.offsets.x),
			boundedFloat(Configuration.BREASTS_OFFSET_Y)
					.optionalFieldOf("YOffset", 0f)
					.forGetter(component -> component.offsets.y),
			boundedFloat(Configuration.BREASTS_OFFSET_Z)
					.optionalFieldOf("ZOffset", 0f)
					.forGetter(component -> component.offsets.y)
		).apply(instance, (breastSize, cleavage, jacket, x, y, z) -> new BreastDataComponent(breastSize, cleavage, new Vector3f(x, y, z), jacket, null))
	);
	private static final MapCodec<BreastDataComponent> MAP_CODEC = CODEC.fieldOf(KEY);

	public static @Nullable BreastDataComponent fromPlayer(@NotNull PlayerEntity player, @NotNull PlayerConfig config) {
		if(!config.getGender().canHaveBreasts() || !config.showBreastsInArmor()) {
			return null;
		}

		return new BreastDataComponent(config.getBustSize(), config.getBreasts().getCleavage(), config.getBreasts().getOffsets(),
				player.isPartVisible(PlayerModelPart.JACKET), null);
	}

	public static @Nullable BreastDataComponent fromComponent(@Nullable NbtComponent component) {
		if(component == null) {
			return null;
		}

		DataResult<BreastDataComponent> result = component.get(MAP_CODEC);
		if(result.isError()) {
			return null;
		}

		return result.getOrThrow().withComponent(component);
	}

	public void write(RegistryWrapper.WrapperLookup lookup, ItemStack stack) {
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("The provided ItemStack must not be empty");
		}

		RegistryOps<NbtElement> op = lookup.getOps(NbtOps.INSTANCE);
		DataResult<NbtComponent> result = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).with(op, MAP_CODEC, this);
		if(result.isSuccess()) {
			stack.set(DataComponentTypes.CUSTOM_DATA, result.getOrThrow());
		}
	}

	public static void removeFromStack(ItemStack stack) {
		NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
		if(component != null && component.contains(KEY)) {
			NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> nbt.remove(KEY));
		}
	}

	private BreastDataComponent withComponent(NbtComponent component) {
		return new BreastDataComponent(breastSize, cleavage, offsets, jacket, component);
	}
}
