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

import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;

import java.util.Calendar;
import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.entitydata.PlayerConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.main.WildfireHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class WardrobeBrowserScreen extends BaseWildfireScreen {
	private static final Identifier BACKGROUND_FEMALE = new Identifier(WildfireGender.MODID, "textures/gui/wardrobe_bg2.png");
	private static final Identifier BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/wardrobe_bg3.png");
	private static final Identifier TXTR_RIBBON = new Identifier(WildfireGender.MODID, "textures/bc_ribbon.png");
	private static final UUID CREATOR_UUID = UUID.fromString("23b6feed-2dfe-4f2e-9429-863fd4adb946");
	private static final boolean isBreastCancerAwarenessMonth = Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(Text.translatable("wildfire_gender.wardrobe.title"), parent, uuid);
	}

	@Override
  	public void init() {
	    int y = this.height / 2;
		PlayerConfig plr = getPlayer();

		this.addDrawableChild(new WildfireButton(this.width / 2 - 42, y - 52, 158, 20, getGenderLabel(plr.getGender()), button -> {
			Gender gender = switch (plr.getGender()) {
				case MALE -> Gender.FEMALE;
				case FEMALE -> Gender.OTHER;
				case OTHER -> Gender.MALE;
			};
			if (plr.updateGender(gender)) {
				button.setMessage(getGenderLabel(gender));
				PlayerConfig.saveGenderInfo(plr);
				clearAndInit();
			}
		}));

		if (plr.getGender().canHaveBreasts()) {
			this.addDrawableChild(new WildfireButton(this.width / 2 - 42, y - 32, 158, 20, Text.translatable("wildfire_gender.appearance_settings.title").append("..."),
					button -> MinecraftClient.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID))));
		}
		this.addDrawableChild(new WildfireButton(this.width / 2 - 42, y - (plr.getGender().canHaveBreasts() ? 12 : 32), 158, 20, Text.translatable("wildfire_gender.char_settings.title").append("..."),
				button -> MinecraftClient.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID))));

		this.addDrawableChild(new WildfireButton(this.width / 2 + 111, y - 63, 9, 9, Text.literal("X"),
			button -> MinecraftClient.getInstance().setScreen(parent)));

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

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		int x = this.width / 2;
	    int y = this.height / 2;
		ctx.drawText(textRenderer, title, x - 118, y - 62, 4473924, false);

		if(client != null && client.world != null) {
		    int xP = this.width / 2 - 82;
		    int yP = this.height / 2 + 40;
		    PlayerEntity ent = client.world.getPlayerByUuid(this.playerUUID);
		    if(ent != null) drawEntityOnScreen(xP, yP, 45, (xP - mouseX), (yP - 76 - mouseY), ent);
		}

		if(client != null && client.player != null) {
			boolean withCreator = client.player.networkHandler.getPlayerList().stream()
					.anyMatch((player) -> player.getProfile().getId().equals(CREATOR_UUID));
			if(withCreator) {
				int creatorY = y + 65;
				// move down so we don't overlap with the breast cancer awareness month banner
				if(isBreastCancerAwarenessMonth) creatorY += 30;
				WildfireHelper.drawCenteredText(ctx, this.textRenderer, Text.translatable("wildfire_gender.label.with_creator"), this.width / 2, creatorY, 0xFF00FF);
			}
		}

		if(isBreastCancerAwarenessMonth) {
			int bcaY = y - 45;
			ctx.fill(x - 159, bcaY + 106, x + 159, bcaY + 136, 0x55000000);
			ctx.drawTextWithShadow(textRenderer, Text.translatable("wildfire_gender.cancer_awareness.title").formatted(Formatting.BOLD, Formatting.ITALIC), this.width / 2 - 148, bcaY + 117, 0xFFFFFF);
			ctx.drawTexture(TXTR_RIBBON, x + 130, bcaY + 109, 26, 26, 0, 0, 20, 20, 20, 20);
		}
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