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
import com.wildfire.main.config.Configuration;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

    private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider, cleavageSlider;
    private WildfireButton btnDualPhysics, btnPresets, btnCustomization;

    private WildfireBreastPresetList PRESET_LIST;

    public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
        super(Text.translatable("wildfire_gender.appearance_settings.title"), parent, uuid);
    }

    private int currentTab = 0; // 0 = customization, 1 = presets
    @Override
    public void init() {
        int j = this.height / 2 - 11;

        GenderPlayer plr = getPlayer();
        Breasts breasts = plr.getBreasts();
        FloatConsumer onSave = value -> {
            //Just save as we updated the actual value in value change
            GenderPlayer.saveGenderInfo(plr);
        };

        this.addDrawableChild(new WildfireButton(this.width / 2 + 178, j - 72, 9, 9, Text.translatable("wildfire_gender.label.exit"),
              button -> MinecraftClient.getInstance().setScreen(parent)));

        //Customization Tab
        this.addDrawableChild(btnCustomization = new WildfireButton(this.width / 2 + 30, j - 60, 158 / 2 - 1, 10,
                Text.translatable("wildfire_gender.breast_customization.tab_customization"), button -> {
            currentTab = 0;
            btnCustomization.active = false;
            btnPresets.active = true;

        }).setActive(false));

        //Presets Tab
        this.addDrawableChild(btnPresets = new WildfireButton(this.width / 2 + 31 + 158/2, j - 60, 158 / 2 - 1, 10,
                Text.translatable("wildfire_gender.breast_customization.tab_presets"), button -> {
            currentTab = 1;
            btnCustomization.active = true;
            btnPresets.active = false;
        }));

        //Preset Tab Below
        PRESET_LIST = new WildfireBreastPresetList(this, 54, (j - 40), (j + 89));
        PRESET_LIST.setRenderBackground(false);
        PRESET_LIST.setRenderHorizontalShadows(false);
        //PLAYER_LIST.refreshList();
        this.addSelectableChild(this.PRESET_LIST);




        //Customization Tab Below
        this.addDrawableChild(this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, Configuration.BUST_SIZE, plr.getBustSize(),
              plr::updateBustSize, value -> Text.translatable("wildfire_gender.wardrobe.slider.breast_size", Math.round(value * 1.25f * 100)), onSave));

        //Customization
        this.addDrawableChild(this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, Configuration.BREASTS_OFFSET_X, breasts.getXOffset(),
              breasts::updateXOffset, value -> Text.translatable("wildfire_gender.wardrobe.slider.separation", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));
        this.addDrawableChild(this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, Configuration.BREASTS_OFFSET_Y, breasts.getYOffset(),
              breasts::updateYOffset, value -> Text.translatable("wildfire_gender.wardrobe.slider.height", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));
        this.addDrawableChild(this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, Configuration.BREASTS_OFFSET_Z, breasts.getZOffset(),
              breasts::updateZOffset, value -> Text.translatable("wildfire_gender.wardrobe.slider.depth", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));

        this.addDrawableChild(this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, Configuration.BREASTS_CLEAVAGE, breasts.getCleavage(),
              breasts::updateCleavage, value -> Text.translatable("wildfire_gender.wardrobe.slider.rotation", Math.round((Math.round(value * 100f) / 100f) * 100)), onSave));

        this.addDrawableChild(this.btnDualPhysics =new WildfireButton(this.width / 2 + 30, j + 57, 158, 20,
                Text.translatable("wildfire_gender.breast_customization.dual_physics", Text.translatable(breasts.isUniboob() ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")), button -> {
            boolean isUniboob = !breasts.isUniboob();
            if (breasts.updateUniboob(isUniboob)) {
                button.setMessage(Text.translatable("wildfire_gender.breast_customization.dual_physics", Text.translatable(isUniboob ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")));
                GenderPlayer.saveGenderInfo(plr);
            }
        }));

        super.init();
    }

    @Override
    public void render(DrawContext ctx, int f1, int f2, float f3) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        GenderPlayer plr = getPlayer();
        super.renderBackground(ctx);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(plr == null) return;

        try {
            RenderSystem.setShaderColor(1f,1.0F, 1.0F, 1.0F);
            int xP = this.width / 2 - 102;
            int yP = this.height / 2 + 275;
            //noinspection DataFlowIssue
            PlayerEntity ent = minecraft.world.getPlayerByUuid(this.playerUUID);
            if(ent != null) {
                WardrobeBrowserScreen.drawEntityOnScreen(xP, yP, 200, -20, -20, ent);
            } else {
                //player left, fallback
                minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
            }
        } catch(Exception e) {
            //error, fallback
            minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
        }

        boolean canHaveBreasts = plr.getGender().canHaveBreasts();
        breastSlider.visible = canHaveBreasts && currentTab == 0;
        xOffsetBoobSlider.visible = canHaveBreasts && currentTab == 0;
        yOffsetBoobSlider.visible = canHaveBreasts && currentTab == 0;
        zOffsetBoobSlider.visible = canHaveBreasts && currentTab == 0;
        cleavageSlider.visible = canHaveBreasts && currentTab == 0;
        btnDualPhysics.visible = canHaveBreasts && currentTab == 0;

        int x = this.width / 2;
        int y = this.height / 2;
        ctx.fill(x + 28, y - 64 - 21, x + 190, y + 79, 0x55000000);
        ctx.fill(x + 29, y - 63 - 21, x + 189, y - 50, 0x55000000);
        ctx.drawText(textRenderer, getTitle(), x + 32, y - 60 - 21, 0xFFFFFF, false);
        super.render(ctx, f1, f2, f3);

        if(currentTab == 0) {

        }
        if(currentTab == 1) {
            PRESET_LIST.render(ctx, f1, f2, f3);
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
}
