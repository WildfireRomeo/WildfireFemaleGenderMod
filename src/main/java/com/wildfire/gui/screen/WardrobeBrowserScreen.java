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

import com.wildfire.main.GenderPlayer.Gender;
import com.wildfire.main.WildfireGender;
import java.util.UUID;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.GenderPlayer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WardrobeBrowserScreen extends BaseWildfireScreen {

	private ResourceLocation BACKGROUND;
	public static float modelRotation = 0.5F;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(Component.translatable("wildfire_gender.wardrobe.title"), parent, uuid);
	}

	@Override
  	public void init() {
	    int j = this.height / 2;

		GenderPlayer plr = getPlayer();

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 52, 158, 20, getGenderLabel(plr.getGender()), button -> {
			Gender gender = switch (plr.getGender()) {
				case MALE -> Gender.FEMALE;
				case FEMALE -> Gender.OTHER;
				case OTHER -> Gender.MALE;
			};
			if (plr.updateGender(gender)) {
				button.setMessage(getGenderLabel(gender));
				GenderPlayer.saveGenderInfo(plr);
			}
		}));

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 32, 158, 20, Component.translatable("wildfire_gender.appearance_settings.title").append("..."),
			button -> Minecraft.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID))));

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 12, 158, 20, Component.translatable("wildfire_gender.char_settings.title").append("..."),
			button -> Minecraft.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID))));

		this.addRenderableWidget(new WildfireButton(this.width / 2 + 111, j - 63, 9, 9, Component.translatable("wildfire_gender.label.exit"),
			button -> Minecraft.getInstance().setScreen(parent)));
	    
	    modelRotation = 0.6F;

	    this.BACKGROUND = new ResourceLocation(WildfireGender.MODID, "textures/gui/wardrobe_bg.png");
    
	    super.init();
  	}

	private Component getGenderLabel(Gender gender) {
		return Component.translatable("wildfire_gender.label.gender").append(" - ").append(gender.getDisplayName());
	}

  	@Override
	public void render(PoseStack m, int f1, int f2, float f3) {
		Minecraft minecraft = Minecraft.getInstance();
		GenderPlayer plr = getPlayer();
	    super.renderBackground(m);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.BACKGROUND);

	    int i = (this.width - 248) / 2;
	    int j = (this.height - 134) / 2;
		blit(m, i, j, 0, 0, 248, 156);

	    if(plr == null) return;


		int x = this.width / 2;
	    int y = this.height / 2;
	    
	    this.font.draw(m, title, x - 42, y - 62, 4473924);

	    modelRotation = 0.6f;


		try {
			RenderSystem.setShaderColor(1f, 1.0F, 1.0F, 1.0F);
		    int xP = this.width / 2 - 82;
		    int yP = this.height / 2 + 32;
		    Player ent = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);
		    if(ent != null) {
				drawEntityOnScreen(xP, yP, 45, (float)(xP) - f1, (float)(j + 75 - 40) - f2, this.minecraft.player);
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

	public static void drawEntityOnScreen(int pPosX, int pPosY, int pScale, float pMouseX, float pMouseY, LivingEntity pLivingEntity) {
		float f = (float)Math.atan((double)(pMouseX / 40.0F));
		float f1 = (float)Math.atan((double)(pMouseY / 40.0F));
		drawEntityOnScreenRaw(pPosX, pPosY, pScale, f, f1, pLivingEntity);
	}
	public static void drawEntityOnScreenRaw(int pPosX, int pPosY, int pScale, float angleXComponent, float angleYComponent, LivingEntity pLivingEntity) {
		float f = angleXComponent;
		float f1 = angleYComponent;
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((float)pPosX, (float)pPosY, 1050.0F);
		posestack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack posestack1 = new PoseStack();
		posestack1.translate(0.0F, 0.0F, 1000.0F);
		posestack1.scale((float)pScale, (float)pScale, (float)pScale);
		Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
		Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float)Math.PI / 180F));
		quaternionf.mul(quaternionf1);
		posestack1.mulPose(quaternionf);
		float f2 = pLivingEntity.yBodyRot;
		float f3 = pLivingEntity.getYRot();
		float f4 = pLivingEntity.getXRot();
		float f5 = pLivingEntity.yHeadRotO;
		float f6 = pLivingEntity.yHeadRot;
		pLivingEntity.yBodyRot = 180.0F + f * 20.0F;
		pLivingEntity.setYRot(180.0F + f * 40.0F);
		pLivingEntity.setXRot(-f1 * 20.0F);
		pLivingEntity.yHeadRot = pLivingEntity.getYRot();
		pLivingEntity.yHeadRotO = pLivingEntity.getYRot();
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternionf1.conjugate();
		entityrenderdispatcher.overrideCameraOrientation(quaternionf1);
		entityrenderdispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			entityrenderdispatcher.render(pLivingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880);
		});
		multibuffersource$buffersource.endBatch();
		entityrenderdispatcher.setRenderShadow(true);
		pLivingEntity.yBodyRot = f2;
		pLivingEntity.setYRot(f3);
		pLivingEntity.setXRot(f4);
		pLivingEntity.yHeadRotO = f5;
		pLivingEntity.yHeadRot = f6;
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}
}