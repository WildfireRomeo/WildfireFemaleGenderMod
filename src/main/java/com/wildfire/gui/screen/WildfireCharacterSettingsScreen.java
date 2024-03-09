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

import com.wildfire.gui.GuiUtils;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.Configuration;

import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class WildfireCharacterSettingsScreen extends DynamicallySizedScreen {

    private static final Text ENABLED = Text.translatable("wildfire_gender.label.enabled").formatted(Formatting.GREEN);
    private static final Text DISABLED = Text.translatable("wildfire_gender.label.disabled").formatted(Formatting.RED);

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(Text.translatable("wildfire_gender.char_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        // NOTE: buttons/sliders do not need to have a set X/Y position, as super.init() will automatically reposition them
        PlayerConfig aPlr = getPlayer();

        this.addDrawableChild(WildfireButton.builder()
                .size(WIDTH, HEIGHT)
                .textSupplier(() -> Text.translatable("wildfire_gender.char_settings.physics", aPlr.hasBreastPhysics() ? ENABLED : DISABLED))
                .onClick(button -> {
                    aPlr.updateBreastPhysics(!aPlr.hasBreastPhysics());
                    save();
                })
                .require(ClientConfiguration.ENABLE_BREAST_RENDERING)
                .build());

        this.addDrawableChild(WildfireButton.builder()
                .size(WIDTH, HEIGHT)
                .textSupplier(() -> Text.translatable("wildfire_gender.char_settings.hide_in_armor", aPlr.showBreastsInArmor() ? DISABLED : ENABLED))
                .onClick(button -> {
                    aPlr.updateShowBreastsInArmor(!aPlr.showBreastsInArmor());
                    save();
                })
                .require(ClientConfiguration.ENABLE_BREAST_RENDERING)
                .build());

        this.addDrawableChild(WildfireSlider.builder()
                .size(WIDTH + 1, HEIGHT)
                .configKey(Configuration.BOUNCE_MULTIPLIER)
                .current(aPlr.getBounceMultiplier())
                .text(value -> Text.translatable("wildfire_gender.slider.bounce", Math.round(3 * value * 100)))
                .update(aPlr::updateBounceMultiplier)
                .save(this::save)
                .require(ClientConfiguration.ENABLE_BREAST_RENDERING)
                .build());

        this.addDrawableChild(WildfireSlider.builder()
                .size(WIDTH + 1, HEIGHT)
                .configKey(Configuration.FLOPPY_MULTIPLIER)
                .current(aPlr.getFloppiness())
                .text(value -> Text.translatable("wildfire_gender.slider.floppy", Math.round(value * 100)))
                .update(aPlr::updateFloppiness)
                .save(this::save)
                .require(ClientConfiguration.ENABLE_BREAST_RENDERING)
                .build());

        this.addDrawableChild(WildfireButton.builder()
                .textSupplier(() -> Text.translatable("wildfire_gender.char_settings.hurt_sounds", aPlr.hasHurtSounds() ? ENABLED : DISABLED))
                .size(WIDTH, HEIGHT)
                .onClick(button -> {
                    aPlr.updateHurtSounds(!aPlr.hasHurtSounds());
                    PlayerConfig.saveGenderInfo(aPlr);
                })
                .tooltip(Tooltip.of(Text.translatable("wildfire_gender.tooltip.hurt_sounds")))
                .require(ClientConfiguration.ENABLE_GENDER_HURT_SOUNDS)
                .build());

        this.addDrawableChild(WildfireButton.builder()
                .text(Text.literal("X"))
                .narrationSupplier(narrationText -> Text.translatable("gui.narrate.button", Text.translatable("gui.back")))
                .scrollableText(false)
                .size(9, 9)
                .closes(this)
                .build());

        super.init();
    }

    @Override
    protected void drawTitle(DrawContext ctx, int x, int y) {
        ctx.drawText(textRenderer, getTitle(), x, y, 4473924, false);
        if(client != null && client.world != null) {
            PlayerEntity plrEntity = client.world.getPlayerByUuid(this.playerUUID);
            if(plrEntity != null) {
                GuiUtils.drawCenteredText(ctx, this.textRenderer, plrEntity.getDisplayName(), this.width / 2, getTopY() - 30, 0xFFFFFF);
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        if(3 * getPlayer().getBounceMultiplier() * 100 > 100) {
            GuiUtils.drawCenteredText(ctx, this.textRenderer, Text.translatable("wildfire_gender.tooltip.bounce_warning").formatted(Formatting.ITALIC), this.width / 2, getBottomY() + 30, 0xFF6666);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        children().forEach(element -> {
            if(element instanceof WildfireSlider slider) {
                slider.save();
            }
        });
        return super.mouseReleased(mouseX, mouseY, state);
    }

    private void save() {
        PlayerConfig.saveGenderInfo(getPlayer());
    }

    private void save(Object ignored) {
        save();
    }
}