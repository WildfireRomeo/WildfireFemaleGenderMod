package com.wildfire.gui.screen;
/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.Screen;

public class WildfireCharacterSettingsScreen extends BaseWildfireScreen {

    private static final Text ENABLED = new TranslatableText("wildfire_gender.label.enabled").formatted(Formatting.GREEN);
    private static final Text DISABLED = new TranslatableText("wildfire_gender.label.disabled").formatted(Formatting.RED);


    private WildfireSlider bounceSlider, floppySlider;
    private Identifier BACKGROUND;
    private float preBounceMult = 0f;
    private float preFloppyMult = 0f;
    private boolean changedSlider = false, changedFloppySlider = false;
    private int yPos = 0;
    boolean enablePhysics, enablePhysicsArmor, enableHurtSounds, enableShowInArmor;
    float bounceMult, floppyMult;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(new TranslatableText("wildfire_gender.char_settings.title"), parent, uuid);
    }


    public boolean shouldPause() { return false; }

    public void init() {
        MinecraftClient m = MinecraftClient.getInstance();
        GenderPlayer aPlr = getPlayer();

        int x = this.width / 2;
        int y = this.height / 2;

        yPos = y - 47;
        enablePhysics = aPlr.hasBreastPhysics;
        enablePhysicsArmor = aPlr.hasArmorBreastPhysics;
        enableShowInArmor = aPlr.showBreastsInArmor;
        enableHurtSounds = aPlr.hurtSounds;
        bounceMult = aPlr.bounceMultiplier;
        floppyMult = aPlr.floppyMultiplier;

        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos, 157, 20, new TranslatableText("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED), button -> {
            enablePhysics ^= true;
            button.setMessage(new TranslatableText("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED));
            aPlr.hasBreastPhysics = enablePhysics;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                renderTooltip(matrices, new TranslatableText("wildfire_gender.tooltip.breast_physics"), mouseX, mouseY);
            }
        }));

        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos + 20, 157, 20, new TranslatableText("wildfire_gender.char_settings.armor_physics", enablePhysics ? ENABLED : DISABLED), button -> {
            enablePhysicsArmor ^= true;
            button.setMessage(new TranslatableText("wildfire_gender.char_settings.armor_physics", enablePhysics ? ENABLED : DISABLED));
            aPlr.hasArmorBreastPhysics = enablePhysicsArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                renderTooltip(matrices, new TranslatableText("wildfire_gender.tooltip.armor_physics"), mouseX, mouseY);
            }
        }));

        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos + 40, 157, 20, new TranslatableText("wildfire_gender.char_settings.hide_in_armor", enablePhysics ? ENABLED : DISABLED), button -> {
            enableShowInArmor ^= true;
            button.setMessage(new TranslatableText("wildfire_gender.char_settings.hide_in_armor", enablePhysics ? ENABLED : DISABLED));
            aPlr.showBreastsInArmor = enableShowInArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                renderTooltip(matrices, new TranslatableText("wildfire_gender.tooltip.hide_in_armor"), mouseX, mouseY);
            }
        }));

        this.addDrawableChild(this.bounceSlider = new WildfireSlider(this.width / 2 - 160/2 + 1, yPos + 60, 157, 20, title, 0.0D, 1.0D, bounceMult) {
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {}

            @Override
            protected void updateMessage() {
                // TODO Auto-generated method stub

            }

            @Override
            protected void applyValue() {
                //GenderPlayer.saveGenderInfo(plr);
            }
        });



        this.addDrawableChild(this.floppySlider = new WildfireSlider(this.width / 2 - 160/2 + 1, yPos + 80, 157, 20, title, 0.0D, 1.0D, floppyMult) {
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {}

            @Override
            protected void updateMessage() {
                // TODO Auto-generated method stub

            }

            @Override
            protected void applyValue() {
                //GenderPlayer.saveGenderInfo(plr);
            }
        });


        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos + 100, 157, 20, new TranslatableText("wildfire_gender.char_settings.hurt_sounds", enableHurtSounds ? ENABLED : DISABLED), button -> {
            enableHurtSounds ^= true;
            button.setMessage(new TranslatableText("wildfire_gender.char_settings.hurt_sounds", enableHurtSounds ? ENABLED : DISABLED));
            aPlr.hurtSounds = enableHurtSounds;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                /*List<Text> list = new ArrayList<Text>();
                list.add(new LiteralText("Enables Custom Hurt Sounds."));
                list.add(new LiteralText(Formatting.RED + "Mod Needed On Server To Work!"));
                RenderSystem.disableDepthTest();
                renderTooltip(matrices, list, mouseX, mouseY);
                RenderSystem.enableDepthTest();*/
            }
        }));

        this.addDrawableChild(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, new LiteralText("X"), button -> {
            MinecraftClient.getInstance().setScreen(parent);
        }));

        this.BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/settings_bg.png");

        super.init();
    }

    public void render(MatrixStack m, int f1, int f2, float f3) {
        super.renderBackground(m);
        PlayerEntity plrEntity = MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(this.BACKGROUND != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.BACKGROUND);
        }
        int i = (this.width - 172) / 2;
        int j = (this.height - 124) / 2;
        drawTexture(m, i, j, 0, 0, 172, 144);

        int x = this.width / 2;
        int y = this.height / 2;

        this.textRenderer.draw(m, title, x - 79, yPos - 10, 4473924);

        //modelRotation = (float)this.rotateSlider.getValue();
        if(preBounceMult != (float) this.bounceSlider.getValue()) {
            bounceMult = (float) this.bounceSlider.getValue();
            preBounceMult = (float) this.bounceSlider.getValue();
            changedSlider = true;
        }
        if(preFloppyMult != (float) this.floppySlider.getValue()) {
            floppyMult = (float) this.floppySlider.getValue();
            preFloppyMult = (float) this.floppySlider.getValue();
            changedFloppySlider = true;
        }

        super.render(m, f1, f2, f3);

        if(plrEntity != null) {
            Screen.drawCenteredText(m, this.textRenderer, plrEntity.getDisplayName().getString(), x, yPos - 30, 0xFFFFFF);
        }

        float bounceText = (bounceMult * 3);
        if (Math.round(bounceText * 10) / 10f == 3) {
            this.textRenderer.draw(m, new TranslatableText("wildfire_gender.slider.max_bounce"), x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else if (Math.round(bounceText * 100) / 100f == 0) {
            this.textRenderer.draw(m, new TranslatableText("wildfire_gender.slider.min_bounce"), x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else {
            this.textRenderer.draw(m, new TranslatableText("wildfire_gender.slider.bounce", Math.round(bounceText * 10) / 10f), x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        }
        this.textRenderer.draw(m, new TranslatableText("wildfire_gender.slider.floppy", Math.round(floppyMult * 100)), x - 72, yPos+87, (this.floppySlider.isMouseOver(f1,  f2) || changedFloppySlider) ? 0xFFFF55: 0xFFFFFF);

        if(Math.round(bounceText * 10) / 10f > 1f) {
            Screen.drawCenteredText(m, textRenderer, new TranslatableText("wildfire_gender.tooltip.bounce_warning").formatted(Formatting.ITALIC), x, y+90, 0xFF6666);
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        MinecraftClient m = MinecraftClient.getInstance();
        GenderPlayer aPlr = getPlayer();

        if(changedSlider) {
            bounceMult = (float) this.bounceSlider.getValue();
            aPlr.bounceMultiplier = bounceMult;
            GenderPlayer.saveGenderInfo(aPlr);
            changedSlider = false;
            bounceSlider.dragging = false;
        }
        if(changedFloppySlider) {
            floppyMult = (float) this.floppySlider.getValue();
            aPlr.floppyMultiplier =  floppyMult;
            GenderPlayer.saveGenderInfo(aPlr);
            changedFloppySlider = false;
            floppySlider.dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }

}