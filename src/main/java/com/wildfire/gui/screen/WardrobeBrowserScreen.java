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

import com.wildfire.main.GenderPlayer.Gender;
import com.wildfire.main.WildfireGender;
import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.GenderPlayer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class WardrobeBrowserScreen extends BaseWildfireScreen {
	private static final Identifier BACKGROUND_FEMALE = new Identifier(WildfireGender.MODID, "textures/gui/wardrobe_bg2.png");
	private static final Identifier BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/wardrobe_bg3.png");
	public static float modelRotation = 0.5F;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(Text.translatable("wildfire_gender.wardrobe.title"), parent, uuid);
	}

	@Override
  	public void init() {
	    int j = this.height / 2;
		GenderPlayer plr = getPlayer();

		this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 52, 158, 20, getGenderLabel(plr.getGender()), button -> {
			Gender gender = switch (plr.getGender()) {
				case MALE -> Gender.FEMALE;
				case FEMALE -> Gender.OTHER;
				case OTHER -> Gender.MALE;
			};
			if (plr.updateGender(gender)) {
				button.setMessage(getGenderLabel(gender));
				GenderPlayer.saveGenderInfo(plr);

				//re-render menu (re-open it)
				MinecraftClient.getInstance().setScreen(new WardrobeBrowserScreen(null, playerUUID));
			}
		}));

		if(plr.getGender().canHaveBreasts()) {
			this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 32, 158, 20, Text.translatable("wildfire_gender.appearance_settings.title").append("..."),
					button -> MinecraftClient.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID))));
			this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 12, 158, 20, Text.translatable("wildfire_gender.char_settings.title").append("..."),
					button -> MinecraftClient.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID))));
		} else {
			this.addDrawableChild(new WildfireButton(this.width / 2 - 42, j - 32, 158, 20, Text.translatable("wildfire_gender.char_settings.title").append("..."),
					button -> MinecraftClient.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID))));
		}

		this.addDrawableChild(new WildfireButton(this.width / 2 + 111, j - 63, 9, 9, Text.translatable("wildfire_gender.label.exit"),
			button -> MinecraftClient.getInstance().setScreen(parent)));
	    
	    modelRotation = 0.6F;

	    super.init();
  	}

	private Text getGenderLabel(Gender gender) {
		return Text.translatable("wildfire_gender.label.gender").append(" - ").append(gender.getDisplayName());
	}

	@Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.renderBackground(ctx, mouseX, mouseY, delta);
		Identifier backgroundTexture = getPlayer().getGender().canHaveBreasts() ? BACKGROUND_FEMALE : BACKGROUND;
		ctx.drawTexture(backgroundTexture, (this.width - 248) / 2, (this.height - 134) / 2, 0, 0, 248, 156);
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		MinecraftClient minecraft = MinecraftClient.getInstance();
		GenderPlayer plr = getPlayer();

	    if(plr == null) return;

		int x = this.width / 2;
	    int y = this.height / 2;
		modelRotation = 0.6f;

		ctx.drawText(textRenderer, title, x - 118, y - 62, 4473924, false);
		try {
			RenderSystem.setShaderColor(1f, 1.0F, 1.0F, 1.0F);
		    int xP = this.width / 2 - 82;
		    int yP = this.height / 2 + 40;
		    PlayerEntity ent = minecraft.world.getPlayerByUuid(this.playerUUID);
		    if(ent != null) {
		    	drawEntityOnScreen(xP, yP, 45, (xP - mouseX), (yP - 76 - mouseY), minecraft.world.getPlayerByUuid(this.playerUUID));
		    }
		} catch(Exception e) {}
	}

	public static void drawEntityOnScreen(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		float f = (float)Math.atan(mouseX / 40.0F);
		float g = (float)Math.atan(mouseY / 40.0F);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.translate((float)x, (float)y, 1050.0F);
		matrixStack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		MatrixStack matrixStack2 = new MatrixStack();
		matrixStack2.translate(0.0F, 0.0F, 1000.0F);
		matrixStack2.scale((float)size, (float)size, (float)size);
		Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
		Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
		quaternionf.mul(quaternionf2);
		matrixStack2.multiply(quaternionf);
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
		quaternionf2.conjugate();
		entityRenderDispatcher.setRotation(quaternionf2);
		entityRenderDispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		RenderSystem.runAsFancy(() -> {
			entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
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