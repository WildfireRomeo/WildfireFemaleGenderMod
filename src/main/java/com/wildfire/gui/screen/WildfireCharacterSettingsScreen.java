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

    private WildfireSlider bounceSlider, floppySlider;
    private boolean bounceWarning;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(Text.translatable("wildfire_gender.char_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        // NOTE: buttons/sliders do not need to have a set X/Y position, as super.init() will automatically reposition them
        PlayerConfig aPlr = getPlayer();

        this.addDrawableChild(new WildfireButton(0, 0, WIDTH, HEIGHT,
                Text.translatable("wildfire_gender.char_settings.physics", aPlr.hasBreastPhysics() ? ENABLED : DISABLED), button -> {
            boolean enablePhysics = !aPlr.hasBreastPhysics();
            if (aPlr.updateBreastPhysics(enablePhysics)) {
                button.setMessage(Text.translatable("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED));
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

        this.addDrawableChild(new WildfireButton(0, 0, WIDTH, HEIGHT,
                Text.translatable("wildfire_gender.char_settings.hide_in_armor", aPlr.showBreastsInArmor() ? DISABLED : ENABLED), button -> {
            boolean enableShowInArmor = !aPlr.showBreastsInArmor();
            if (aPlr.updateShowBreastsInArmor(enableShowInArmor)) {
                button.setMessage(Text.translatable("wildfire_gender.char_settings.hide_in_armor", enableShowInArmor ? DISABLED : ENABLED));
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

        this.addDrawableChild(this.bounceSlider = new WildfireSlider(0, 0, WIDTH + 1, HEIGHT, Configuration.BOUNCE_MULTIPLIER, aPlr.getBounceMultiplier(), value -> {
        }, value -> {
            float bounceText = 3 * value;
            int v = Math.round(bounceText * 100);
            bounceWarning = v > 100;
            return Text.translatable("wildfire_gender.slider.bounce", v);
        }, value -> {
            if (aPlr.updateBounceMultiplier(value)) {
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

        this.addDrawableChild(this.floppySlider = new WildfireSlider(0, 0, WIDTH + 1, HEIGHT, Configuration.FLOPPY_MULTIPLIER, aPlr.getFloppiness(), value -> {
        }, value -> Text.translatable("wildfire_gender.slider.floppy", Math.round(value * 100)), value -> {
            if (aPlr.updateFloppiness(value)) {
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

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
                WildfireHelper.drawCenteredText(ctx, this.textRenderer, plrEntity.getDisplayName(), this.width / 2, getTopY() - 30, 0xFFFFFF);
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        if(bounceWarning) {
            WildfireHelper.drawCenteredText(ctx, this.textRenderer, Text.translatable("wildfire_gender.tooltip.bounce_warning").formatted(Formatting.ITALIC), this.width / 2, getBottomY() + 30, 0xFF6666);
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