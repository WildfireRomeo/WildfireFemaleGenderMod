package com.wildfire.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.Nonnull;

public interface WildfireButton2 {
    void renderButton(@Nonnull PoseStack m, int mouseX, int mouseY, float partialTicks);
}
