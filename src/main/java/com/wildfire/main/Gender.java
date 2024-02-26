package com.wildfire.main;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

public enum Gender {
    FEMALE(Component.translatable("wildfire_gender.label.female").withStyle(ChatFormatting.LIGHT_PURPLE), true, WildfireSounds.FEMALE_HURT),
    MALE(Component.translatable("wildfire_gender.label.male").withStyle(ChatFormatting.BLUE), false, null),
    OTHER(Component.translatable("wildfire_gender.label.other").withStyle(ChatFormatting.GREEN), true, null);

    private final Component name;
    @Nullable
    private final SoundEvent hurtSound;
    private final boolean canHaveBreasts;

    Gender(Component name, boolean canHaveBreasts, @Nullable SoundEvent hurtSound) {
        this.name = name;
        this.canHaveBreasts = canHaveBreasts;
        this.hurtSound = hurtSound;
    }

    public Component getDisplayName() {
        return name;
    }

    @Nullable
    public SoundEvent getHurtSound() {
        return hurtSound;
    }

    public boolean canHaveBreasts() {
        return canHaveBreasts;
    }
}