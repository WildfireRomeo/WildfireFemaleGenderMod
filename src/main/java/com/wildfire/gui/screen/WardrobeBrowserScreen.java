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

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.GenderPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class WardrobeBrowserScreen extends Screen {
	

	private Identifier BACKGROUND;
	public static float modelRotation = 0.5F;

	private UUID playerUUID;
	private Screen parent;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(new TranslatableText("wildfire_gender.settings.title"));
		this.playerUUID = uuid;
		this.parent = parent;
	}
  
 
	public boolean shouldPause() { return false; }

  
  	public void init() {
	  	MinecraftClient m = MinecraftClient.getInstance();
	    int j = this.height / 2;

	    
	    GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());

	    LiteralText genderString = new LiteralText(new TranslatableText("wildfire_gender.label.gender").getString() + " - ");

	    if(plr.gender == 0) {
	    	genderString.append(Formatting.LIGHT_PURPLE + new TranslatableText("wildfire_gender.label.female").getString());
		} else if(plr.gender == 1) {
			genderString.append(Formatting.BLUE + new TranslatableText("wildfire_gender.label.male").getString());
		} else if(plr.gender == 2) {
			genderString.append(Formatting.GREEN + new TranslatableText("wildfire_gender.label.other").getString());
		}
		this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 52, 158, 20, genderString, button -> {
			if(plr.gender == 0) {
				plr.gender = 2;
			} else if(plr.gender == 1) {
				plr.gender = 0;
			} else {
				plr.gender = 1;
			}

			LiteralText btnString = new LiteralText(new TranslatableText("wildfire_gender.label.gender").getString() + " - ");

			if(plr.gender == 0) {
				btnString.append(Formatting.LIGHT_PURPLE +  new TranslatableText("wildfire_gender.label.female").getString());
			} else if(plr.gender == 1) {
				btnString.append(Formatting.BLUE + new TranslatableText("wildfire_gender.label.male").getString());
			} else if(plr.gender == 2) {
				btnString.append(Formatting.GREEN + new TranslatableText("wildfire_gender.label.other").getString());
			}

			button.setMessage(btnString);
			GenderPlayer.saveGenderInfo(plr);
		}));

		this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 32, 158, 20, new LiteralText("Appearance Settings..."), button -> {
			MinecraftClient.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID));
		}));

		this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 12, 158, 20, new LiteralText("Character Settings..."), button -> {
			MinecraftClient.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID));
		}));

		this.addDrawableChild(new WildfireButton(this.width / 2 + 111, j - 63, 9, 9, new LiteralText("X"), button -> {
			MinecraftClient.getInstance().setScreen(parent);
		}));
	    
	    modelRotation = 0.6F;

	    this.BACKGROUND = new Identifier("wildfire_gender", "textures/gui/wardrobe_bg.png");
    
	    super.init();
  	}

  	public void render(MatrixStack m, int f1, int f2, float f3) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
	    GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());
	    super.renderBackground(m);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.BACKGROUND);

	    int i = (this.width - 248) / 2;
	    int j = (this.height - 134) / 2;
		drawTexture(m, i, j, 0, 0, 248, 156);

	    if(plr == null) return;


		int x = this.width / 2;
	    int y = this.height / 2;
	    
	    this.textRenderer.draw(m, new TranslatableText("wildfire_gender.wardrobe.title"), x - 42, y - 62, 4473924);

	    modelRotation = 0.6f;

		try {
			RenderSystem.setShaderColor(1f, 1.0F, 1.0F, 1.0F);
		    int xP = this.width / 2 - 82;
		    int yP = this.height / 2 + 32;
		    PlayerEntity ent = MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID);
		    if(ent != null) {
		    	drawEntity(xP, yP, 45, (xP - f1), (yP - 76 - f2), MinecraftClient.getInstance().world.getPlayerByUuid(this.playerUUID));
		    } else {
				//player left, fallback
				minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
		    }
		} catch(Exception e) {
			//error, fallback
			minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
		}
	    super.render(m, f1, f2, f3);
	}
  	
  	public boolean mouseReleased(double mouseX, double mouseY, int state) {
	    GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());
	  
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
		matrixStack2.translate(0.0D, 0.0D, 1000.0D);
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
		entity.bodyYaw = 180.0F + f * 20.0F;
		entity.setYaw(180.0F + f * 40.0F);
		entity.setPitch(-g * 20.0F);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();
		DiffuseLighting.method_34742();
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
		quaternion2.conjugate();
		entityRenderDispatcher.setRotation(quaternion2);
		entityRenderDispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		RenderSystem.runAsFancy(() -> {
			entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
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