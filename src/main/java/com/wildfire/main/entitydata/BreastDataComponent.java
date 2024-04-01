package com.wildfire.main.entitydata;

import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.Configuration;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * <p>Record class for storing player breast settings on armor equipped onto armor stands</p>
 *
 * <p>Note that while this is treated similarly to any other {@link DataComponentTypes data component} for performance reasons,
 * this is never written as its own component on item stacks, but instead uses the {@link DataComponentTypes#CUSTOM_DATA custom NBT data component}
 * for compatibility with vanilla clients on servers.</p>
 */
public record BreastDataComponent(float breastSize, float cleavage, Vector3f offsets, boolean jacket, @Nullable NbtComponent nbtComponent) {
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

		@SuppressWarnings("deprecation") NbtCompound root = component.getNbt();
		if(!root.contains("WildfireGender", NbtElement.COMPOUND_TYPE)) {
			return null;
		}
		NbtCompound nbt = root.getCompound("WildfireGender");

		float breastSize = WildfireHelper.readNbt(nbt, "BreastSize", Configuration.BUST_SIZE).orElse(0f);
		float cleavage = WildfireHelper.readNbt(nbt, "Cleavage", Configuration.BREASTS_CLEAVAGE).orElseGet(Configuration.BREASTS_CLEAVAGE::getDefault);
		boolean jacket = WildfireHelper.readNbt(nbt, "Jacket", nbt::getBoolean).orElse(true);
		Vector3f offsets = new Vector3f(
				WildfireHelper.readNbt(nbt, "XOffset", Configuration.BREASTS_OFFSET_X).orElse(0f),
				WildfireHelper.readNbt(nbt, "YOffset", Configuration.BREASTS_OFFSET_Y).orElse(0f),
				WildfireHelper.readNbt(nbt, "ZOffset", Configuration.BREASTS_OFFSET_Z).orElse(0f));

		return new BreastDataComponent(breastSize, cleavage, offsets, jacket, component);
	}

	public void write(ItemStack stack) {
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("The provided ItemStack must not be empty");
		}
		NbtCompound nbt = new NbtCompound();
		nbt.putFloat("BreastSize", breastSize);
		nbt.putFloat("Cleavage", cleavage);
		nbt.putFloat("XOffset", offsets.x);
		nbt.putFloat("YOffset", offsets.y);
		nbt.putFloat("ZOffset", offsets.z);
		nbt.putBoolean("Jacket", jacket);
		// see the class javadoc for why we're using the custom data component instead of using this class
		// as its own data component type
		NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, stackNbt -> stackNbt.put("WildfireGender", nbt));
	}
}
