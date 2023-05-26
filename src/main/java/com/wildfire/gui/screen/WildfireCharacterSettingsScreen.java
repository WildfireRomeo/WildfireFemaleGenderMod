/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022 WildfireRomeo

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

package com.wildfire.gui.screen;

import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.Configuration;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.GenderPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class WildfireCharacterSettingsScreen extends BaseWildfireScreen {

    private static final Text ENABLED = Text.translatable("wildfire_gender.label.enabled").formatted(Formatting.GREEN);
    private static final Text DISABLED = Text.translatable("wildfire_gender.label.disabled").formatted(Formatting.RED);

    private WildfireSlider bounceSlider, floppySlider;
    private Identifier BACKGROUND;
    private int yPos = 0;
    private boolean bounceWarning;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(Text.translatable("wildfire_gender.char_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        GenderPlayer aPlr = getPlayer();

        int x = this.width / 2;
        int y = this.height / 2;

        yPos = y - 47;
        int xPos = x - 156 / 2 - 1;

        this.addDrawableChild(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, Text.translatable("wildfire_gender.label.exit"),
                button -> MinecraftClient.getInstance().setScreen(parent)));

        this.addDrawableChild(new WildfireButton(xPos, yPos, 157, 20,
                Text.translatable("wildfire_gender.char_settings.physics", aPlr.hasBreastPhysics() ? ENABLED : DISABLED), button -> {
            boolean enablePhysics = !aPlr.hasBreastPhysics();
            if (aPlr.updateBreastPhysics(enablePhysics)) {
                button.setMessage(Text.translatable("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.of(Text.translatable("wildfire_gender.tooltip.breast_physics"))));

        /*this.addDrawableChild(new WildfireButton(xPos, yPos + 20, 157, 20,
                Text.translatable("wildfire_gender.char_settings.armor_physics", aPlr.hasArmorBreastPhysics() ? ENABLED : DISABLED), button -> {
            boolean enablePhysicsArmor = !aPlr.hasArmorBreastPhysics();
            if (aPlr.updateArmorBreastPhysics(enablePhysicsArmor)) {
                button.setMessage(Text.translatable("wildfire_gender.char_settings.armor_physics", enablePhysicsArmor ? ENABLED : DISABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.of(Text.translatable("wildfire_gender.tooltip.armor_physics"))));*/

        this.addDrawableChild(new WildfireButton(xPos, yPos + 20, 157, 20,
                Text.translatable("wildfire_gender.char_settings.hide_in_armor", aPlr.showBreastsInArmor() ? DISABLED : ENABLED), button -> {
            boolean enableShowInArmor = !aPlr.showBreastsInArmor();
            if (aPlr.updateShowBreastsInArmor(enableShowInArmor)) {
                button.setMessage(Text.translatable("wildfire_gender.char_settings.hide_in_armor", enableShowInArmor ? DISABLED : ENABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.of(Text.translatable("wildfire_gender.tooltip.hide_in_armor"))));

        this.addDrawableChild(this.bounceSlider = new WildfireSlider(xPos, yPos + 40, 158, 22, Configuration.BOUNCE_MULTIPLIER, aPlr.getBounceMultiplierRaw(), value -> {
        }, value -> {
            float bounceText = 3 * value;
            float v = Math.round(bounceText * 10) / 10f;
            bounceWarning = v > 1;
            if (v == 3) {
                return Text.translatable("wildfire_gender.slider.max_bounce");
            } else if (Math.round(bounceText * 100) / 100f == 0) {
                return Text.translatable("wildfire_gender.slider.min_bounce");
            }
            return Text.translatable("wildfire_gender.slider.bounce", v);
        }, value -> {
            if (aPlr.updateBounceMultiplier(value)) {
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }));

        this.addDrawableChild(this.floppySlider = new WildfireSlider(xPos, yPos + 60, 158, 22, Configuration.FLOPPY_MULTIPLIER, aPlr.getFloppiness(), value -> {
        }, value -> Text.translatable("wildfire_gender.slider.floppy", Math.round(value * 100)), value -> {
            if (aPlr.updateFloppiness(value)) {
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }));

        this.addDrawableChild(new WildfireButton(xPos, yPos + 80, 157, 20,
                Text.translatable("wildfire_gender.char_settings.hurt_sounds", aPlr.hasHurtSounds() ? ENABLED : DISABLED), button -> {
            boolean enableHurtSounds = !aPlr.hasHurtSounds();
            if (aPlr.updateHurtSounds(enableHurtSounds)) {
                button.setMessage(Text.translatable("wildfire_gender.char_settings.hurt_sounds", enableHurtSounds ? ENABLED : DISABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.of(Text.translatable("wildfire_gender.tooltip.hurt_sounds"))));

        this.BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/settings_bg.png");

        super.init();
    }

    @Override
    public void render(MatrixStack m, int f1, int f2, float f3) {
        super.renderBackground(m);
        PlayerEntity plrEntity = MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(this.BACKGROUND != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.BACKGROUND);
        }
        int i = (this.width - 172) / 2;
        int j = (this.height - 124) / 2;
        drawTexture(m, i, j, 0, 0, 172, 144);

        int x = this.width / 2;
        int y = this.height / 2;

        this.textRenderer.draw(m, getTitle(), x - 79, yPos - 10, 4473924);

        super.render(m, f1, f2, f3);

        if(plrEntity != null) {
            WildfireHelper.drawCenteredText(m, this.textRenderer, plrEntity.getDisplayName(), x, yPos - 30, 0xFFFFFF);
        }

        if(bounceWarning) {
            WildfireHelper.drawCenteredText(m, this.textRenderer, Text.translatable("wildfire_gender.tooltip.bounce_warning").formatted(Formatting.ITALIC), x, y+90, 0xFF6666);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        //Ensure all sliders are saved
        bounceSlider.save();
        floppySlider.save();
        return super.mouseReleased(mouseX, mouseY, state);
    }
}