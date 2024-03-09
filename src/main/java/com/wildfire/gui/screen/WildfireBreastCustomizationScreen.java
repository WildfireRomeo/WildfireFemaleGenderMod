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

import com.wildfire.gui.WildfireBreastPresetList;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.config.BreastPresetConfiguration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

    @SuppressWarnings("unused")
    private @Nullable WildfireButton btnDeletePreset;
    private @Nullable WildfireBreastPresetList PRESET_LIST;

    private Tab currentTab = Tab.CUSTOMIZATION;

    public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
        super(Text.translatable("wildfire_gender.appearance_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        final int x = this.width / 2 + 30;
        final int y = this.height / 2 - 11;
        PRESET_LIST = null;
        btnDeletePreset = null;

        this.addDrawableChild(WildfireButton.builder()
                .position(x, y - 60)
                .size(158 / 2 - 1, 10)
                .text(Text.translatable("wildfire_gender.breast_customization.tab_customization"))
                .onClick(button -> {
                    currentTab = Tab.CUSTOMIZATION;
                    clearAndInit();
                })
                .deselected(currentTab != Tab.CUSTOMIZATION)
                .highlightBackground(currentTab == Tab.CUSTOMIZATION)
                .build());

        this.addDrawableChild(WildfireButton.builder()
                .position(x + 158 / 2, y - 60)
                .size(158 / 2 - 1, 10)
                .text(Text.translatable("wildfire_gender.breast_customization.tab_presets"))
                .onClick(button -> {
                    // TODO temporary release readiness fix: lock presets tab behind a development environment (-Dfabric.development=true)
                    if(!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

                    currentTab = Tab.PRESETS;
                    clearAndInit();
                })
                .require(FabricLoader.getInstance().isDevelopmentEnvironment(), Tooltip.of(Text.translatable("wildfire_gender.coming_soon")))
                .deselected(currentTab != Tab.PRESETS)
                .highlightBackground(currentTab == Tab.PRESETS)
                .build());

        if(currentTab == Tab.CUSTOMIZATION) initCustomization();
        else initPresets();

        this.addDrawableChild(WildfireButton.builder()
                .text(Text.literal("X"))
                .narrationSupplier(narration -> Text.translatable("gui.narrate.button", Text.translatable("gui.back")))
                .scrollableText(false)
                .position(this.width / 2 + 178, y - 72)
                .size(9, 9)
                .closes(this)
                .build());

        super.init();
    }

    private void initCustomization() {
        final int x = this.width / 2 + 30;
        final int y = this.height / 2 - 11;
        final int width = 158;
        final int height = 20;
        final PlayerConfig plr = getPlayer();
        final Breasts breasts = plr.getBreasts();

        this.addDrawableChild(WildfireSlider.builder()
                .position(x, y - 48)
                .size(width, height)
                .text(value -> Text.translatable("wildfire_gender.wardrobe.slider.breast_size", Math.round(value * 1.25f * 100)))
                .configKey(Configuration.BUST_SIZE)
                .current(plr.getBustSize())
                .update(plr::updateBustSize)
                .save(this::save)
                .build());

        this.addDrawableChild(WildfireSlider.builder()
                .position(x, y - 27)
                .size(width, height)
                .text(value -> Text.translatable("wildfire_gender.wardrobe.slider.separation", Math.round((Math.round(value * 100f) / 100f) * 10)))
                .configKey(Configuration.BREASTS_OFFSET_X)
                .current(breasts.getXOffset())
                .update(breasts::updateXOffset)
                .save(this::save)
                .build());
        this.addDrawableChild(WildfireSlider.builder()
                .position(x, y - 6)
                .size(width, height)
                .text(value -> Text.translatable("wildfire_gender.wardrobe.slider.height", Math.round((Math.round(value * 100f) / 100f) * 10)))
                .configKey(Configuration.BREASTS_OFFSET_Y)
                .current(breasts.getYOffset())
                .update(breasts::updateYOffset)
                .save(this::save)
                .build());
        this.addDrawableChild(WildfireSlider.builder()
                .position(x, y + 15)
                .size(width, height)
                .text(value -> Text.translatable("wildfire_gender.wardrobe.slider.depth", Math.round((Math.round(value * 100f) / 100f) * 10)))
                .configKey(Configuration.BREASTS_OFFSET_Z)
                .current(breasts.getZOffset())
                .update(breasts::updateZOffset)
                .save(this::save)
                .build());

        this.addDrawableChild(WildfireSlider.builder()
                .position(x, y + 36)
                .size(width, height)
                .text(value -> Text.translatable("wildfire_gender.wardrobe.slider.rotation", Math.round((Math.round(value * 100f) / 100f) * 100)))
                .configKey(Configuration.BREASTS_CLEAVAGE)
                .current(breasts.getCleavage())
                .update(breasts::updateCleavage)
                .save(this::save)
                .build());

        this.addDrawableChild(WildfireButton.builder()
                .position(x, y + 57)
                .size(width, height)
                .textSupplier(() -> Text.translatable("wildfire_gender.breast_customization.dual_physics", Text.translatable(breasts.isUniboob() ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")))
                .onClick(button -> {
                    breasts.updateUniboob(!breasts.isUniboob());
                    PlayerConfig.saveGenderInfo(plr);
                })
                .build());
    }

    private void initPresets() {
        final int x = this.width / 2 + 30;
        final int y = this.height / 2 - 11;
        final int width = 158;
        final int height = 12;

        this.addDrawableChild(WildfireButton.builder()
                .position(x + 1 + width / 2, y + 80)
                .size(width / 2 + 1, height)
                .text(Text.translatable("wildfire_gender.breast_customization.presets.add_new"))
                .onClick(button -> {
                    createNewPreset("Test Preset");
                })
                .build());

        this.addDrawableChild(btnDeletePreset = WildfireButton.builder()
                .position(x, y + 80)
                .size(width / 2 - 1, height)
                .text(Text.translatable("wildfire_gender.breast_customization.presets.delete"))
                .onClick(button -> {
                    // TODO
                })
                .active(false)
                .build());

        PRESET_LIST = new WildfireBreastPresetList(this, 156, (y - 48));
        PRESET_LIST.setX(this.width / 2 + 30);
        PRESET_LIST.setHeight(125);
        this.addSelectableChild(this.PRESET_LIST);
    }

    @SuppressWarnings("SameParameterValue")
    private void createNewPreset(String presetName) {
        BreastPresetConfiguration cfg = new BreastPresetConfiguration(presetName);
        cfg.set(BreastPresetConfiguration.PRESET_NAME, presetName);
        cfg.set(BreastPresetConfiguration.BUST_SIZE, this.getPlayer().getBustSize());
        cfg.set(BreastPresetConfiguration.BREASTS_UNIBOOB, this.getPlayer().getBreasts().isUniboob());
        cfg.set(BreastPresetConfiguration.BREASTS_CLEAVAGE, this.getPlayer().getBreasts().getCleavage());
        cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_X, this.getPlayer().getBreasts().getXOffset());
        cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_Y, this.getPlayer().getBreasts().getYOffset());
        cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_Z, this.getPlayer().getBreasts().getZOffset());
        cfg.save();

        if(PRESET_LIST != null) PRESET_LIST.refreshList();
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        int x = this.width / 2;
        int y = this.height / 2;
        ctx.fill(x + 28, y - 64 - 21, x + 190, y + 68, 0x55000000);
        ctx.fill(x + 29, y - 63 - 21, x + 189, y - 60, 0x55000000);
        ctx.drawText(textRenderer, getTitle(), x + 32, y - 60 - 21, 0xFFFFFF, false);
        if(currentTab == Tab.PRESETS && PRESET_LIST != null) {
            ctx.fill(PRESET_LIST.getX(), PRESET_LIST.getY(), PRESET_LIST.getRight(), PRESET_LIST.getBottom(), 0x55000000);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if(client == null || client.player == null || client.world == null) return;
        super.render(ctx, mouseX, mouseY, delta);

        int xP = this.width / 2 - 102;
        int yP = this.height / 2 + 275;
        PlayerEntity ent = client.world.getPlayerByUuid(this.playerUUID);
        if(ent != null) WardrobeBrowserScreen.drawEntityOnScreen(xP, yP, 200, -20, -20, ent);

        int x = this.width / 2;
        int y = this.height / 2;
        if(currentTab == Tab.PRESETS && PRESET_LIST != null) {
            PRESET_LIST.render(ctx, mouseX, mouseY, delta);
            if(PRESET_LIST.getPresetList().length == 0) {
                ctx.drawText(textRenderer, "No Presets Found", x + ((190 + 28) / 2) - textRenderer.getWidth("No Presets Found") / 2, y - 4, 0xFFFFFF, false);
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        PlayerConfig.saveGenderInfo(getPlayer());
        return super.mouseReleased(mouseX, mouseY, state);
    }

    private enum Tab {
        CUSTOMIZATION, PRESETS
    }
}
