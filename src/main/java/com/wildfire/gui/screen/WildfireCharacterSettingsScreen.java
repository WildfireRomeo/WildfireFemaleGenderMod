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

import com.wildfire.main.WildfireGender;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class WildfireCharacterSettingsScreen extends BaseWildfireScreen {

    private static final Component ENABLED = new TranslatableComponent("wildfire_gender.label.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED = new TranslatableComponent("wildfire_gender.label.disabled").withStyle(ChatFormatting.RED);

    private WildfireSlider bounceSlider, floppySlider;
    private ResourceLocation BACKGROUND;
    private float preBounceMult = 0f;
    private float preFloppyMult = 0f;
    private boolean changedSlider = false, changedFloppySlider = false;
    private int yPos = 0;
    private boolean enablePhysics, enablePhysicsArmor, enableHurtSounds, enableShowInArmor;
    private float bounceMult, floppyMult;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(new TranslatableComponent("wildfire_gender.char_settings.title"), parent, uuid);
    }

    @Override
    public void init() {
        GenderPlayer aPlr = getPlayer();

        int x = this.width / 2;
        int y = this.height / 2;

        yPos = y - 47;
        enablePhysics = aPlr.hasBreastPhysics;
        enablePhysicsArmor = aPlr.hasArmorBreastPhysics;
        enableShowInArmor = aPlr.showBreastsInArmor;
        enableHurtSounds = aPlr.hurtSounds;
        bounceMult = aPlr.bounceMultiplier;
        floppyMult = aPlr.floppyMultiplier;

        this.addRenderableWidget(new WildfireButton(this.width / 2 - 156/2-1, yPos, 157, 20,
              new TranslatableComponent("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED), button -> {
            enablePhysics ^= true;
            button.setMessage(new TranslatableComponent("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED));
            aPlr.hasBreastPhysics = enablePhysics;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, new TranslatableComponent("wildfire_gender.tooltip.breast_physics"), mouseX, mouseY)));

        this.addRenderableWidget(new WildfireButton(this.width / 2 - 156/2-1, yPos + 20, 157, 20,
              new TranslatableComponent("wildfire_gender.char_settings.armor_physics", enablePhysicsArmor ? ENABLED : DISABLED), button -> {
            enablePhysicsArmor ^= true;
            button.setMessage(new TranslatableComponent("wildfire_gender.char_settings.armor_physics", enablePhysicsArmor ? ENABLED : DISABLED));
            aPlr.hasArmorBreastPhysics = enablePhysicsArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, new TranslatableComponent("wildfire_gender.tooltip.armor_physics"), mouseX, mouseY)));

        this.addRenderableWidget(new WildfireButton(this.width / 2 - 156/2-1, yPos + 40, 157, 20,
              new TranslatableComponent("wildfire_gender.char_settings.hide_in_armor", enableShowInArmor ? DISABLED : ENABLED), button -> {
            enableShowInArmor ^= true;
            button.setMessage(new TranslatableComponent("wildfire_gender.char_settings.hide_in_armor", enableShowInArmor ? DISABLED : ENABLED));
            aPlr.showBreastsInArmor = enableShowInArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, new TranslatableComponent("wildfire_gender.tooltip.hide_in_armor"), mouseX, mouseY)));

        this.addRenderableWidget(this.bounceSlider = new WildfireSlider(this.width / 2 - 160/2-1, yPos + 60, 160, 22, TextComponent.EMPTY, title, 0.0D, 1.0D, bounceMult, false,  false, button -> {
        }, slider -> {
        }));

        this.addRenderableWidget(this.floppySlider = new WildfireSlider(this.width / 2 - 160/2-1, yPos + 80, 160, 22, TextComponent.EMPTY, title, 0.0D, 1.0D, floppyMult, false,  false, button -> {
        }, slider -> {
        }));


        this.addRenderableWidget((new WildfireButton(this.width / 2 - 156/2-1, yPos + 100, 157, 20,
              new TranslatableComponent("wildfire_gender.char_settings.hurt_sounds", enableHurtSounds ? ENABLED : DISABLED), button -> {
            enableHurtSounds ^= true;
            button.setMessage(new TranslatableComponent("wildfire_gender.char_settings.hurt_sounds", enableHurtSounds ? ENABLED : DISABLED));
            aPlr.hurtSounds = enableHurtSounds;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> {
            //List<TextComponent> list = new ArrayList<>();
            //list.add(new TextComponent("Enables Custom Hurt Sounds."));
            //list.add(new TextComponent(ChatFormatting.RED + "Mod Needed On Server To Work!"));
            //RenderSystem.disableDepthTest();
            //renderTooltip(matrices, list, mouseX, mouseY);
            //RenderSystem.enableDepthTest();
        })));

        this.addRenderableWidget(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, new TranslatableComponent("wildfire_gender.label.exit"),
              button -> Minecraft.getInstance().setScreen(parent)));

        this.BACKGROUND = new ResourceLocation(WildfireGender.MODID, "textures/gui/settings_bg.png");

        super.init();
    }

    @Override
    public void render(@Nonnull PoseStack m, int f1, int f2, float f3) {
        super.renderBackground(m);
        Player plrEntity = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(this.BACKGROUND != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.BACKGROUND);
        }
        int i = (this.width - 172) / 2;
        int j = (this.height - 124) / 2;
        blit(m, i, j, 0, 0, 172, 144);

        int x = this.width / 2;
        int y = this.height / 2;

        this.font.draw(m, title, x - 79, yPos - 10, 4473924);

        //modelRotation = (float)this.rotateSlider.getValue();
        if(preBounceMult != (float) this.bounceSlider.getValue()) {
            bounceMult = (float) this.bounceSlider.getValue();
            preBounceMult = (float) this.bounceSlider.getValue();
            changedSlider = true;
        }
        if(preFloppyMult != (float) this.floppySlider.getValue()) {
            floppyMult = (float) this.floppySlider.getValue();
            preFloppyMult = (float) this.floppySlider.getValue();
            changedFloppySlider = true;
        }

        super.render(m, f1, f2, f3);

        if(plrEntity != null) {
            Screen.drawCenteredString(m, this.font, plrEntity.getDisplayName(), x, yPos - 30, 0xFFFFFF);
        }

        float bounceText = (bounceMult * 3);
        if (Math.round(bounceText * 10) / 10f == 3) {
            this.font.draw(m, new TranslatableComponent("wildfire_gender.slider.max_bounce"), x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else if (Math.round(bounceText * 100) / 100f == 0) {
            this.font.draw(m, new TranslatableComponent("wildfire_gender.slider.min_bounce"), x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else {
            this.font.draw(m, new TranslatableComponent("wildfire_gender.slider.bounce", Math.round(bounceText * 10) / 10f), x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        }
        this.font.draw(m, new TranslatableComponent("wildfire_gender.slider.floppy", Math.round(floppyMult * 100)), x - 72, yPos+87, (this.floppySlider.isMouseOver(f1,  f2) || changedFloppySlider) ? 0xFFFF55: 0xFFFFFF);

        if(Math.round(bounceText * 10) / 10f > 1f) {
            Screen.drawCenteredString(m, font, new TranslatableComponent("wildfire_gender.tooltip.bounce_warning").withStyle(ChatFormatting.ITALIC), x, y+90, 0xFF6666);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        GenderPlayer aPlr = getPlayer();

        if(changedSlider) {
            bounceMult = (float) this.bounceSlider.getValue();
            aPlr.bounceMultiplier = bounceMult;
            GenderPlayer.saveGenderInfo(aPlr);
            changedSlider = false;
            bounceSlider.dragging = false;
        }
        if(changedFloppySlider) {
            floppyMult = (float) this.floppySlider.getValue();
            aPlr.floppyMultiplier =  floppyMult;
            GenderPlayer.saveGenderInfo(aPlr);
            changedFloppySlider = false;
            floppySlider.dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }

}