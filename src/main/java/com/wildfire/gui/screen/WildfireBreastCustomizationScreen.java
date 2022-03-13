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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class WildfireBreastCustomizationScreen extends Screen {

    private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider; //rotateSlider
    private WildfireSlider cleavageSlider;

    private float preBreastSize = 0f;
    private float preXOff = 0f, preYOff = 0f, preZOff = 0f;
    private boolean changedBreastSlider = false;
    private boolean changedSliderX, changedSliderY, changedSliderZ;
    private float preCleavage;
    private boolean changedCleavageSlider;

    private UUID playerUUID;
    private Screen parent;

    public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
        super(new TranslatableComponent("wildfire_gender.settings.title"));
        this.playerUUID = uuid;
        this.parent = parent;
    }


    public boolean isPauseScreen() { return false; }


    public void init() {
        Minecraft m = Minecraft.getInstance();
        int j = this.height / 2;


        GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());

        preBreastSize = plr.getBustSize();
        preXOff = plr.getBreasts().xOffset;
        preYOff = plr.getBreasts().yOffset;
        preZOff = plr.getBreasts().zOffset;
        preCleavage = plr.getBreasts().cleavage;


        this.addRenderableWidget(new WildfireButton(this.width / 2 + 178, j - 61, 9, 9, new TextComponent("X"),
              button -> Minecraft.getInstance().setScreen(parent)));


        this.addRenderableWidget(this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, new TextComponent(""), title, 0.0D, 1.0D, plr.getBustSize(), false, false, button -> {
        }, slider -> {
        }));

        //Customization
        this.addRenderableWidget(this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, new TextComponent(""), title, -1.0D, 1.0D, plr.getBreasts().xOffset, false, false, button -> {
        }, slider -> {
        }));
        this.addRenderableWidget(this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, new TextComponent(""), title, -1.0D, 1.0D, plr.getBreasts().yOffset, false, false, button -> {
        }, slider -> {
        }));
        this.addRenderableWidget(this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, new TextComponent(""), title, -1.0D, 0.0D, plr.getBreasts().zOffset, false, false, button -> {
        }, slider -> {
        }));

        if(plr.getBreasts().cleavage > 0.1f) plr.getBreasts().cleavage = 0.1f;



        this.addRenderableWidget(this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, new TextComponent(""), title, 0.0D, 1.0D, plr.getBreasts().cleavage, false,  false, button -> {
        }, slider -> {
        }));

        this.addRenderableWidget(new WildfireButton(this.width / 2 + 30, j + 57, 158, 20, new TextComponent("Dual-Physics: " + (plr.getBreasts().isUniboob?"No":"Yes")), button -> {
            plr.getBreasts().isUniboob ^= true;
            button.setMessage(new TextComponent("Dual-Physics: " + (plr.getBreasts().isUniboob?"No":"Yes")));
            GenderPlayer.saveGenderInfo(plr);

        }));

        super.init();
    }

    public void render(PoseStack m, int f1, int f2, float f3) {
        Minecraft minecraft = Minecraft.getInstance();
        GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());
        super.renderBackground(m);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(plr == null) return;

        try {
            RenderSystem.setShaderColor(1f,1.0F, 1.0F, 1.0F);
            int xP = this.width / 2 - 102;
            int yP = this.height / 2 + 275;
            Player ent = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);
            if(ent != null) {
                WardrobeBrowserScreen.drawEntityOnScreen(xP, yP, 200, (xP), (yP - 76), Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID));
            } else {
                //player left, fallback
                minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
            }
        } catch(Exception e) {
            //error, fallback
            minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
        }

        breastSlider.visible = plr.gender != 1;

        xOffsetBoobSlider.visible = plr.gender != 1;
        yOffsetBoobSlider.visible = plr.gender != 1;
        zOffsetBoobSlider.visible = plr.gender != 1;
        cleavageSlider.visible = plr.gender != 1;

        int x = this.width / 2;
        int y = this.height / 2;
        fill(m, x + 28, y - 64, x + 190, y + 79, 0x55000000);
        fill(m, x + 29, y - 63, x + 189, y - 50, 0x55000000);
        this.font.draw(m, new TextComponent("Appearance Settings"), x + 32, y - 60, 0xFFFFFF);


        if(preBreastSize != (float) this.breastSlider.getValue()) {
            plr.updateBustSize((float) this.breastSlider.getValue());
            preBreastSize = (float) this.breastSlider.getValue();
            changedBreastSlider = true;
        }
        if(preXOff != (float) this.xOffsetBoobSlider.getValue()) {
            plr.getBreasts().xOffset = ((float) this.xOffsetBoobSlider.getValue());
            preXOff = (float) this.xOffsetBoobSlider.getValue();
            changedSliderX = true;
        }
        if(preYOff != (float) this.yOffsetBoobSlider.getValue()) {
            plr.getBreasts().yOffset = ((float) this.yOffsetBoobSlider.getValue());
            preYOff = (float) this.yOffsetBoobSlider.getValue();
            changedSliderY = true;
        }
        if(preZOff != (float) this.zOffsetBoobSlider.getValue()) {
            plr.getBreasts().zOffset = ((float) this.zOffsetBoobSlider.getValue());
            preZOff = (float) this.zOffsetBoobSlider.getValue();
            changedSliderZ = true;
        }
        if(preCleavage != (float) this.cleavageSlider.getValue()) {
            plr.getBreasts().cleavage = ((float) this.cleavageSlider.getValue()) / 10f;
            preCleavage = (float) this.cleavageSlider.getValue() / 10f;
            changedCleavageSlider = true;
        }
        super.render(m, f1, f2, f3);

        if(breastSlider.visible) this.font.draw(m, new TranslatableComponent("wildfire_gender.wardrobe.slider.breast_size").getString() + ": " + Math.round(plr.getBustSize() * 100) + "%", x + 36, y - 42, (this.breastSlider.isMouseOver(f1,  f2) || changedBreastSlider) ? 0xFFFF55: 0xFFFFFF);
        if(xOffsetBoobSlider.visible) this.font.draw(m, "Separation: " + Math.round((Math.round(plr.getBreasts().xOffset * 100f) / 100f) * 10) + "", x + 36, y - 21, (this.xOffsetBoobSlider.isMouseOver(f1,  f2) || changedSliderX) ? 0xFFFF55: 0xFFFFFF);
        if(yOffsetBoobSlider.visible) this.font.draw(m, "Height: " + Math.round((Math.round(plr.getBreasts().yOffset * 100f) / 100f) * 10) + "", x + 36, y, (this.yOffsetBoobSlider.isMouseOver(f1,  f2) || changedSliderY) ? 0xFFFF55: 0xFFFFFF);
        if(zOffsetBoobSlider.visible) this.font.draw(m, "Depth: " + Math.round((Math.round(plr.getBreasts().zOffset * 100f) / 100f) * 10) + "", x + 36, y + 21, (this.zOffsetBoobSlider.isMouseOver(f1,  f2) || changedSliderZ) ? 0xFFFF55: 0xFFFFFF);
        if(cleavageSlider.visible) this.font.draw(m, "Rotation: " + Math.round((Math.round(plr.getBreasts().cleavage * 100f) / 100f) * 100) + " degrees", x + 36, y + 42, (this.cleavageSlider.isMouseOver(f1,  f2) || changedCleavageSlider) ? 0xFFFF55: 0xFFFFFF);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());
        if(changedBreastSlider) {
            plr.updateBustSize((float) this.breastSlider.getValue());
            changedBreastSlider = false;
            breastSlider.dragging = false;
            GenderPlayer.saveGenderInfo(plr);
            //System.out.println("Changed");
        }
        if(changedSliderX) {
            plr.getBreasts().xOffset = (float) this.xOffsetBoobSlider.getValue();
            changedSliderX = false;
            xOffsetBoobSlider.dragging = false;
            GenderPlayer.saveGenderInfo(plr);
        }
        if(changedSliderY) {
            plr.getBreasts().yOffset = (float) this.yOffsetBoobSlider.getValue();
            changedSliderY = false;
            yOffsetBoobSlider.dragging = false;
            GenderPlayer.saveGenderInfo(plr);
        }
        if(changedSliderZ) {
            plr.getBreasts().zOffset = (float) this.zOffsetBoobSlider.getValue();
            changedSliderZ = false;
            zOffsetBoobSlider.dragging = false;
            GenderPlayer.saveGenderInfo(plr);
        }
        if(changedCleavageSlider) {
            plr.getBreasts().cleavage = (float) this.cleavageSlider.getValue() / 10f;
            changedCleavageSlider = false;
            cleavageSlider.dragging = false;
            GenderPlayer.saveGenderInfo(plr);
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }
}
