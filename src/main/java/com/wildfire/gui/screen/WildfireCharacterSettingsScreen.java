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
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class WildfireCharacterSettingsScreen extends Screen {


    private WildfireSlider bounceSlider, floppySlider;
    private ResourceLocation BACKGROUND;
    private float preBounceMult = 0f;
    private float preFloppyMult = 0f;
    private boolean changedSlider = false, changedFloppySlider = false;

    private Screen parent;
    private UUID playerUUID;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(new TranslatableComponent("Gender Settings"));
        this.parent = parent;
        this.playerUUID = uuid;
    }


    @Override
    public boolean isPauseScreen() { return false; }

    private int yPos = 0;

    boolean enablePhysics, enablePhysicsArmor, enableHurtSounds, enableShowInArmor;
    float bounceMult, floppyMult;

    @Override
    public void init() {
        Minecraft m = Minecraft.getInstance();
        GenderPlayer aPlr = WildfireGender.getPlayerByName(this.playerUUID.toString());

        int x = this.width / 2;
        int y = this.height / 2;

        yPos = y - 47;
        enablePhysics = aPlr.breast_physics;
        enablePhysicsArmor = aPlr.breast_physics_armor;
        enableShowInArmor = aPlr.show_in_armor;
        enableHurtSounds = aPlr.hurtSounds;
        bounceMult = aPlr.bounceMultiplier;
        floppyMult = aPlr.floppyMultiplier;

        this.addRenderableWidget(new WildfireButton(this.width / 2 - 156/2-1, yPos, 157, 20, new TextComponent("Breast Physics: " + (enablePhysics ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")), button -> {
            enablePhysics ^= true;
            button.setMessage(new TextComponent("Breast Physics: " + (enablePhysics ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")));
            aPlr.breast_physics = enablePhysics;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, new TranslatableComponent("wildfire_gender.tooltip.breast_physics"), mouseX, mouseY)));

        this.addRenderableWidget(new WildfireButton(this.width / 2 - 156/2-1, yPos + 20, 157, 20, new TextComponent("Armor Physics: " + (enablePhysicsArmor ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")), button -> {
            enablePhysicsArmor ^= true;
            button.setMessage(new TextComponent("Armor Physics: " + (enablePhysicsArmor ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")));
            aPlr.breast_physics_armor = enablePhysicsArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, new TextComponent("Enables Breast Physics With Armor Equipped"), mouseX, mouseY)));

        this.addRenderableWidget(new WildfireButton(this.width / 2 - 156/2-1, yPos + 40, 157, 20, new TextComponent("Hide In Armor: " + (!enableShowInArmor ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")), button -> {
            enableShowInArmor ^= true;
            button.setMessage(new TextComponent("Hide In Armor: " + (!enableShowInArmor ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")));
            aPlr.show_in_armor = enableShowInArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, new TextComponent("Hide Breast Model When Wearing Armor"), mouseX, mouseY)));

        this.addRenderableWidget(this.bounceSlider = new WildfireSlider(this.width / 2 - 160/2-1, yPos + 60, 160, 22, new TextComponent(""), title, 0.0D, 1.0D, bounceMult, false,  false, button -> {
        }, slider -> {
        }));

        this.addRenderableWidget(this.floppySlider = new WildfireSlider(this.width / 2 - 160/2-1, yPos + 80, 160, 22, new TextComponent(""), title, 0.0D, 1.0D, floppyMult, false,  false, button -> {
        }, slider -> {
        }));


        this.addRenderableWidget((new WildfireButton(this.width / 2 - 156/2-1, yPos + 100, 157, 20, new TextComponent("Female Hurt Sounds: " + (enableHurtSounds ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")), button -> {
            enableHurtSounds ^= true;
            button.setMessage(new TextComponent("Female Hurt Sounds: " + (enableHurtSounds ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled")));
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

        this.addRenderableWidget(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, new TextComponent("X"),
              button -> Minecraft.getInstance().setScreen(parent)));

        this.BACKGROUND = new ResourceLocation("wildfire_gender", "textures/gui/settings_bg.png");

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

        this.font.draw(m, new TranslatableComponent("wildfire_gender.char_settings.title"), x - 79, yPos - 10, 4473924);

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
            Screen.drawCenteredString(m, this.font, plrEntity.getDisplayName().getString(), x, yPos - 30, 0xFFFFFF);
        }

        float bounceText = (bounceMult * 3);
        if (Math.round(bounceText * 10) / 10f == 3) {
            this.font.draw(m, "#Anime Breast Physics!!!", x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else if (Math.round(bounceText * 100) / 100f == 0) {
            this.font.draw(m, "Why Are Physics Even On?", x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else {
            this.font.draw(m, "Bounce Intensity: " + Math.round(bounceText * 10) / 10f + "x", x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        }
        this.font.draw(m, "Breast Momentum: " + Math.round(floppyMult * 100) + "%", x - 72, yPos+87, (this.floppySlider.isMouseOver(f1,  f2) || changedFloppySlider) ? 0xFFFF55: 0xFFFFFF);

        if(Math.round(bounceText * 10) / 10f > 1f) {
            Screen.drawCenteredString(m, font, ChatFormatting.ITALIC + "Setting 'Bounce Intensity' to a high value will look very unnatural!", x, y+90, 0xFF6666);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        Minecraft m = Minecraft.getInstance();
        GenderPlayer aPlr = WildfireGender.getPlayerByName(this.playerUUID.toString());

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