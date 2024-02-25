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

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireBreastPresetList;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.Breasts;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.BreastPresetConfiguration;
import com.wildfire.main.config.ClientConfiguration;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLLoader;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

    private static final float ANGLE = (float) Math.atan(-0.5);
    private static final float PREVIEW_Y_BODY_ROT = 180.0F + ANGLE * 20.0F;
    private static final float PREVIEW_Y_ROT = 180.0F + ANGLE * 40.0F;
    private static final float PREVIEW_X_ROT = -ANGLE * 20.0F;
    private static final Quaternionf CAMERA_ORIENTATION = (new Quaternionf()).rotateX(ANGLE * 20.0F * ((float) Math.PI / 180F));
    private static final Quaternionf PREVIEW_ANGLE = Util.make(new Quaternionf().rotateZ(Mth.PI), preview -> preview.mul(CAMERA_ORIENTATION));

    private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider, cleavageSlider;
    private WildfireButton btnDualPhysics, btnPresets, btnCustomization;
    private WildfireButton btnAddPreset, btnDeletePreset;

    private WildfireBreastPresetList PRESET_LIST;
    private Tab currentTab = Tab.CUSTOMIZATION;

    public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
        super(Component.translatable("wildfire_gender.appearance_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        int j = this.height / 2 - 11;

        GenderPlayer plr = getPlayer();
        Breasts breasts = plr.getBreasts();
        FloatConsumer onSave = value -> {
            //Just save as we updated the actual value in value change
            GenderPlayer.saveGenderInfo(plr);
        };

        this.addRenderableWidget(new WildfireButton(this.width / 2 + 178, j - 72, 9, 9, Component.literal("X"),
              button -> Minecraft.getInstance().setScreen(parent)));

        //Customization Tab
        this.addRenderableWidget(btnCustomization = new WildfireButton(this.width / 2 + 30, j - 60, 158 / 2 - 1, 10,
              Component.translatable("wildfire_gender.breast_customization.tab_customization"), button -> {
            currentTab = Tab.CUSTOMIZATION;
            updatePresetTab();
        }));
        //Presets Tab
        this.addRenderableWidget(btnPresets = new WildfireButton(this.width / 2 + 31 + 79, j - 60, 158 / 2 - 1, 10,
              Component.translatable("wildfire_gender.breast_customization.tab_presets"), button -> {
            // TODO temporary release readiness fix: lock presets tab behind a development environment
            if (FMLLoader.isProduction()) {
                return;
            }

            currentTab = Tab.PRESETS;
            PRESET_LIST.refreshList();
            updatePresetTab();
        }));
        if (FMLLoader.isProduction()) {
            btnPresets.setTooltip(Tooltip.create(Component.translatable("wildfire_gender.coming_soon")));
        }
        this.addRenderableWidget(btnAddPreset = new WildfireButton(this.width / 2 + 31 + 79, j + 80, 158 / 2 - 1, 12,
              Component.translatable("wildfire_gender.breast_customization.presets.add_new"), button -> {
            createNewPreset("Test Preset");
        }));

        this.addRenderableWidget(btnDeletePreset = new WildfireButton(this.width / 2 + 30, j + 80, 158 / 2 - 1, 12,
              Component.translatable("wildfire_gender.breast_customization.presets.delete"), button -> {

        })).active = false;

        //Customization Tab Below

        this.addRenderableWidget(this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, ClientConfiguration.BUST_SIZE, plr.getBustSize(),
              plr::updateBustSize, value -> Component.translatable("wildfire_gender.wardrobe.slider.breast_size", Math.round(value * 125)), onSave));

        //Customization
        this.addRenderableWidget(this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, ClientConfiguration.BREASTS_OFFSET_X, breasts.getXOffset(),
              breasts::updateXOffset, value -> Component.translatable("wildfire_gender.wardrobe.slider.separation", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));
        this.addRenderableWidget(this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, ClientConfiguration.BREASTS_OFFSET_Y, breasts.getYOffset(),
              breasts::updateYOffset, value -> Component.translatable("wildfire_gender.wardrobe.slider.height", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));
        this.addRenderableWidget(this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, ClientConfiguration.BREASTS_OFFSET_Z, breasts.getZOffset(),
              breasts::updateZOffset, value -> Component.translatable("wildfire_gender.wardrobe.slider.depth", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));

        this.addRenderableWidget(this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, ClientConfiguration.BREASTS_CLEAVAGE, breasts.getCleavage(),
              breasts::updateCleavage, value -> Component.translatable("wildfire_gender.wardrobe.slider.rotation", Math.round((Math.round(value * 100f) / 100f) * 100)), onSave));

        this.addRenderableWidget(this.btnDualPhysics = new WildfireButton(this.width / 2 + 30, j + 57, 158, 20,
              Component.translatable("wildfire_gender.breast_customization.dual_physics", Component.translatable(breasts.isUniboob() ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")), button -> {
            boolean isUniboob = !breasts.isUniboob();
            if (breasts.updateUniboob(isUniboob)) {
                button.setMessage(Component.translatable("wildfire_gender.breast_customization.dual_physics", Component.translatable(isUniboob ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")));
                GenderPlayer.saveGenderInfo(plr);
            }
        }));

        //Preset Tab Below
        PRESET_LIST = new WildfireBreastPresetList(this, 156, j - 48, 125);
        PRESET_LIST.setX(this.width / 2 + 30);

        this.addWidget(this.PRESET_LIST);

        this.currentTab = Tab.CUSTOMIZATION;
        //Set default visibilities
        updatePresetTab();

        super.init();
    }

    private void createNewPreset(String presetName) {
        BreastPresetConfiguration cfg = new BreastPresetConfiguration(presetName);
        cfg.set(BreastPresetConfiguration.PRESET_NAME, presetName);
        GenderPlayer player = this.getPlayer();
        cfg.set(BreastPresetConfiguration.BUST_SIZE, player.getBustSize());
        cfg.set(BreastPresetConfiguration.BREASTS_UNIBOOB, player.getBreasts().isUniboob());
        cfg.set(BreastPresetConfiguration.BREASTS_CLEAVAGE, player.getBreasts().getCleavage());
        cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_X, player.getBreasts().getXOffset());
        cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_Y, player.getBreasts().getYOffset());
        cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_Z, player.getBreasts().getZOffset());
        cfg.save();

        PRESET_LIST.refreshList();
    }

    private void updatePresetTab() {
        boolean displayBreastSettings = getPlayer().getGender().canHaveBreasts() && currentTab == Tab.CUSTOMIZATION;
        breastSlider.visible = displayBreastSettings;
        xOffsetBoobSlider.visible = displayBreastSettings;
        yOffsetBoobSlider.visible = displayBreastSettings;
        zOffsetBoobSlider.visible = displayBreastSettings;
        cleavageSlider.visible = displayBreastSettings;
        btnDualPhysics.visible = displayBreastSettings;
        PRESET_LIST.visible = currentTab == Tab.PRESETS;

        btnCustomization.active = currentTab != Tab.CUSTOMIZATION;
        btnPresets.active = currentTab != Tab.PRESETS;
        btnAddPreset.visible = currentTab == Tab.PRESETS;
        btnDeletePreset.visible = currentTab == Tab.PRESETS;
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int x = this.width / 2;
        int y = this.height / 2;
        graphics.fill(x + 28, y - 64 - 21, x + 190, y + 68, 0x55000000);
        graphics.fill(x + 29, y - 63 - 21, x + 189, y - 60, 0x55000000);
        graphics.drawString(font, getTitle(), x + 32, y - 60 - 21, 0xFFFFFF, false);
        if (currentTab == Tab.PRESETS) {
            graphics.fill(PRESET_LIST.getX(), PRESET_LIST.getY(), PRESET_LIST.getRight(), PRESET_LIST.getBottom(), 0x55000000);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.width / 2;
        int y = this.height / 2;
        if (minecraft != null && minecraft.level != null) {
            Player ent = minecraft.level.getPlayerByUUID(this.playerUUID);
            if (ent != null) {
                WildfireHelper.withEntityAngles(ent, PREVIEW_Y_BODY_ROT, PREVIEW_Y_ROT, PREVIEW_X_ROT, entity -> InventoryScreen.renderEntityInInventory(graphics,
                      x - 102, y + 75, 200, new Vector3f(0, entity.getBbHeight() / 2F, 0), PREVIEW_ANGLE, CAMERA_ORIENTATION, entity));
            }
        }

        if (currentTab == Tab.PRESETS) {
            PRESET_LIST.render(graphics, mouseX, mouseY, partialTick);
            if (!PRESET_LIST.hasPresets()) {
                graphics.drawCenteredString(font, Component.translatable("wildfire_gender.breast_customization.presets.none"), x + ((190 + 28) / 2), y - 4, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        //Ensure all sliders are saved
        breastSlider.save();
        xOffsetBoobSlider.save();
        yOffsetBoobSlider.save();
        zOffsetBoobSlider.save();
        cleavageSlider.save();
        return super.mouseReleased(mouseX, mouseY, state);
    }

    private enum Tab {
        CUSTOMIZATION,
        PRESETS
    }
}
