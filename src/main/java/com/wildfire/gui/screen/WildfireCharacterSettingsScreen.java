package com.wildfire.gui.screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.Screen;

public class WildfireCharacterSettingsScreen extends Screen {


    private WildfireSlider bounceSlider, floppySlider;
    private Identifier BACKGROUND;
    private float preBounceMult = 0f;
    private float preFloppyMult = 0f;
    private boolean changedSlider = false, changedFloppySlider = false;

    private Screen parent;
    private UUID playerUUID;

    protected WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
        super(new TranslatableText("Gender Settings"));
        this.parent = parent;
        this.playerUUID = uuid;
    }


    public boolean isPauseScreen() { return false; }

    private int yPos = 0;

    boolean enablePhysics, enablePhysicsArmor, enableHurtSounds, enableShowInArmor;
    float bounceMult, floppyMult;
    public void init() {
        MinecraftClient m = MinecraftClient.getInstance();
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

        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos, 157, 20, new LiteralText("Breast Physics: " + (enablePhysics ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")), button -> {
            enablePhysics ^= true;
            button.setMessage(new LiteralText("Breast Physics: " + (enablePhysics ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")));
            aPlr.breast_physics = enablePhysics;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                renderTooltip(matrices, new TranslatableText("wildfire_gender.tooltip.breast_physics"), mouseX, mouseY);
            }
        }));

        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos + 20, 157, 20, new LiteralText("Armor Physics: " + (enablePhysicsArmor ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")), button -> {
            enablePhysicsArmor ^= true;
            button.setMessage(new LiteralText("Armor Physics: " + (enablePhysicsArmor ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")));
            aPlr.breast_physics_armor = enablePhysicsArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                renderTooltip(matrices, new LiteralText("Enables Breast Physics With Armor Equipped"), mouseX, mouseY);
            }
        }));

        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos + 40, 157, 20, new LiteralText("Hide In Armor: " + (!enableShowInArmor ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")), button -> {
            enableShowInArmor ^= true;
            button.setMessage(new LiteralText("Hide In Armor: " + (!enableShowInArmor ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")));
            aPlr.show_in_armor = enableShowInArmor;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                renderTooltip(matrices, new LiteralText("Hide Breast Model When Wearing Armor"), mouseX, mouseY);
            }
        }));

        this.addDrawableChild(this.bounceSlider = new WildfireSlider(this.width / 2 - 160/2 + 1, yPos + 60, 157, 20, title, 0.0D, 1.0D, bounceMult) {
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



        this.addDrawableChild(this.floppySlider = new WildfireSlider(this.width / 2 - 160/2 + 1, yPos + 80, 157, 20, title, 0.0D, 1.0D, floppyMult) {
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


        this.addDrawableChild(new WildfireButton(this.width / 2 - 156/2-1, yPos + 100, 157, 20, new LiteralText("Female Hurt Sounds: " + (enableHurtSounds ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")), button -> {
            enableHurtSounds ^= true;
            button.setMessage(new LiteralText("Female Hurt Sounds: " + (enableHurtSounds ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled")));
            aPlr.hurtSounds = enableHurtSounds;
            GenderPlayer.saveGenderInfo(aPlr);
        }, new ButtonWidget.TooltipSupplier() {
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                List<Text> list = new ArrayList<Text>();
                list.add(new LiteralText("Enables Custom Hurt Sounds."));
                list.add(new LiteralText(Formatting.RED + "Mod Needed On Server To Work!"));
                RenderSystem.disableDepthTest();
                renderTooltip(matrices, list, mouseX, mouseY);
                RenderSystem.enableDepthTest();
            }
        }));

        this.addDrawableChild(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, new LiteralText("X"), button -> {
            MinecraftClient.getInstance().setScreen(parent);
        }));

        this.BACKGROUND = new Identifier("wildfire_gender", "textures/gui/settings_bg.png");

        super.init();
    }

    public void render(MatrixStack m, int f1, int f2, float f3) {
        super.renderBackground(m);
        PlayerEntity plrEntity = MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(this.BACKGROUND != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.BACKGROUND);
        }
        int i = (this.width - 172) / 2;
        int j = (this.height - 124) / 2;
        drawTexture(m, i, j, 0, 0, 172, 144);

        int x = this.width / 2;
        int y = this.height / 2;

        this.textRenderer.draw(m, new TranslatableText("wildfire_gender.char_settings.title"), x - 79, yPos - 10, 4473924);

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
            Screen.drawCenteredText(m, this.textRenderer, plrEntity.getDisplayName().getString(), x, yPos - 30, 0xFFFFFF);
        }

        float bounceText = (bounceMult * 3);
        if (Math.round(bounceText * 10) / 10f == 3) {
            this.textRenderer.draw(m, "#Anime Breast Physics!!!", x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else if (Math.round(bounceText * 100) / 100f == 0) {
            this.textRenderer.draw(m, "Why Are Physics Even On?", x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        } else {
            this.textRenderer.draw(m, "Bounce Intensity: " + Math.round(bounceText * 10) / 10f + "x", x - 72, yPos+67, (this.bounceSlider.isMouseOver(f1,  f2) || changedSlider) ? 0xFFFF55: 0xFFFFFF);
        }
        this.textRenderer.draw(m, "Breast Momentum: " + Math.round(floppyMult * 100) + "%", x - 72, yPos+87, (this.floppySlider.isMouseOver(f1,  f2) || changedFloppySlider) ? 0xFFFF55: 0xFFFFFF);

        if(Math.round(bounceText * 10) / 10f > 1f) {
            Screen.drawCenteredText(m, textRenderer, Formatting.ITALIC + "Setting 'Bounce Intensity' to a high value will look very unnatural!", x, y+90, 0xFF6666);
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        MinecraftClient m = MinecraftClient.getInstance();
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