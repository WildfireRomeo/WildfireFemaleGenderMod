/*
    Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
    Copyright (C) 2023 WildfireRomeo

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.gui.screen;

import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.ClientConfiguration;

import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.GenderPlayer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class WildfireCharacterSettingsScreen extends BaseWildfireScreen {

    private static final Component ENABLED = Component.translatable("wildfire_gender.label.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED = Component.translatable("wildfire_gender.label.disabled").withStyle(ChatFormatting.RED);
    private static final ResourceLocation BACKGROUND = WildfireGender.rl("textures/gui/settings_bg.png");

    private WildfireSlider bounceSlider, floppySlider;
    private int yPos = 0;
    private boolean bounceWarning;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(Component.translatable("wildfire_gender.char_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        GenderPlayer aPlr = getPlayer();

        int x = this.width / 2;
        int y = this.height / 2;

        yPos = y - 47;
        int xPos = x - 156 / 2 - 1;

        //Add 'Close' button at beginning
        this.addRenderableWidget(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, Component.translatable("wildfire_gender.label.exit"),
                button -> Minecraft.getInstance().setScreen(parent)));

        this.addRenderableWidget(new WildfireButton(xPos, yPos, 157, 20,
              Component.translatable("wildfire_gender.char_settings.physics", aPlr.hasBreastPhysics() ? ENABLED : DISABLED), button -> {
            boolean enablePhysics = !aPlr.hasBreastPhysics();
            if (aPlr.updateBreastPhysics(enablePhysics)) {
                button.setMessage(Component.translatable("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.create(Component.translatable("wildfire_gender.tooltip.breast_physics"))));

        this.addRenderableWidget(new WildfireButton(xPos, yPos + 20, 157, 20,
              Component.translatable("wildfire_gender.char_settings.hide_in_armor", aPlr.showBreastsInArmor() ? DISABLED : ENABLED), button -> {
            boolean enableShowInArmor = !aPlr.showBreastsInArmor();
            if (aPlr.updateShowBreastsInArmor(enableShowInArmor)) {
                button.setMessage(Component.translatable("wildfire_gender.char_settings.hide_in_armor", enableShowInArmor ? DISABLED : ENABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.create(Component.translatable("wildfire_gender.tooltip.hide_in_armor"))));

        this.addRenderableWidget(new WildfireButton(xPos, yPos + 40, 157, 20,
              Component.translatable("wildfire_gender.char_settings.override_armor_physics", aPlr.getArmorPhysicsOverride() ? ENABLED : DISABLED), button -> {
            boolean enableArmorPhysicsOverride = !aPlr.getArmorPhysicsOverride();
            if (aPlr.updateArmorPhysicsOverride(enableArmorPhysicsOverride)) {
                button.setMessage(Component.translatable("wildfire_gender.char_settings.override_armor_physics", enableArmorPhysicsOverride ? DISABLED : ENABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.create(Component.translatable("wildfire_gender.tooltip.override_armor_physics"))));

        this.addRenderableWidget(this.bounceSlider = new WildfireSlider(xPos, yPos + 60, 157, 20, ClientConfiguration.BOUNCE_MULTIPLIER, aPlr.getBounceMultiplierRaw(), value -> {
        }, value -> {
            float bounceText = 3 * value;
            float v = Math.round(bounceText * 10) / 10f;
            bounceWarning = v > 1;
            if (v == 3) {
                return Component.translatable("wildfire_gender.slider.max_bounce");
            } else if (Math.round(bounceText * 100) / 100f == 0) {
                return Component.translatable("wildfire_gender.slider.min_bounce");
            }
            return Component.translatable("wildfire_gender.slider.bounce", v);
        }, value -> {
            if (aPlr.updateBounceMultiplier(value)) {
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }));

        this.addRenderableWidget(this.floppySlider = new WildfireSlider(xPos, yPos + 80, 157, 20, ClientConfiguration.FLOPPY_MULTIPLIER, aPlr.getFloppiness(), value -> {
        }, value -> Component.translatable("wildfire_gender.slider.floppy", Math.round(value * 100)), value -> {
            if (aPlr.updateFloppiness(value)) {
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }));

        this.addRenderableWidget(new WildfireButton(xPos, yPos + 100, 157, 20,
              Component.translatable("wildfire_gender.char_settings.hurt_sounds", aPlr.hasHurtSounds() ? ENABLED : DISABLED), button -> {
            boolean enableHurtSounds = !aPlr.hasHurtSounds();
            if (aPlr.updateHurtSounds(enableHurtSounds)) {
                button.setMessage(Component.translatable("wildfire_gender.char_settings.hurt_sounds", enableHurtSounds ? ENABLED : DISABLED));
                GenderPlayer.saveGenderInfo(aPlr);
            }
        }, Tooltip.create(Component.translatable("wildfire_gender.tooltip.hurt_sounds"))));

        super.init();
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.blit(BACKGROUND, (this.width - 172) / 2, (this.height - 124) / 2, 0, 0, 172, 144);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);

        int x = this.width / 2;
        int y = this.height / 2;

        graphics.drawString(this.font, title, x - 79, yPos - 10, 4473924, false);

        if (minecraft != null && minecraft.level != null) {
            Player plrEntity = minecraft.level.getPlayerByUUID(this.playerUUID);
            if (plrEntity != null) {
                graphics.drawCenteredString(this.font, plrEntity.getDisplayName(), x, yPos - 30, 0xFFFFFF);
            }
        }

        if (bounceWarning) {
            graphics.drawCenteredString(font, Component.translatable("wildfire_gender.tooltip.bounce_warning").withStyle(ChatFormatting.ITALIC), x, y+90, 0xFF6666);
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