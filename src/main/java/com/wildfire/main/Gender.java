package com.wildfire.main;

import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public enum Gender {
	FEMALE(Text.translatable("wildfire_gender.label.female").formatted(Formatting.LIGHT_PURPLE), true, WildfireSounds.FEMALE_HURT),
	MALE(Text.translatable("wildfire_gender.label.male").formatted(Formatting.BLUE), false, null),
	OTHER(Text.translatable("wildfire_gender.label.other").formatted(Formatting.GREEN), true, WildfireSounds.FEMALE_HURT);

	private final Text name;
	private final boolean canHaveBreasts;
	private final @Nullable SoundEvent hurtSound;

	Gender(Text name, boolean canHaveBreasts, @Nullable SoundEvent hurtSound) {
		this.name = name;
		this.canHaveBreasts = canHaveBreasts;
		this.hurtSound = hurtSound;
	}

	public Text getDisplayName() {
		return name;
	}

	public @Nullable SoundEvent getHurtSound() {
		return hurtSound;
	}

	public boolean canHaveBreasts() {
		return canHaveBreasts;
	}
}
