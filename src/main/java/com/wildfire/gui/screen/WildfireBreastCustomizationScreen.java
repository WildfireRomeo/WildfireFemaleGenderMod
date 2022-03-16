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
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.UUID;

public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

    private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider; //rotateSlider
    private WildfireSlider cleavageSlider;

    private float preBreastSize = 0f;
    private float preXOff = 0f, preYOff = 0f, preZOff = 0f;
    private boolean changedBreastSlider = false;
    private boolean changedSliderX, changedSliderY, changedSliderZ;
    private float preCleavage;
    private boolean changedCleavageSlider;

    public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
        super(new TranslatableText("wildfire_gender.appearance_settings.title"), parent, uuid);
    }

    public void init() {
        MinecraftClient m = MinecraftClient.getInstance();
        int j = this.height / 2;

        GenderPlayer plr = getPlayer();

        preBreastSize = plr.getBustSize();
        preXOff = plr.getBreasts().xOffset;
        preYOff = plr.getBreasts().yOffset;
        preZOff = plr.getBreasts().zOffset;
        preCleavage = plr.getBreasts().cleavage;


        this.addDrawableChild(new WildfireButton(this.width / 2 + 178, j - 61, 9, 9, new TranslatableText("wildfire_gender.label.exit"), button -> {
            MinecraftClient.getInstance().setScreen(parent);
        }));


        this.addDrawableChild(this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, title, 0.0D, 1.0D, plr.getBustSize()) {

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

        //Customization
        this.addDrawableChild(this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, title, -1.0D, 1.0D, plr.getBreasts().xOffset) {
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {}

            @Override
            protected void updateMessage() {

            }

            @Override
            protected void applyValue() {
                //GenderPlayer.saveGenderInfo(plr);
            }
        });
        this.addDrawableChild(this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, title, -1.0D, 1.0D, plr.getBreasts().yOffset) {
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
        this.addDrawableChild(this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, title, -1.0D, 0.0D, plr.getBreasts().zOffset) {

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

        if(plr.getBreasts().cleavage > 0.1f) plr.getBreasts().cleavage = 0.1f;

        this.addDrawableChild(this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, title, 0.0D, 0.1D, plr.getBreasts().cleavage) {
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
            }
        );

        this.addDrawableChild(new WildfireButton(this.width / 2 + 30, j + 57, 158, 20, new TranslatableText("wildfire_gender.breast_customization.dual_physics", new TranslatableText(plr.getBreasts().isUniboob ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")), button -> {
            plr.getBreasts().isUniboob ^= true;
            button.setMessage(new TranslatableText("wildfire_gender.breast_customization.dual_physics", new TranslatableText(plr.getBreasts().isUniboob ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")));
            GenderPlayer.saveGenderInfo(plr);
        }));

        super.init();
    }

    public void render(MatrixStack m, int f1, int f2, float f3) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        GenderPlayer plr = getPlayer();
        super.renderBackground(m);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(plr == null) return;

        try {
            RenderSystem.setShaderColor(1f,1.0F, 1.0F, 1.0F);
            int xP = this.width / 2 - 102;
            int yP = this.height / 2 + 275;
            PlayerEntity ent = MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID);
            if(ent != null) {
                drawEntity(xP, yP, 200, (xP), (yP - 76), MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID));
            } else {
                //player left, fallback
                minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
            }
        } catch(Exception e) {
            //error, fallback
            minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
        }

        breastSlider.visible = plr.gender.canHaveBreasts();

        xOffsetBoobSlider.visible = plr.gender.canHaveBreasts();
        yOffsetBoobSlider.visible = plr.gender.canHaveBreasts();
        zOffsetBoobSlider.visible = plr.gender.canHaveBreasts();
        cleavageSlider.visible = plr.gender.canHaveBreasts();

        int x = this.width / 2;
        int y = this.height / 2;
        fill(m, x + 28, y - 64, x + 190, y + 79, 0x55000000);
        fill(m, x + 29, y - 63, x + 189, y - 50, 0x55000000);
        this.textRenderer.draw(m, title, x + 32, y - 60, 0xFFFFFF);


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
            plr.getBreasts().cleavage = ((float) this.cleavageSlider.getValue());
            preCleavage = (float) this.cleavageSlider.getValue();
            changedCleavageSlider = true;
        }
        super.render(m, f1, f2, f3);

        if(breastSlider.visible) this.textRenderer.draw(m, new TranslatableText("wildfire_gender.wardrobe.slider.breast_size").getString() + Math.round(plr.getBustSize() * 100) + "%", x + 36, y - 42, (this.breastSlider.isMouseOver(f1,  f2) || changedBreastSlider) ? 0xFFFF55: 0xFFFFFF);
        if(xOffsetBoobSlider.visible) this.textRenderer.draw(m, new TranslatableText("wildfire_gender.wardrobe.slider.separation", Math.round((Math.round(plr.getBreasts().xOffset * 100f) / 100f) * 10)), x + 36, y - 21, (this.xOffsetBoobSlider.isMouseOver(f1,  f2) || changedSliderX) ? 0xFFFF55: 0xFFFFFF);
        if(yOffsetBoobSlider.visible) this.textRenderer.draw(m, new TranslatableText("wildfire_gender.wardrobe.slider.height", Math.round((Math.round(plr.getBreasts().yOffset * 100f) / 100f) * 10)), x + 36, y, (this.yOffsetBoobSlider.isMouseOver(f1,  f2) || changedSliderY) ? 0xFFFF55: 0xFFFFFF);
        if(zOffsetBoobSlider.visible) this.textRenderer.draw(m, new TranslatableText("wildfire_gender.wardrobe.slider.depth", Math.round((Math.round(plr.getBreasts().zOffset * 100f) / 100f) * 10)), x + 36, y + 21, (this.zOffsetBoobSlider.isMouseOver(f1,  f2) || changedSliderZ) ? 0xFFFF55: 0xFFFFFF);
        if(cleavageSlider.visible) this.textRenderer.draw(m, new TranslatableText("wildfire_gender.wardrobe.slider.rotation", Math.round((Math.round(plr.getBreasts().cleavage * 100f) / 100f) * 100)), x + 36, y + 42, (this.cleavageSlider.isMouseOver(f1,  f2) || changedCleavageSlider) ? 0xFFFF55: 0xFFFFFF);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        GenderPlayer plr = getPlayer();
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
            plr.getBreasts().cleavage = (float) this.cleavageSlider.getValue();
            changedCleavageSlider = false;
            cleavageSlider.dragging = false;
            GenderPlayer.saveGenderInfo(plr);
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }
    public static void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate((double)x, (double)y, 1050.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0D, 0.0D, 800.0D);
        matrixStack2.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 160.0F;
        entity.setYaw(180.0F);
        entity.setPitch(0.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1f, matrixStack2, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
