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

package com.wildfire.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.config.Configuration;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

    private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider; //rotateSlider
    private WildfireSlider cleavageSlider;

    public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
        super(new TranslatableComponent("wildfire_gender.appearance_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        int j = this.height / 2;

        GenderPlayer plr = getPlayer();
        FloatConsumer onSave = value -> {
            //Just save as we updated the actual value in value change
            GenderPlayer.saveGenderInfo(plr);
        };

        this.addRenderableWidget(new WildfireButton(this.width / 2 + 178, j - 61, 9, 9, new TranslatableComponent("wildfire_gender.label.exit"),
              button -> Minecraft.getInstance().setScreen(parent)));

        this.addRenderableWidget(this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, Configuration.BUST_SIZE, plr,
              value -> new TranslatableComponent("wildfire_gender.wardrobe.slider.breast_size", Math.round(value * 100)), onSave));

        //Customization
        this.addRenderableWidget(this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, Configuration.BREASTS_OFFSET_X, plr,
              value -> new TranslatableComponent("wildfire_gender.wardrobe.slider.separation", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));
        this.addRenderableWidget(this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, Configuration.BREASTS_OFFSET_Y, plr,
              value -> new TranslatableComponent("wildfire_gender.wardrobe.slider.height", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));
        this.addRenderableWidget(this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, Configuration.BREASTS_OFFSET_Z, plr,
              value -> new TranslatableComponent("wildfire_gender.wardrobe.slider.depth", Math.round((Math.round(value * 100f) / 100f) * 10)), onSave));

        this.addRenderableWidget(this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, Configuration.BREASTS_CLEAVAGE, plr,
              value -> new TranslatableComponent("wildfire_gender.wardrobe.slider.rotation", Math.round((Math.round(value * 100f) / 100f) * 100)), onSave));

        this.addRenderableWidget(new WildfireButton(this.width / 2 + 30, j + 57, 158, 20, Configuration.BREASTS_UNIBOOB, plr,
              value -> new TranslatableComponent("wildfire_gender.breast_customization.dual_physics",
                    new TranslatableComponent((Boolean) value ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")), b -> !b));

        super.init();
    }

    @Override
    public void render(@Nonnull PoseStack m, int f1, int f2, float f3) {
        Minecraft minecraft = Minecraft.getInstance();
        GenderPlayer plr = getPlayer();
        super.renderBackground(m);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(plr == null) return;

        try {
            RenderSystem.setShaderColor(1f,1.0F, 1.0F, 1.0F);
            int xP = this.width / 2 - 102;
            int yP = this.height / 2 + 275;
            Player ent = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);
            if(ent != null) {
                WardrobeBrowserScreen.drawEntityOnScreen(xP, yP, 200, (xP), (yP - 76), ent);
            } else {
                //player left, fallback
                minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
            }
        } catch(Exception e) {
            //error, fallback
            minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
        }

        boolean canHaveBreasts = plr.getGender().canHaveBreasts();
        breastSlider.visible = canHaveBreasts;
        xOffsetBoobSlider.visible = canHaveBreasts;
        yOffsetBoobSlider.visible = canHaveBreasts;
        zOffsetBoobSlider.visible = canHaveBreasts;
        cleavageSlider.visible = canHaveBreasts;

        int x = this.width / 2;
        int y = this.height / 2;
        fill(m, x + 28, y - 64, x + 190, y + 79, 0x55000000);
        fill(m, x + 29, y - 63, x + 189, y - 50, 0x55000000);
        this.font.draw(m, title, x + 32, y - 60, 0xFFFFFF);
        super.render(m, f1, f2, f3);
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
