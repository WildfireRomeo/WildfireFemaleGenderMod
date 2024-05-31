package com.wildfire.main.entitydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.FloatConfigKey;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * <p>Record class for storing player breast settings on armor equipped onto armor stands</p>
 *
 * <p>Note that while this is treated similarly to any other {@link net.minecraft.core.component.DataComponentType data component} for performance reasons,
 * this is never written as its own component on item stacks, but instead uses the
 * {@link net.minecraft.core.component.DataComponents#CUSTOM_DATA custom NBT data component} for compatibility with vanilla clients on servers.</p>
 */
public record BreastDataComponent(float breastSize, float cleavage, Vector3f offsets, boolean jacket, @Nullable CustomData nbtComponent) {

    private static final String KEY = "WildfireGender";

    private static Codec<Float> boundedFloat(FloatConfigKey configKey) {
        return Codec.FLOAT.xmap(val -> Mth.clamp(val, configKey.getMinInclusive(), configKey.getMaxInclusive()), Function.identity());
    }

    private static final Codec<BreastDataComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          boundedFloat(ClientConfiguration.BUST_SIZE).optionalFieldOf("BreastSize", 0F).forGetter(BreastDataComponent::breastSize),
          boundedFloat(ClientConfiguration.BREASTS_CLEAVAGE).optionalFieldOf("Cleavage", ClientConfiguration.BREASTS_CLEAVAGE.getDefault()).forGetter(BreastDataComponent::cleavage),
          Codec.BOOL.optionalFieldOf("Jacket", true).forGetter(BreastDataComponent::jacket),
          boundedFloat(ClientConfiguration.BREASTS_OFFSET_X).optionalFieldOf("XOffset", 0F).forGetter(component -> component.offsets.x),
          boundedFloat(ClientConfiguration.BREASTS_OFFSET_Y).optionalFieldOf("YOffset", 0F).forGetter(component -> component.offsets.y),
          boundedFloat(ClientConfiguration.BREASTS_OFFSET_Z).optionalFieldOf("ZOffset", 0F).forGetter(component -> component.offsets.z)
    ).apply(instance, (breastSize, cleavage, jacket, xOffset, yOffset, zOffset) -> new BreastDataComponent(breastSize, cleavage, new Vector3f(xOffset, yOffset, zOffset), jacket, null)));
    private static final MapCodec<BreastDataComponent> MAP_CODEC = CODEC.fieldOf(KEY);

    public static @Nullable BreastDataComponent fromPlayer(@NotNull Player player, @NotNull PlayerConfig config) {
        if (!config.getGender().canHaveBreasts() || !config.showBreastsInArmor()) {
            return null;
        }
        return new BreastDataComponent(config.getBustSize(), config.getBreasts().getCleavage(), config.getBreasts().getOffsets(),
              player.isModelPartShown(PlayerModelPart.JACKET), null);
    }

    public static @Nullable BreastDataComponent fromComponent(@NotNull CustomData component) {
        DataResult<BreastDataComponent> result = component.read(MAP_CODEC);
        if (result.isError()) {
            return null;
        }
        BreastDataComponent parsedData = result.getOrThrow();
        return new BreastDataComponent(parsedData.breastSize, parsedData.cleavage, parsedData.offsets, parsedData.jacket, component);
    }

    public void write(ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("The provided ItemStack must not be empty");
        }
        // see the class javadoc for why we're using the custom data component instead of using this class
        // as its own data component type
        DataResult<CustomData> result = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(MAP_CODEC, this);
        if (result.isSuccess()) {
            //Note: We don't have to handle the case that update normally does of if it is now empty then remove it
            // as we know we have added our own data to it, so it isn't empty
            stack.set(DataComponents.CUSTOM_DATA, result.getOrThrow());
        }
    }

    public static boolean removeFromStack(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.contains(KEY)) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, nbt -> nbt.remove(KEY));
            return true;
        }
        return false;
    }
}