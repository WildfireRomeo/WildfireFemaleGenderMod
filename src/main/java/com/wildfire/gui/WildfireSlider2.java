package com.wildfire.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.Nonnull;

public interface WildfireSlider2 {
    void renderButton(@Nonnull PoseStack mStack, int mouseX, int mouseY, float partial);
}
